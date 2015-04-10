/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.auth;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.comm.RequestMessage;

public interface RequestSigner {

    public void sign(RequestMessage request)
            throws ClientException;
}
