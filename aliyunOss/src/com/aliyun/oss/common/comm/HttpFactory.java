/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.internal.OSSConstants;

/**
 * The factory to create HTTP-related objects.
 */
class HttpFactory {

    /**
     * Creates a HttpClient instance.
     * @param config
     *          Client configuration.
     * @param context
     *          Execution context.
     * @return HttpClient instance.
     */
    public HttpClient createHttpClient(ClientConfiguration config){
        // Set HTTP params.
        HttpParams httpParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent(httpParams, config.getUserAgent());
        HttpConnectionParams.setConnectionTimeout(httpParams, config.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpParams, config.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);

        // Use thread-safe connection manager.
        ThreadSafeClientConnManager connMgr = createThreadSafeClientConnManager(config);
        DefaultHttpClient httpClient = new DefaultHttpClient(connMgr, httpParams);

        /*
         * If SSL cert checking for endpoints has been explicitly disabled,
         * register a new scheme for HTTPS that won't cause self-signed certs to
         * error out.
         */
        if (System.getProperty("com.aliyun.openservices.disableCertChecking") != null) {
            Scheme sch = new Scheme("https", 443, getSSLSocketFactory());
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }

        // Set proxy if set.
        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();

        if (proxyHost != null && proxyPort > 0){
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();

            if (proxyUsername != null && proxyPassword != null){
                String proxyDomain = config.getProxyDomain();
                String proxyWorkstation = config.getProxyWorkstation();

                httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
            }
        }

        return httpClient;
    }

    private ThreadSafeClientConnManager createThreadSafeClientConnManager(
            ClientConfiguration clientConfig){
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager();
        connMgr.setDefaultMaxPerRoute(clientConfig.getMaxConnections());
        connMgr.setMaxTotal(clientConfig.getMaxConnections());

        return connMgr;
    }

    /**
     * Creates a HttpRequestBase instance.
     * @param request
     *          Request message.
     * @return HttpRequestBase instance.
     */
    public HttpRequestBase createHttpRequest(ServiceClient.Request request, ExecutionContext context){

        String uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpRequestBase httpRequest;
        if (method == HttpMethod.POST){
            // POST
            HttpPost postMethod = new HttpPost(uri);

            if (request.getContent() != null){
                postMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = postMethod;
        } else if (method == HttpMethod.PUT){
            // PUT
            HttpPut putMethod = new HttpPut(uri);

            if (request.getContent() != null){
                if (request.isUseChunkEncoding()) {
                    putMethod.setEntity(buildChunkedEntity(request));
                } else {
                	putMethod.setEntity(new RepeatableInputStreamEntity(request));
                }
            }

            httpRequest = putMethod;
        } else if (method == HttpMethod.GET){
            // GET
            httpRequest = new HttpGet(uri);
        } else if (method == HttpMethod.DELETE){
            // DELETE
            httpRequest = new HttpDelete(uri);
        } else if (method == HttpMethod.HEAD){
            httpRequest = new HttpHead(uri);
        } else if (method == HttpMethod.OPTIONS){
            httpRequest = new HttpOptions(uri);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported HTTP method：%s.", request.getMethod().toString()));
        }

        configureRequestHeaders(request, context, httpRequest);

        return httpRequest;
    }
    
    private HttpEntity buildChunkedEntity(ServiceClient.Request request) {
//    	InputStream wrappedStream = new ChunkedUploadStream(request.getContent(), 
//    			OSSConstants.DEFAULT_BUFFER_SIZE);
    	InputStreamEntity requestEntity = new InputStreamEntity(request.getContent(), -1);
        if (!request.isUseUrlSignature()) {
        	requestEntity.setContentType(OSSConstants.DEFAULT_OBJECT_CONTENT_TYPE);
        }
        requestEntity.setChunked(true);
        return requestEntity;
    }

    private void configureRequestHeaders(ServiceClient.Request request, ExecutionContext context, HttpRequestBase httpRequest){
        // Copy headers in the request message to the HTTP request
        for(Entry<String, String> entry : request.getHeaders().entrySet()){
            // HttpClient fills in the Content-Length,
            // and complains if add it again, so skip it as well as the Host header.
            if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
                    entry.getKey().equalsIgnoreCase(HttpHeaders.HOST)){
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }

        // Set content type and encoding
        if (!request.isUseUrlSignature() && 
        		(httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE) == null ||
                httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE).length == 0)) {
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,
                    "application/x-www-form-urlencoded; " +
                            "charset=" + context.getCharset().toLowerCase());
        }
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null; 
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, null);
            SSLSocketFactory ssf =
                    new SSLSocketFactory(sslcontext,
                            SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
