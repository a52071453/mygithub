/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss;


/**
 * 该异常在对开放存储数据服务（Open Storage Service）访问失败时抛出。
 */
public class OSSException extends ServiceException {

    private static final long serialVersionUID = -1979779664334663173L;
    private String header;
    private String resourceType;

    public OSSException() {
        super();
    }

    public OSSException(String message) {
        super(message);
    }

    public OSSException(String message, Throwable cause) {
        super(message, cause);
    }

    public OSSException(String message, String errorCode, String requestId,
            String hostId, String header, String resourceType, String method) {

        super(message, null, errorCode, requestId, hostId);

        this.header = header;
        this.resourceType = resourceType;
    }

    public String getHeader() {
        return header;
    }

    public String getResourceType() {
        return resourceType;
    }
}