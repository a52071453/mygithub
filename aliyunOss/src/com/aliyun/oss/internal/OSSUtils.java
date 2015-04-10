/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.ResourceManager;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.ResponseHeaderOverrides;

public class OSSUtils {
	
    public static final ResourceManager OSS_RESOURCE_MANAGER =
            ResourceManager.getInstance("oss");

    /**
     * Validate bucket name
     */
    public static boolean validateBucketName(String bucketName) {
        if (bucketName == null) {
            return false;
        }
        final String BUCKET_REGEX = "^[a-z0-9][a-z0-9\\-]{1,61}[a-z0-9]$";
        return bucketName.matches(BUCKET_REGEX);
    }

    public static void ensureBucketNameValid(String bucketName){
        if (!validateBucketName(bucketName)){
            throw new IllegalArgumentException(
                    OSS_RESOURCE_MANAGER.getString("BucketNameInvalid"));
        }
    }

    /**
     * Validate object name
     */
    public static boolean validateObjectKey(String key) {
        if (key == null) {
            return false;
        }
        // Validate CHARSET encode
        byte[] bytes;
        try {
            bytes = key.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        
        // Validate exculde xml unsupported chars
        char keyChars[] = key.toCharArray();
        
        // Could not start with "/" or "\"
        char beginKeyChar = keyChars[0];
        if (beginKeyChar == '/' || beginKeyChar == '\\') {
        	return false;
        }
        
        // Exculde char smaller than 0x20 but 0x9 is legal
        for (char keyChar : keyChars) {
            if (keyChar != 0x9 && keyChar < 0x20) {
                return false;
            }
        }
        // Strikly, the below code can exclude all XML unsupported chars
//        for (char keyChar : keyChars) {
//            if (!((keyChar == 0x9)
//                    || ((keyChar >= 0x20) && (keyChar <= 0xD7FF))
//                    || ((keyChar >= 0xE000) && (keyChar <= 0xFFFD))
//                    || ((keyChar >= 0x10000) && (keyChar <= 0x10FFFF)))) {
//                return false;
//            }
//        }

        return (bytes.length > 0 && bytes.length < 1024);
    }

    public static void ensureObjectKeyValid(String key){
        if (!validateObjectKey(key)){
            throw new IllegalArgumentException(
                    OSS_RESOURCE_MANAGER.getString("ObjectKeyInvalid"));
        }
    }
    
    /**
     * Make a endpoint that contains the bucket.
     * This is a thrid level domain endpoint beginning with the name of bucket
     * @param endpoint
     * @param bucket
     * @param clientConfig
     * @return The endpoint beginning with bucket name.
     */
    public static URI makeBukcetEndpoint(URI endpoint, String bucket, ClientConfiguration clientConfig) {
        try {
            return new URI(endpoint.getScheme(), 
                            null,
                            buildCanonicalHost(endpoint, bucket, clientConfig),
                            endpoint.getPort(),
                            endpoint.getPath(),
                            null,
                            null);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);            
        }
    }
    
    private static String buildCanonicalHost(URI endpoint, String bucket, ClientConfiguration clientConfig) {
    	String host = endpoint.getHost();
    	boolean isCname = cnameExcludeFilter(host, clientConfig.getCnameExcludeList());
    	
    	StringBuffer cannonicalHost = new StringBuffer();
    	if (bucket != null && !isCname) {
    		cannonicalHost.append(bucket);
    		cannonicalHost.append(".");
    		cannonicalHost.append(host);
    	} else {
    		cannonicalHost.append(host);
    	}
    	
    	return cannonicalHost.toString();
    }

	 private static boolean cnameExcludeFilter(String hostToFilter, List<String> excludeList) {
    	if (hostToFilter != null && !hostToFilter.trim().isEmpty()) {
    		String canonicalHost = hostToFilter.toLowerCase();
    		for (String excl : excludeList) {
    			if (canonicalHost.endsWith(excl)) {
    				return false;
    			}
    		}
    		return true;
    	}
    	throw new  NullPointerException("Host name can not be null.");
    }
	
    /**
     * Makes a resource path from the object key, used when the bucket name pearing in the endpoint.
     * @param bucket
     * @param key
     * @return
     */
    public static String makeResourcePath(String key) {
        return key != null ? OSSUtils.urlEncodeKey(key) : null;
    }

