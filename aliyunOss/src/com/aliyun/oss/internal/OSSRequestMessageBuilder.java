/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.DateUtil;

public class OSSRequestMessageBuilder {
    
    private URI endpoint;
    private HttpMethod method = HttpMethod.GET;
    private String bucket;
    private String key;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> parameters = new LinkedHashMap<String, String>();
    private InputStream inputInstream;
    private long inputSize = 0; 
    private ServiceClient innerClient;
    private boolean useChunkEncoding = false;
    
    public OSSRequestMessageBuilder(ServiceClient innerClient) {
    	this.innerClient = innerClient;
    }
    
    public URI getEndpoint() {
        return endpoint;
    }
    
    public OSSRequestMessageBuilder setEndpoint(URI endpoint) {
        assertParameterNotNull(endpoint, "endpoint");
        this.endpoint = endpoint;
        return this;
    }

    
    public HttpMethod getMethod() {
        return method;
    }
    
    public OSSRequestMessageBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }
    
    public String getBucket() {
        return bucket;
    }
    
    public OSSRequestMessageBuilder setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }
    
    public String getKey() {
        return key;
    }
    
    public OSSRequestMessageBuilder setKey(String key) {
        this.key = key;
        return this;
    }
    
    public Map<String, String>getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
    
    public OSSRequestMessageBuilder setHeaders(Map<String, String> headers) {
        assertParameterNotNull(headers, "headers");
        this.headers = headers;
        return this;
    }
    
    public OSSRequestMessageBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
    
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    public OSSRequestMessageBuilder setParameters(Map<String, String> parameters) {
        assertParameterNotNull(parameters, "parameters");
        this.parameters = parameters;
        return this;
    }
    
    public OSSRequestMessageBuilder addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }
    
    public InputStream getInputStream() {
        return inputInstream;
    }
    
    public OSSRequestMessageBuilder setInputStream(InputStream inputStream) {
        this.inputInstream = inputStream;
        return this;
    }
    
    public long getInputSize() {
        return inputSize;
    }
    
    public OSSRequestMessageBuilder setInputSize(long inputSize) {
        this.inputSize = inputSize;
        return this;
    }
    
    public boolean isUseChunkEncoding() {
		return useChunkEncoding;
	}

	public OSSRequestMessageBuilder setUseChunkEncoding(boolean useChunkEncoding) {
		this.useChunkEncoding = useChunkEncoding;
		return this;
	}

	public RequestMessage build() {
        assertParameterNotNull(endpoint, "endpoint");
        assert (OSSConstants.MAX_FILESIZE >= inputSize);
        
        // Copy headers and parameteres for resuse.
        Map<String, String> sentHeaders = new HashMap<String, String>(headers);
        Map<String, String> sentParameters = new LinkedHashMap<String, String>(parameters);
       
        // Put the current time into headers
        sentHeaders.put(OSSHeaders.DATE, DateUtil.formatRfc822Date(new Date()));
        
        // Put the Content-Type into headers
        if (sentHeaders.get(OSSHeaders.CONTENT_TYPE) == null ){
            sentHeaders.put(OSSHeaders.CONTENT_TYPE, "");
        }
        
        // Create request
        RequestMessage request = new RequestMessage();
        ClientConfiguration cc = innerClient.getClientConfiguration();
        request.setEndpoint(OSSUtils.makeBukcetEndpoint(endpoint, bucket, cc));
        request.setResourcePath(OSSUtils.makeResourcePath(key));
        request.setHeaders(sentHeaders);
        request.setParameters(sentParameters);
        request.setMethod(method);
        request.setContent(inputInstream);
        request.setContentLength(inputSize);
        request.setUseChunkEncoding(inputSize == -1 ? true : useChunkEncoding);
        
        return request;
    }
   
}
