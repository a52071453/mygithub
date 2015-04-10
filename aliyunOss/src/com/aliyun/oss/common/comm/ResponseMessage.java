/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.comm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.ResourceManager;
import com.aliyun.oss.common.utils.ServiceConstants;
import com.aliyun.oss.internal.OSSHeaders;

/**
 * 表示返回结果的信息。
 */
public class ResponseMessage extends HttpMesssage {
    private String uri;
    private int statusCode;
    private static final int HTTP_SUCCESS_STATUS_CODE = 200;
    private static ResourceManager rm = ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON);

    public ResponseMessage(){
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
 
    public String getRequestId() {
       return getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID);
    }

    public boolean isSuccessful(){
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }

    public String getDebugInfo() throws ClientException {
        String debugInfo = "Response Header:\n" + getHeaders().toString() +
        	"\nResponse Content:\n";
        InputStream inStream = getContent();
        if (inStream == null) {
        	return debugInfo;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.flush();
            debugInfo += outStream.toString("utf-8");
            setContent(new ByteArrayInputStream(outStream.toByteArray()));
            //outStream.close(); //close has no effect
            return debugInfo;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClientException(getRequestId(), ClientErrorCode.INVALID_RESPONSE,
                rm.getFormattedString("FailedToParseResponse",
                    e.getMessage()), e);
        }
    }
}
