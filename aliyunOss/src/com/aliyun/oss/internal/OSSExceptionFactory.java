/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.model.OSSErrorResult;

/**
 * 创建oss exception
 */
public class OSSExceptionFactory {

    public static OSSException create(OSSErrorResult err) {

        return new OSSException(
                err.Message != null ? err.Message.trim() : null,
                err.Code != null ? err.Code.trim() : null,
                err.RequestId != null ? err.RequestId.trim() : null,
                err.HostId != null ? err.HostId.trim() : null,
                err.Header != null ? err.Header.trim() : null,
                err.ResourceType != null ? err.ResourceType.trim() : null,
                err.Method != null ? err.Method.trim() : null);
    }

    public static ClientException createInvalidResponseException(String requestId,
            String message, Throwable cause) {
        return new ClientException(requestId, ClientErrorCode.INVALID_RESPONSE , message, cause);
    }
    
    public static OSSException create(String requestId, String errorCode, String message) {
        return new OSSException(message, errorCode, requestId, null, null, null, null);
    }
}