    /**
     * Makes a resource path from the bucket name and the object key.
     * @param bucket
     * @param key
     * @return
     */
    public static String makeResourcePath(String bucket, String key) {
        if (bucket != null){
            return bucket + (key != null ? "/" + OSSUtils.urlEncodeKey(key) : "");
        } else {
            return null;
        }
    }

    /**
     * Encode object URI
     * 
     * @param key
     *            Object name
     * @return object URI
     */
    private static String urlEncodeKey(String key) {
        String[] keys = key.split("/");
        StringBuffer uri = new StringBuffer();
        try {
            uri.append(HttpUtil.urlEncode(keys[0], OSSConstants.DEFAULT_CHARSET_NAME));

            for (int i = 1; i < keys.length; i++) {
                uri.append("/").append(HttpUtil.urlEncode(keys[i], OSSConstants.DEFAULT_CHARSET_NAME));
            }

            if (key.endsWith("/")) {
                // String#split ignores trailing empty strings,
                // e.g., "a/b/" will be split as a 2-entries array,
                // so we have to append all the trailing slash to the uri.
                for (int i = key.length() - 1; i >= 0; i--) {
                    if (key.charAt(i) == '/') {
                        uri.append("/");
                    } else {
                        break;
                    }
                }
            }

            return uri.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(
                    OSS_RESOURCE_MANAGER.getFormattedString("FailedToEncodeObjectKey",
                            key, OSSConstants.DEFAULT_CHARSET_NAME),
                    e);
        }
    }

    /**
     * populate metadata to headers
     * */
    public static void populateRequestMetadata(Map<String, String> headers, ObjectMetadata metadata) {
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Entry<String, Object> entry : rawMetadata.entrySet()) {
                headers.put(entry.getKey(), entry.getValue().toString());
            }
            if (!rawMetadata.keySet().contains(OSSHeaders.CONTENT_TYPE)){
                headers.put(OSSHeaders.CONTENT_TYPE, OSSConstants.DEFAULT_OBJECT_CONTENT_TYPE);
            }
        }

        Map<String, String> userMetadata = metadata.getUserMetadata();
        if (userMetadata != null) {
            for (Entry<String, String> entry : userMetadata.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null) key = key.trim();
                if (value != null) value = value.trim();
                headers.put(OSSHeaders.OSS_USER_METADATA_PREFIX + key, value);
            }
        }
    }
 
    public static void addHeader(Map<String, String> headers, String header, String value) {
        if (value != null){
            headers.put(header, value);
        }
    }

    public static void addDateHeader(Map<String, String> headers, String header, Date value) {
        if (value != null){
            headers.put(header, DateUtil.formatRfc822Date(value));
        }
    }

    public static void addListHeader(Map<String, String> headers, String header, List<String> values) {
        if (values != null && values.size() > 0){
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for(String value : values){
                if (!first){
                    sb.append(", ");
                }

                sb.append(value);
                first = false;
            }

            headers.put(header, sb.toString());
        }
    }
    /**
     * 删除字符串开始和结尾的双引号，应用于ETag。
     * */
    public static String trimQuotes(String s) {
        if (s == null) return null;

        s = s.trim();
        if (s.startsWith("\"")) s = s.substring(1);
        if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);

        return s;
    }

    /**
     * Gets a map that contains the fields of response header overrides in order.
     * @param responseHeader
     * @return
     */
    public static Map<String, String> getResponseHeaderParameters(ResponseHeaderOverrides responseHeader){
        HashMap<String, String> params = new HashMap<String, String>();

        if (responseHeader != null){
            if (responseHeader.getCacheControl() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL, responseHeader.getCacheControl());
            }
            if (responseHeader.getContentDisposition() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION, responseHeader.getContentDisposition());
            }
            if (responseHeader.getContentEncoding() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING, responseHeader.getContentEncoding());
            }
            if (responseHeader.getContentLangauge() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE, responseHeader.getContentLangauge());
            }
            if (responseHeader.getContentType() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE, responseHeader.getContentType());
            }
            if (responseHeader.getExpires() != null){
                params.put(ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES, responseHeader.getExpires());
            }
        }

        return params;
    }

    public static void safeCloseResponse(ResponseMessage response) {
        assert (response != null);
        try{
            response.close();
        } catch(IOException e) {}
    }
}
