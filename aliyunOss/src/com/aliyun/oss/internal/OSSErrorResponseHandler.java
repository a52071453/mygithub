/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseHandler;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.parser.JAXBResultParser;
import com.aliyun.oss.common.parser.ResultParseException;
import com.aliyun.oss.common.utils.ResourceManager;
import com.aliyun.oss.common.utils.ServiceConstants;
import com.aliyun.oss.internal.model.OSSErrorResult;

/**
 * 检查返回结果是否有错误。 如果返回状态码不为2XX，则抛出<code>OSSException</code>异常。
 * 
 */
public class OSSErrorResponseHandler implements ResponseHandler {
    public void handle(ResponseMessage responseData)
            throws OSSException, ClientException {

        assert responseData != null;

        if (responseData.isSuccessful()) {
            return;
        }

        String reqId = responseData.getRequestId();
        if (responseData.getContent() == null) {
            throw OSSExceptionFactory.createInvalidResponseException(reqId,
                    ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                            .getString("ServerReturnsUnknownError"), null);
        }

        JAXBResultParser d = new JAXBResultParser(OSSErrorResult.class);
        try {
            // throws OSSException if OSSErrorResult is deserialized
            OSSErrorResult err = (OSSErrorResult)d.getObject(responseData);
            throw OSSExceptionFactory.create(err);
        } catch (ResultParseException e) {
            throw OSSExceptionFactory.createInvalidResponseException(reqId,
                    ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                        .getString("ServerReturnsUnknownError"), e);
        } finally {            
            safeCloseResponse(responseData);
        }
    }
    
}
