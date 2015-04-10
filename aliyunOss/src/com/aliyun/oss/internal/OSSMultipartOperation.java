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
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;
import static com.aliyun.oss.internal.OSSUtils.populateRequestMetadata;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.ListMultipartUploadsRequest;
import com.aliyun.oss.model.ListPartsRequest;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * Multipart operation
 * */
public class OSSMultipartOperation extends OSSOperation {
    private static final int LIST_PART_MAX_PARTS = 1000;
    private static final String SUBRESOURCE_UPLOADS = "uploads";

    public OSSMultipartOperation(URI endpoint, ServiceClient client, ServiceCredentials cred) {
        super(endpoint, client, cred);
    }

    public void abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest)
            throws OSSException, ClientException {

        String objectKey = abortMultipartUploadRequest.getKey();
        String bucketName = abortMultipartUploadRequest.getBucketName();
        String uploadId = abortMultipartUploadRequest.getUploadId();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);
        assertUploadIdNotNull(uploadId);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("uploadId", uploadId);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                    .setEndpoint(getEndpoint())
                                                    .setMethod(HttpMethod.DELETE)
                                                    .setBucket(bucketName)
                                                    .setKey(objectKey)
                                                    .setParameters(parameters)
                                                    .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);
        send(request, context);
    
    }

    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest)
                    throws OSSException, ClientException {

        String objectKey = completeMultipartUploadRequest.getKey();
        String bucketName = completeMultipartUploadRequest.getBucketName();
        String uploadId = completeMultipartUploadRequest.getUploadId();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);
        assertUploadIdNotNull(uploadId);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.CONTENT_TYPE, "text/plain");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("uploadId", uploadId);

        String multipartRequestXml = buildMultipartRequestXml(
                completeMultipartUploadRequest.getPartETags());
        byte[] inputBytes;
        try {
            inputBytes = multipartRequestXml.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new ClientException(e);
        }
        ByteArrayInputStream input = new ByteArrayInputStream(inputBytes);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.POST)
                                            .setBucket(bucketName)
                                            .setKey(objectKey)
                                            .setHeaders(headers)
                                            .setParameters(parameters)
                                            .setInputStream(input)
                                            .setInputSize(inputBytes.length)
                                            .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);
        ResponseMessage response = send(request, context, true);

        try {
            CompleteMultipartUploadResult result =
                    ResponseParser.parseCompleteMultipartUpload(response.getRequestId(), response.getContent());
            return result;
        } finally {
            safeCloseResponse(response);
        }
    }

    private static String buildMultipartRequestXml(List<PartETag> eTags) 
            throws IllegalArgumentException {
        StringBuffer xml = new StringBuffer();
        int size = eTags.size();

        xml.append("<CompleteMultipartUpload>");
        for (int i = 0; i < size; i++){
            PartETag part = eTags.get(i);
            String eTag = "&quot;" + part.getETag().replace("\"", "") + "&quot;";
            int partNumber = part.getPartNumber();

            xml.append("<Part>");
            xml.append("<PartNumber>" + partNumber + "</PartNumber>");
            xml.append("<ETag>" + eTag + "</ETag>");
            xml.append("</Part>");
        }
        xml.append("</CompleteMultipartUpload>");

        return xml.toString();
    }

    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest initiateMultipartUploadRequest)
                    throws OSSException, ClientException {

        String objectKey = initiateMultipartUploadRequest.getKey();
        String bucketName = initiateMultipartUploadRequest.getBucketName();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);

        Map<String, String> headers = new HashMap<String, String>();

        if (initiateMultipartUploadRequest.getObjectMetadata() != null){
            populateRequestMetadata(headers,
                    initiateMultipartUploadRequest.getObjectMetadata());
        }

        // Be careful that we don't send the object's total size as the content
        // length for the InitiateMultipartUpload request.
        headers.remove(OSSHeaders.CONTENT_LENGTH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_UPLOADS, null);
        
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                        .setEndpoint(getEndpoint())
                                        .setMethod(HttpMethod.POST)
                                        .setBucket(bucketName)
                                        .setKey(objectKey)
                                        .setHeaders(headers)
                                        .setParameters(params)
                                        .setInputStream(new ByteArrayInputStream(new byte[0]))
                                        .setInputSize(0)
                                        .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);

        // Set a 0 byte input stream to avoid the ServiceClient from putting
        // the parameters to request body. Set HttpFactory.createHttpRequest
        // for details.
        ResponseMessage response = send(request, context, true);


        try {
            InitiateMultipartUploadResult result =
                    ResponseParser.parseInitiateMultipartUpload(response.getRequestId(), response.getContent());
                     

            return result;
        } finally {
            safeCloseResponse(response);
        }
    }

    public MultipartUploadListing listMultipartUploads(
            ListMultipartUploadsRequest listMultipartUploadsRequest)
                    throws OSSException, ClientException {

        String bucketName = listMultipartUploadsRequest.getBucketName();
        assertBucketName(bucketName);

        // Make the parameters cause uploads must be the first.
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(SUBRESOURCE_UPLOADS, null);
        if (listMultipartUploadsRequest.getDelimiter() != null){
            params.put("delimiter", listMultipartUploadsRequest.getDelimiter());
        }
        if (listMultipartUploadsRequest.getKeyMarker() != null){
            params.put("key-marker", listMultipartUploadsRequest.getKeyMarker());
        }
        if (listMultipartUploadsRequest.getMaxUploads() != null){
            params.put("max-uploads", listMultipartUploadsRequest.getMaxUploads().toString());
        }
        if (listMultipartUploadsRequest.getPrefix() != null){
            params.put("prefix", listMultipartUploadsRequest.getPrefix());
        }
        if (listMultipartUploadsRequest.getUploadIdMarker() != null){
            params.put("upload-id-marker", listMultipartUploadsRequest.getUploadIdMarker());
        }
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.GET)
                                            .setBucket(bucketName)
                                            .setParameters(params)
                                            .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);
        ResponseMessage response = send(request, context, true);


        try {
            MultipartUploadListing result =
                    ResponseParser.parseListMultipartUploads(response.getRequestId(), response.getContent());

            return result;
        } finally {
            safeCloseResponse(response);
        }
    }

    public PartListing listParts(ListPartsRequest listPartsRequest)
            throws OSSException, ClientException {

        String objectKey = listPartsRequest.getKey();
        String bucketName = listPartsRequest.getBucketName();
        String uploadId = listPartsRequest.getUploadId();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);
        assertUploadIdNotNull(uploadId);
        Integer maxParts = listPartsRequest.getMaxParts();
        if (maxParts != null && (maxParts < 0 || maxParts > LIST_PART_MAX_PARTS)){
            throw new IllegalArgumentException(
                    OSS_RESOURCE_MANAGER.getFormattedString(
                            "MaxPartsOutOfRange=", LIST_PART_MAX_PARTS));
        }
        Integer partNumberMarker = listPartsRequest.getPartNumberMarker();
        if (partNumberMarker != null && !isPartNumberInRange(partNumberMarker))
            throw new IllegalArgumentException(
                    OSS_RESOURCE_MANAGER.getString("PartNumberMarkerOutOfRange"));

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("uploadId", uploadId);
        if (maxParts != null) {
            parameters.put("max-parts", maxParts.toString());
        }
        if (partNumberMarker != null){
            parameters.put("part-number-marker", partNumberMarker.toString());
        }
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.GET)
                                            .setBucket(bucketName)
                                            .setKey(objectKey)
                                            .setParameters(parameters)
                                            .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);
        ResponseMessage response = send(request, context, true);


        try {
            PartListing result = ResponseParser.parseListParts(response.getRequestId(), response.getContent());

            return result;
        } finally {
            safeCloseResponse(response);
        }
    }

    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest)
            throws OSSException, ClientException {

        String bucketName = uploadPartRequest.getBucketName();
        String objectKey = uploadPartRequest.getKey();
        String uploadId = uploadPartRequest.getUploadId();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);
        assertUploadIdNotNull(uploadId);

        long partSize = uploadPartRequest.getPartSize();
        int partNumber = uploadPartRequest.getPartNumber();

        if (uploadPartRequest.getInputStream() == null) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetContentStream"));
        }
        if (partSize < 0 || partSize > OSSConstants.MAX_FILESIZE) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("FileSizeOutOfRange"));
        }
        if (!isPartNumberInRange(partNumber)) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("PartNumberOutOfRange"));
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.CONTENT_LENGTH, Long.toString(partSize));
        if (uploadPartRequest.getMd5Digest() != null){
            headers.put(OSSHeaders.CONTENT_MD5,uploadPartRequest.getMd5Digest());
        }

        // Make the params in order for easy testing.
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("partNumber", Integer.toString(partNumber));
        params.put("uploadId", uploadId);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                        .setEndpoint(getEndpoint())
                                        .setMethod(HttpMethod.PUT)
                                        .setBucket(bucketName)
                                        .setKey(objectKey)
                                        .setParameters(params)
                                        .setHeaders(headers)
                                        .setInputStream(uploadPartRequest.buildPartialStream())
                                        .setInputSize(partSize)
                                        .setUseChunkEncoding(uploadPartRequest.isUseChunkEncoding())
                                        .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);
        ResponseMessage response = send(request, context);
        System.out.println(response.getRequestId());
        UploadPartResult result = new UploadPartResult();
        result.setPartNumber(partNumber);
        result.setETag(OSSUtils.trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
        return result;
    }
    
    public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest uploadPartCopyRequest)
            throws OSSException, ClientException {

        String bucketName = uploadPartCopyRequest.getBucketName();
        String objectKey = uploadPartCopyRequest.getKey();
        String uploadId = uploadPartCopyRequest.getUploadId();

        assertBucketName(bucketName);
        assertObjectKey(objectKey);
        assertUploadIdNotNull(uploadId);

        long partSize = uploadPartCopyRequest.getPartSize();
        int partNumber = uploadPartCopyRequest.getPartNumber();

        if (partSize < 0 || partSize > OSSConstants.MAX_FILESIZE) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("FileSizeOutOfRange"));
        }
        if (!isPartNumberInRange(partNumber)) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("PartNumberOutOfRange"));
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.CONTENT_LENGTH, Long.toString(partSize));
        if (uploadPartCopyRequest.getMd5Digest() != null){
            headers.put(OSSHeaders.CONTENT_MD5,uploadPartCopyRequest.getMd5Digest());
        }
        headers.put(OSSHeaders.COPY_OBJECT_SOURCE, uploadPartCopyRequest.getSourceKey());
        headers.put(OSSHeaders.COPY_SOURCE_RANGE,  "bytes=" + uploadPartCopyRequest.getBeginIndex() 
        		+ "-" + Long.toString(uploadPartCopyRequest.getBeginIndex()+ partSize - 1));

        // Make the params in order for easy testing.
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("partNumber", Integer.toString(partNumber));
        params.put("uploadId", uploadId);
        ResponseMessage response = null;
        UploadPartCopyResult result = new UploadPartCopyResult();
        try {
		    RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		                                    .setEndpoint(getEndpoint())
		                                    .setMethod(HttpMethod.PUT)
		                                    .setBucket(bucketName)
		                                    .setKey(objectKey)
		                                    .setParameters(params)
		                                    .setHeaders(headers)
		                                    .setInputSize(partSize)
		                                    .build();
		    ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, objectKey);
		    response = send(request, context,true);
		
		    
		    result.setPartNumber(partNumber);
            result.setETag( ResponseParser.parseUploadPartCopy(response.getRequestId(), response.getContent()));
        } finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
        return result;
    }

    private void assertBucketName(String bucketName)
            throws IllegalArgumentException, NullPointerException{
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
    }

    private void assertObjectKey(String key)
            throws IllegalArgumentException, NullPointerException{
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
    }

    private void assertUploadIdNotNull(String uploadId){
        if (uploadId == null || uploadId.trim().length() == 0){
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetUploadId"));
        }
    }

    private boolean isPartNumberInRange(Integer partNumber){
        return partNumber > 0 && partNumber <= 10000;
    }
}
