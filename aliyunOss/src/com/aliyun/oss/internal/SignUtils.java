/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.model.ResponseHeaderOverrides;

public class SignUtils {
    private static final String NEW_LINE = "\n";

    private static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[] {
            "acl", "uploadId", "partNumber", "uploads", "location", "cors", "logging", "website", "referer", "lifecycle",
            ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL,
            ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION,
            ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING,
            ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE,
            ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE,
            ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES,
    });
    
    public static String buildCanonicalString(String method, String resourcePath,
            RequestMessage request, String expires){
        StringBuilder builder = new StringBuilder();
        builder.append(method + NEW_LINE);
        
        Map<String, String> headers = request.getHeaders();
        TreeMap<String, String> headersToSign = new TreeMap<String, String>();
        
        if (headers != null){
            for(Entry<String, String> header : headers.entrySet()){
                if (header.getKey() == null){
                    continue;
                }
                
                String lowerKey = header.getKey().toLowerCase();
                
                if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase())
                        || lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase())
                        || lowerKey.equals(HttpHeaders.DATE.toLowerCase())
                        || lowerKey.startsWith(OSSHeaders.OSS_PREFIX)){
                    headersToSign.put(lowerKey, header.getValue());
                }
            }
        }
        
        if (!headersToSign.containsKey(HttpHeaders.CONTENT_TYPE.toLowerCase())){
            headersToSign.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), "");
        }
        if (!headersToSign.containsKey(HttpHeaders.CONTENT_MD5.toLowerCase())){
            headersToSign.put(HttpHeaders.CONTENT_MD5.toLowerCase(), "");
        }
        
        // Add params that have the prefix "x-oss-"
        if (request.getParameters() != null){
            for(Map.Entry<String, String> p : request.getParameters().entrySet()){
                if (p.getKey().startsWith(OSSHeaders.OSS_PREFIX)){
                    headersToSign.put(p.getKey(), p.getValue());
                }
            }
        }
        
        // Add all headers to sign to the builder
        for(Map.Entry<String, String> entry : headersToSign.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (key.startsWith(OSSHeaders.OSS_PREFIX)){
                builder.append(key).append(':').append(value);
            } else {
                builder.append(value);
            }
            
            builder.append("\n");
        }
        
        // Add canonical resource
        builder.append(buildCanonicalizedResource(resourcePath, request.getParameters()));
        
        return builder.toString();
    }

    private static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters){
        assert (resourcePath.startsWith("/"));

        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);

        if (parameters != null){
            String[] parameterNames = parameters.keySet().toArray(
                    new String[parameters.size()]);
            Arrays.sort(parameterNames);
            char separater = '?';
            for(String paramName : parameterNames){
                if (!SIGNED_PARAMTERS.contains(paramName)){
                    continue;
                }

                builder.append(separater);
                builder.append(paramName);
                String paramValue = parameters.get(paramName);
                if (paramValue != null){
                    builder.append("=").append(paramValue);
                }

                separater = '&';
            }
        }
        
        return builder.toString();
    }
}
