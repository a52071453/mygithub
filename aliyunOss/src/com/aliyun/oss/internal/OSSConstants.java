/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import com.aliyun.oss.common.utils.ServiceConstants;

/**
 * 定义oss上的一些常量信息
 * **/
public class OSSConstants {
    public static final String DEFAULT_OSS_ENDPOINT = "http://oss.aliyuncs.com";

    public static final String DEFAULT_CHARSET_NAME = ServiceConstants.DEFAULT_ENCODING;
    public static final String DEFAULT_XML_CHARSET = ServiceConstants.DEFAULT_ENCODING;
    
    public static final String DEFAULT_OBJECT_CONTENT_TYPE = "application/octet-stream";
    
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final long MAX_FILESIZE = 5 * 1024 * 1024 * 1024L;
}
