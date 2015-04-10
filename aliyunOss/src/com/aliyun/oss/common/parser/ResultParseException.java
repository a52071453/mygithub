/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.parser;

/**
 * The exception from parsing service result.
 */
public class ResultParseException extends Exception {
    private static final long serialVersionUID = -6660159156997037589L;

    public ResultParseException(){
        super();
    }
    
    public ResultParseException(String message){
        super(message);
    }
    
    public ResultParseException(Throwable cause){
        super(cause);
    }
    
    public ResultParseException(String message, Throwable cause){
        super(message, cause);
    }
}
