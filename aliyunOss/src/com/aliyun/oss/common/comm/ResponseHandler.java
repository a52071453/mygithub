/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.ServiceException;

/**
 * 对返回结果进行处理。
 *
 */
public interface ResponseHandler {

    /**
     * 处理返回的结果
     */
    public void handle(ResponseMessage responseData)
            throws ServiceException, ClientException;
}
