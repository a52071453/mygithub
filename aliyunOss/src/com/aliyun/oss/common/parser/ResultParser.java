/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.parser;

import com.aliyun.oss.common.comm.ResponseMessage;

/**
 * Used to convert an result stream to a java object.
 */
public interface ResultParser {
    /**
     * Converts the result from stream to a java object.
     * @param resultStream The stream of the result.
     * @return The java object that the result stands for.
     * @throws ResultParseException Failed to parse the result.
     */
    public Object getObject(ResponseMessage response) throws ResultParseException;
}