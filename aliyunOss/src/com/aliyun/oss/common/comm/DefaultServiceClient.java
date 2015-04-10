/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.ExceptionFactory;
import com.aliyun.oss.common.utils.HttpUtil;


/**
 * The default implementation of <code>ServiceClient</code>.
 */
public class DefaultServiceClient extends ServiceClient {

    private HttpClient httpClient;

    public DefaultServiceClient(ClientConfiguration config) {
        super(config);
        httpClient = new HttpFactory().createHttpClient(config);
    }

    @Override
    public ResponseMessage sendRequestCore(ServiceClient.Request request, ExecutionContext context)
            throws IOException{
        assert request != null && context != null;
        
        // When we release connections, the connection manager leaves them
        // open so they can be reused.  We want to close out any idle
        // connections so that they don't sit around in CLOSE_WAIT.
        httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);

        HttpRequestBase httpRequest = new HttpFactory().createHttpRequest(request, context);

        // Execute request, make the exception to the standard WebException
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpRequest);
        } catch (IOException ex) {
            throw ExceptionFactory.createNetworkException(ex);
        }

        // Build result
        ResponseMessage result = new ResponseMessage();
        result.setUrl(request.getUri());
        if (response.getStatusLine() != null){
            result.setStatusCode(response.getStatusLine().getStatusCode());
        }
        if (response.getEntity() != null){
            result.setContent(response.getEntity().getContent());
        }
        // fill in headers
        Header[] headers = response.getAllHeaders();
        Map<String, String> resultHeaders = new HashMap<String, String>();
        for(int i = 0; i < headers.length; i++){
            Header h = headers[i];
            resultHeaders.put(h.getName(), h.getValue());
        }
        HttpUtil.convertHeaderCharsetFromIso88591(resultHeaders);
        result.setHeaders(resultHeaders);

        return result;
    }
    
    private static class DefaultRetryStrategy extends RetryStrategy {
        
        @Override
        public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
            if (ex instanceof ClientException) {
                String errorCode = ((ClientException) ex).getErrorCode();
                if (errorCode.equals(ClientErrorCode.CONNECTION_TIMEOUT)
                        || errorCode.equals(ClientErrorCode.SOCKET_TIMEOUT)) {
                    return true;
                }
            }
            
            if (response != null) {
                int statusCode = response.getStatusCode();
                if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR ||
                    statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    protected RetryStrategy getDefaultRetryStrategy() {
        return new DefaultRetryStrategy();
    }
}
