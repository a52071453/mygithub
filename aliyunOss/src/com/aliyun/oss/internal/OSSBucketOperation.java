/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;

/**
 * bucket operation
 * */
public class OSSBucketOperation extends OSSOperation {
    private static final String SUBRESOURCE_ACL = "acl";
    private static final String SUBRESOURCE_REFERER = "referer";
    private static final String SUBRESOURCE_LOCATION = "location"; 
    private static final String SUBRESOURCE_LOGGING = "logging"; 
    private static final String SUBRESOURCE_WEBSITE = "website"; 
    private static final String SUBRESOURCE_LIFECYCLE = "lifecycle";

    public OSSBucketOperation(URI endpoint, ServiceClient client, ServiceCredentials cred) {
        super(endpoint, client, cred);
    }
    
    /**
     * Creates a bucket.
     * */
    public Bucket createBucket(CreateBucketRequest createBucketRequest)
            throws OSSException, ClientException{

        String bucketName = createBucketRequest.getBucketName();
        String locationConstraint = createBucketRequest.getLocationConstraint();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
                
        String xmlBody = "";
        if (locationConstraint != null) {
            xmlBody = buildCreateBucketXml(locationConstraint);
        }
        byte[] inputBytes = xmlBody.getBytes();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setInputStream(new ByteArrayInputStream(inputBytes))
                                            .setInputSize(inputBytes.length)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        
        send(request, context);

        return new Bucket(bucketName);
    }

