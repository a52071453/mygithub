/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.auth.RequestSigner;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.comm.RequestMessage;

public class OSSRequestSigner implements RequestSigner{

    private String httpMethod;
    private String resourcePath; // This resourcePath should not have been url encoded.
    private ServiceCredentials credentials;

    public OSSRequestSigner(String httpMethod, String resourcePath, ServiceCredentials credentials){
        assert credentials != null;
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.credentials = credentials;
    }

    @Override
    public void sign(RequestMessage request) throws ClientException {
        String secretAccessKey = credentials.getAccessKeySecret();
        String accessId = credentials.getAccessKeyId();

        if (accessId.length() > 0 && secretAccessKey.length() > 0) {

            String canonicalString = SignUtils.buildCanonicalString(httpMethod, resourcePath, request, null);
            String signature = ServiceSignature.create().computeSignature(secretAccessKey, canonicalString);
            request.addHeader(OSSHeaders.AUTHORIZATION, "OSS " + accessId + ":" + signature);

        } else if (accessId.length() > 0) {
            request.addHeader(OSSHeaders.AUTHORIZATION, accessId);
        }
    }
}
