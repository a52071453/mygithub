/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss;

/**
 * <p>
 * 表示尝试访问阿里云服务时遇到的异常。
 * </p>
 * 
 * <p>
 * {@link ClientException}表示的则是在向阿里云服务发送请求时出现的错误，以及客户端无法处理返回结果。
 * 例如，在发送请求时网络连接不可用，则会抛出{@link ClientException}的异常。
 * </p>
 * 
 * <p>
 * {@link ServiceException}用于处理阿里云服务返回的错误消息。比如，用于身份验证的Access ID不存在，
 * 则会抛出{@link ServiceException}（严格上讲，会是该类的一个继承类。比如，OSSClient会抛出OSSException）。
 * 异常中包含了错误代码，用于让调用者进行特定的处理。
 * </p>
 * 
 * <p>
 * 通常来讲，调用者只需要处理{@link ServiceException}。因为该异常表明请求被服务处理，但处理的结果表明
 * 存在错误。异常中包含了细节的信息，特别是错误代码，可以帮助调用者进行处理。
 * </p>
 * 
 */
public class ClientException extends RuntimeException {
    
    private static final long serialVersionUID = 1870835486798448798L;
    private String requestId = "Unknown";
    private String errorCode = ClientErrorCode.UNKNOWN;
    
    /**
     * 获取异常的错误码
     * @return 异常错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取本次异常的 RequestId
     * @return 本次异常的 RequestId
     */
    public String getRequestId() {
       return requestId;
    }

    /**
     * 构造新实例。
     */
    public ClientException(){
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     * @param message 异常信息。
     */
    public ClientException(String message){
        super("[ErrorMessage]: " + message);
    }

    /**
     * 用表示异常原因的对象构造新实例。
     * @param cause 异常原因。
     */
    public ClientException(Throwable cause){
        super(cause);
    }
    
    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param message 异常信息。
     * @param cause 异常原因。
     */
    public ClientException(String message, Throwable cause){
        super("[ErrorMessage]: " + message, cause);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param requestId 请求编号。
     * @param errorCode 错误码。
     * @param message 异常信息。
     * @param cause 异常原因。
     */
    public ClientException(String requestId, String errorCode, String message, Throwable cause){
        super("[ErrorCode]: " + errorCode + "\n[ErrorMessage]: " + message + "\n[RequestId]: " + requestId, cause);
        this.requestId = requestId;
        this.errorCode = errorCode;
    }

}