    /**
     * Delete the bucket.
     * */
    public void deleteBucket(String bucketName)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.DELETE)
                                            .setBucket(bucketName)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        
        send(request, context);
        
    }

    /**
     * Lists all my buckets. 
     * */
    public BucketList listBuckets(ListBucketsRequest listBucketRequest) throws OSSException, ClientException{

        // check parameter
        assertParameterNotNull(listBucketRequest, "request");

        // 使用LinkedHashMap以保证参数有序可测试
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (listBucketRequest.getPrefix() != null){
            params.put("prefix", listBucketRequest.getPrefix());
        }
        if (listBucketRequest.getMarker() != null){
            params.put("marker", listBucketRequest.getMarker());
        }
        if (listBucketRequest.getMaxKeys() != null){
            params.put("max-keys", Integer.toString(listBucketRequest.getMaxKeys()));
        }

        ResponseMessage response = null;

        try {
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setParameters(params)
                .build();
            
            ExecutionContext context = createDefaultContext(request.getMethod());        
 
            response = send(request, context, true);

            // TODO: refactor ResponseParser
            return ResponseParser.parseListBucket(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }

    /**
     * Sets ACL for the bucket.
     */
    public void setBucketAcl(String bucketName, CannedAccessControlList acl)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        if (acl == null) {
            acl = CannedAccessControlList.Private;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_CANNED_ACL, acl.toString());
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setHeaders(headers)
                                            .setParameters(params)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        
        send(request, context);

    }

    /**
     * Gets ACL of the bucket.
     * */
    public AccessControlList getBucketAcl(String bucketName)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();
            
            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            
            response = send(request, context, true);

            return ResponseParser.parseGetBucketAcl(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * Sets http referer for the bucket.
     */
    public void setBucketReferer(String bucketName, BucketReferer referer)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        if (referer == null) {
            referer = new BucketReferer();
        }
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);
        byte[] inputBytes = referer.toXmlString().getBytes();;
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .setInputStream(new ByteArrayInputStream(inputBytes))
                                            .setInputSize(inputBytes.length)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        
        send(request, context);
    }

    /**
     * Gets http referer of the bucket.
     * */
    public BucketReferer getBucketReferer(String bucketName)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        ResponseMessage response = null;
        try {
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();
            
            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            
            response = send(request, context, true);

            return ResponseParser.parseGetBucketReferer(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * Get bucket location
     */
    public String getBucketLocation(String bucketName) {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOCATION, null);
        
        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();
            
            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            
            response = send(request, context, true);

            return ResponseParser.parseGetBucketLocation(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }

    /**
     * is bucket exist
     * */
    public boolean bucketExists(String bucketName)
            throws OSSException, ClientException{

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        try {
        	 getBucketAcl(bucketName);
		} catch (OSSException e) {
		   if(e.getErrorCode().equals(OSSErrorCode.NO_SUCH_BUCKET)) {
			   return false;
		   }
		}
        return true;
    }

    /**
     * list objects in the bucket.
     * */
    public ObjectListing listObjects(ListObjectsRequest listObjectRequest)
            throws OSSException, ClientException{

        assertParameterNotNull(listObjectRequest, "request");
        if (listObjectRequest.getBucketName() == null)
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetBucketName"));
        ensureBucketNameValid(listObjectRequest.getBucketName());

        // 使用LinkedHashMap以保证参数有序可测试
        Map<String, String> params = new LinkedHashMap<String, String>();
        if (listObjectRequest.getPrefix() != null){
            params.put("prefix", listObjectRequest.getPrefix());
        }
        if (listObjectRequest.getMarker() != null){
            params.put("marker", listObjectRequest.getMarker());
        }
        if (listObjectRequest.getDelimiter() != null){
            params.put("delimiter", listObjectRequest.getDelimiter());
        }
        if (listObjectRequest.getMaxKeys() != null){
            params.put("max-keys", Integer.toString(listObjectRequest.getMaxKeys()));
        }

        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                    .setEndpoint(getEndpoint())
                                                    .setMethod(HttpMethod.GET)
                                                    .setBucket(listObjectRequest.getBucketName())
                                                    .setParameters(params)
                                                    .build();
            
            ExecutionContext context = createDefaultContext(request.getMethod(), listObjectRequest.getBucketName());        
            
            response = send(request, context, true);

            return ResponseParser.parseListObjects(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * set bucket logging.
     * */
    public void setBucketLogging(SetBucketLoggingRequest setBucketLoggingRequest)
            throws OSSException, ClientException{

        String bucketName = setBucketLoggingRequest.getBucketName();
        String targetBucket = setBucketLoggingRequest.getTargetBucket();
        String targetPrefix = setBucketLoggingRequest.getTargetPrefix();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(targetBucket, "targetBucket");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
                
        String xmlBody = "";
        xmlBody = buildPutBucketLoggingXml(targetBucket,targetPrefix);
        byte[] inputBytes = xmlBody.getBytes();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .setInputStream(new ByteArrayInputStream(inputBytes))
                                            .setInputSize(inputBytes.length)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        
        send(request, context);
    }
    
    /**
     * get bucket logging.
     * */
    public BucketLoggingResult getBucketLogging(String bucketName)
            throws OSSException, ClientException{
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();

            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            response = send(request, context, true);
            return ResponseParser.parseBucketLogging(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * delete bucket logging.
     * */
    public void deleteBucketLogging(String bucketName)
            throws OSSException, ClientException{

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.DELETE)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        send(request, context);
    }
    
    /**
     * set bucket website.
     * */
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest)
            throws OSSException, ClientException{

        String bucketName = setBucketWebSiteRequest.getBucketName();
        String indexDocument = setBucketWebSiteRequest.getIndexDocument();
        String errorDocument = setBucketWebSiteRequest.getErrorDocument();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(indexDocument, "indexDocument");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
                
        String xmlBody = "";
        xmlBody = buildPutBucketWebSiteXml(indexDocument,errorDocument);
        byte[] inputBytes = xmlBody.getBytes();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .setInputStream(new ByteArrayInputStream(inputBytes))
                                            .setInputSize(inputBytes.length)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        send(request, context);
    }
    
    /**
     * get bucket website.
     * */
    public BucketWebsiteResult getBucketWebsite(String bucketName)
            throws OSSException, ClientException{
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();

            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            response = send(request, context, true);
            return ResponseParser.parseBucketWebsite(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * delete bucket website.
     * */
    public void deleteBucketWebsite(String bucketName)
            throws OSSException, ClientException{

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.DELETE)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        send(request, context);
    }
    
    /**
     * set bucket lifecycle.
     * */
    public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException{
    	
        String bucketName = setBucketLifecycleRequest.getBucketName();
        List<LifecycleRule> lifecycleRules = setBucketLifecycleRequest.getLifecycleRules();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(lifecycleRules, "lifecycleRules");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
                
        String xmlBody = "";
        xmlBody = buildSetBucketLifecycleXml(lifecycleRules);
        byte[] inputBytes = xmlBody.getBytes();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.PUT)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .setInputStream(new ByteArrayInputStream(inputBytes))
                                            .setInputSize(inputBytes.length)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        send(request, context);
    }
    
    /**
     * get bucket lifecycle.
     * */
    public List<LifecycleRule> getBucketLifecycle(String bucketName)
            throws OSSException, ClientException{
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        ResponseMessage response = null;
        try{
            RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.GET)
                                                .setBucket(bucketName)
                                                .setParameters(params)
                                                .build();

            ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
            response = send(request, context, true);
            return ResponseParser.parseGetBucketLifecycle(response.getRequestId(), response.getContent());
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
    }
    
    /**
     * delete bucket lifecycle.
     */
    public void deleteBucketLifecycle(String bucketName)
            throws OSSException, ClientException {

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.DELETE)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .build();
        
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);        
        send(request, context);
    }
    
    private static String buildPutBucketWebSiteXml(String indexDocument,String errorDocument) {
        StringBuffer xml = new StringBuffer();
        xml.append("<WebsiteConfiguration>");
        xml.append("<IndexDocument>");
        xml.append("<Suffix>" + indexDocument + "</Suffix>");
        xml.append("</IndexDocument>");
        if(errorDocument != null){
            xml.append("<ErrorDocument>");
        	xml.append("<Key>" + errorDocument + "</Key>");
            xml.append("</ErrorDocument>");
        }
        xml.append("</WebsiteConfiguration>");
        return xml.toString();
    }
    
    private static String buildPutBucketLoggingXml(String targetBucket,String targetPrefix) {
        StringBuffer xml = new StringBuffer();
        xml.append("<BucketLoggingStatus>");
        xml.append("<LoggingEnabled>");
        xml.append("<TargetBucket>" + targetBucket + "</TargetBucket>");
        if(targetPrefix != null){
        	xml.append("<TargetPrefix>" + targetPrefix + "</TargetPrefix>");
        }
        xml.append("</LoggingEnabled>");
        xml.append("</BucketLoggingStatus>");
        return xml.toString();
    }
    
    private static String buildCreateBucketXml(String locationConstraint) {
        StringBuffer xml = new StringBuffer();

        xml.append("<CreateBucketConfiguration>");
        xml.append("<LocationConstraint>" + locationConstraint + "</LocationConstraint>");
        xml.append("</CreateBucketConfiguration>");

        return xml.toString();
    }
    
    private static String buildSetBucketLifecycleXml(List<LifecycleRule> lifecycleRules) {
        StringBuffer xml = new StringBuffer();
        xml.append("<LifecycleConfiguration>");
        for (LifecycleRule rule : lifecycleRules) {
        	xml.append("<Rule>");
            xml.append("<ID>" + rule.getId() + "</ID>");
            xml.append("<Prefix>" + rule.getPrefix() + "</Prefix>");
            
            if (rule.getStatus() == RuleStatus.Enabled) {
            	xml.append("<Status>Enabled</Status>");
            } else {
            	xml.append("<Status>Disabled</Status>");
            }
            
            if (rule.getExpirationTime() != null) {
            	String formatDate = DateUtil.formatIso8601Date(rule.getExpirationTime());
            	xml.append("<Expiration><Date>" + formatDate + "</Date></Expiration>");
            } else {
            	xml.append("<Expiration><Days>" + rule.getExpriationDays() + "</Days></Expiration>");
            }
            
            xml.append("</Rule>");
        }
        xml.append("</LifecycleConfiguration>");
        return xml.toString();
    }
}
