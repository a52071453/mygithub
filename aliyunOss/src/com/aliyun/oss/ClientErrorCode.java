/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss;

public interface ClientErrorCode {
    
    /**
     * 未知错误
     */
    static final String UNKNOWN = "Unknown"; 
    
    /**
     * 未知域名
     */
    static final String UNKNOWN_HOST = "UnknownHost";
    
    /**
     * 远程服务连接超时
     */
    static final String CONNECTION_TIMEOUT = "ConnectionTimeout";
    
    /**
     * 远程服务socket读写超时
     */
    static final String SOCKET_TIMEOUT = "SocketTimeout";
    
    /**
     * 返回结果无法解析
     */
    static final String INVALID_RESPONSE = "InvalidResponse";
}
