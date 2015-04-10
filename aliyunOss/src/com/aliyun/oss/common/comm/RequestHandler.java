/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.comm.ServiceClient.Request;

/**
 * 对即将发送的请求数据进行预处理
 *
 */
public interface RequestHandler {

    /**
     * 预处理需要发送的请求数据
     */
    public void handle(Request message)
            throws ServiceException, ClientException;
}
