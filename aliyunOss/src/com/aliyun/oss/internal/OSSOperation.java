/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;

import java.net.URI;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.auth.RequestSigner;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.RetryStrategy;
import com.aliyun.oss.common.comm.ServiceClient;

/**
 * Base class for classes doing OSS operations.
 */
public abstract class OSSOperation {
    private URI endpoint;
    private ServiceCredentials credentials;
    private ServiceClient client;

    protected OSSOperation(URI endpoint, ServiceClient client, ServiceCredentials cred) {
        assert (endpoint != null && client != null && cred != null);

        this.endpoint = endpoint;
        this.client = client;
        this.credentials = cred;
    }
    
    public URI getEndpoint() {
        return endpoint;
    }
    
    protected ServiceClient getInnerClient() {
    	return this.client;
    }

    /**
     * Invokes a request.
     * @param request
     *          Request message to be sent.
     * @param context
     *          Requset context.
     * @return
     *          Response message.
     * @throws OSSException
     *          On service returns an error.
     * @throws ClientException
     *          On client request fails.
     */    
    protected ResponseMessage send(RequestMessage request, 
            ExecutionContext context) throws OSSException, ClientException {
        return send(request, context, false);
    }
    
    protected ResponseMessage send(RequestMessage request, 
                    ExecutionContext context, boolean keepResponseOpen) throws OSSException, ClientException{
        try {
            ResponseMessage response = client.sendRequest(request, context);
            if (!keepResponseOpen) {
                safeCloseResponse(response);
            }
            return response;
        } catch (ServiceException e) {
            assert (e instanceof OSSException);
            throw (OSSException)e;
        }
    }
    

    private static RequestSigner createSigner(HttpMethod method, String bucket,
            String key, ServiceCredentials credentials){
        String resourcePath = "/" +
                ((bucket != null) ? bucket : "") +
                ((key != null ? "/" + key : ""));
        
        // Hacked. the sign path is /bucket/key for two-level-domain mode
        // but /bucket/key/ for the three-level-domain mode.
        if (bucket != null && key == null) {
            resourcePath = resourcePath + "/";
        }
        
        return new OSSRequestSigner(method.toString(), resourcePath, credentials);
    }
    
    protected ExecutionContext createDefaultContext(HttpMethod method, String bucket, String key) {
        ExecutionContext context = new ExecutionContext();
        context.setCharset(OSSConstants.DEFAULT_CHARSET_NAME);
        context.setSigner(createSigner(method, bucket, key, credentials));
        context.getResponseHandlers().add(new OSSErrorResponseHandler());
        if (method == HttpMethod.POST) {
            // Non Idempotent operation
            context.setRetryStrategy(new RetryStrategy() {             
                @Override
                public boolean shouldRetry(Exception ex, RequestMessage request,
                        ResponseMessage response, int retries) {
                    return false;
                }
            });
        } 
        return context;
    }
    
    protected ExecutionContext createDefaultContext(HttpMethod method, String bucketName) {
        return this.createDefaultContext(method, bucketName, null);
    }
    
    protected ExecutionContext createDefaultContext(HttpMethod method) {
        return this.createDefaultContext(method, null, null);        
    }

}
