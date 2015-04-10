/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.aliyun.oss.common.utils.CaseInsensitiveMap;

/**
 * The base class for message of HTTP request and response.
 */
public abstract class HttpMesssage {

    private Map<String, String> headers = new CaseInsensitiveMap<String>();
    private InputStream content;
    private long contentLength;

    protected HttpMesssage() {
        super();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers){
        assert (headers != null);
        this.headers = headers;
    }
    
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void close() throws IOException{
        if (content != null){
            content.close();
            content = null;
        }
    }
}