/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.addHeader;
import static com.aliyun.oss.internal.OSSUtils.addDateHeader;
import static com.aliyun.oss.internal.OSSUtils.addListHeader;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;
import static com.aliyun.oss.internal.OSSUtils.getResponseHeaderParameters;
import static com.aliyun.oss.internal.OSSUtils.populateRequestMetadata;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.OSSUtils.trimQuotes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseHandler;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * Object operation
 * */
public class OSSObjectOperation extends OSSOperation {

    public OSSObjectOperation(URI endpoint, ServiceClient client,
            ServiceCredentials cred) {
        super(endpoint, client, cred);
    }

    /**
     * upload file to oss, from inputstream
     * */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, 
    		ObjectMetadata metadata) throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        assertParameterNotNull(input, "input");
        assertParameterNotNull(metadata, "metadata");

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestMetadata(headers, metadata);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                                .setEndpoint(getEndpoint())
                                                .setMethod(HttpMethod.PUT)
                                                .setBucket(bucketName)
                                                .setKey(key)
                                                .setHeaders(headers)
                                                .setInputStream(input)
                                                .setInputSize(determineInputStreamLength(input, metadata.getContentLength()))
                                                .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, key);

        ResponseMessage response = send(request, context, true);

        // TODO Compare the MD5 value of the uploading stream with the returned ETag.
        PutObjectResult result = new PutObjectResult();
        try{
            result.setETag(trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
        } finally {
            safeCloseResponse(response);
        }

        return result;
    }
    
    private static long determineInputStreamLength(InputStream instream, long hintLength) {
    	if (hintLength <= 0 || !instream.markSupported()) {
    		return -1;
    	} 
    	
    	return hintLength;
    }
    
    private static long determineInputStreamLength(InputStream instream, long hintLength, boolean useChunkEncoding) {
    	if (useChunkEncoding) {
    		return -1;
    	}
    	
    	if (hintLength <= 0 || !instream.markSupported()) {
    		return -1;
    	} 
    	
    	return hintLength;
    }
    
    /**
     * upload file to oss by using url signature, from inputstream
     * */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
    		Map<String, String> customHeaders, boolean useChunkEncoding) throws OSSException, ClientException {

        assertParameterNotNull(signedUrl, "signedUrl");
        assertParameterNotNull(requestContent, "requestContent");

        RequestMessage request = new RequestMessage();
        request.setMethod(HttpMethod.PUT);
    	request.setAbsoluteUrl(signedUrl);
    	request.setUseUrlSignature(true);
    	request.setContent(requestContent);
    	request.setContentLength(determineInputStreamLength(requestContent, contentLength, useChunkEncoding));
    	request.setHeaders(customHeaders == null ? new HashMap<String, String>() : customHeaders);
    	request.setUseChunkEncoding(useChunkEncoding);
    	
    	ExecutionContext context = createDefaultContext(request.getMethod());
        
        ResponseMessage response = send(request, context, true);

        // TODO Compare the MD5 value of the uploading stream with the returned ETag.
        PutObjectResult result = new PutObjectResult();
        try{
            result.setETag(trimQuotes(response.getHeaders().get(OSSHeaders.ETAG)));
        } finally {
            safeCloseResponse(response);
        }

        return result;
    }

    /**
     * Downloads an object from OSS.
     * */
    public OSSObject getObject(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        return getObject(new GetObjectRequest(bucketName, key));
    }

    /**
     * Downloads an object from OSS.
     * 
     * @param getObjectRequest
     * @return
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest)
            throws OSSException, ClientException {
    	String bucketName = null;
    	String key = null;
    	RequestMessage request = null;
    	ExecutionContext context = null;
    	
    	if (!getObjectRequest.isUseUrlSignature()) {
        	assertParameterNotNull(getObjectRequest, "getObjectRequest");
            if (getObjectRequest.getBucketName() == null)
                throw new IllegalArgumentException(
                        OSS_RESOURCE_MANAGER.getString("MustSetBucketName"));

            bucketName = getObjectRequest.getBucketName();
            key = getObjectRequest.getKey();
            ensureBucketNameValid(bucketName);
            ensureObjectKeyValid(key);

            Map<String, String> headers = new HashMap<String, String>();

            // Add the header of "Range"
            long[] range = getObjectRequest.getRange();
            if (getObjectRequest.getRange() != null &&
                    (range[0] >= 0 || range[1] >= 0)) {
                StringBuilder rangeValue = new StringBuilder().append("bytes=");
                if (range[0] >= 0) {
                    rangeValue.append(Long.toString(range[0]));
                }
                rangeValue.append("-");
                if (range[1] >= 0) {
                    rangeValue.append(Long.toString(range[1]));
                }

                headers.put(OSSHeaders.RANGE, rangeValue.toString());
            }

            // Add the headers of matching conditions
            if (getObjectRequest.getModifiedSinceConstraint() != null) {
                headers.put(OSSHeaders.GET_OBJECT_IF_MODIFIED_SINCE, DateUtil
                        .formatRfc822Date(getObjectRequest
                                .getModifiedSinceConstraint()));
            }
            if (getObjectRequest.getUnmodifiedSinceConstraint() != null) {
                headers.put(OSSHeaders.GET_OBJECT_IF_UNMODIFIED_SINCE, DateUtil
                        .formatRfc822Date(getObjectRequest
                                .getUnmodifiedSinceConstraint()));
            }
            if (getObjectRequest.getMatchingETagConstraints().size() > 0){
                headers.put(OSSHeaders.GET_OBJECT_IF_MATCH,
                        joinETags(getObjectRequest.getMatchingETagConstraints()));
            }
            if (getObjectRequest.getNonmatchingETagConstraints().size() > 0){
                headers.put(OSSHeaders.GET_OBJECT_IF_NONE_MATCH,
                        joinETags(getObjectRequest.getNonmatchingETagConstraints()));
            }
            headers.putAll(getObjectRequest.getHeaders());

            Map<String, String> params = getResponseHeaderParameters(
                    getObjectRequest.getResponseHeaders());
            
            request = new OSSRequestMessageBuilder(getInnerClient())
                                            .setEndpoint(getEndpoint())
                                            .setMethod(HttpMethod.GET)
                                            .setBucket(bucketName)
                                            .setKey(key)
                                            .setHeaders(headers)
                                            .setParameters(params)
                                            .build();
            context = createDefaultContext(request.getMethod(), bucketName, key);
        } else {
        	request = new RequestMessage();
        	request.setMethod(HttpMethod.GET);
        	request.setAbsoluteUrl(getObjectRequest.getAbsoluteUri());
        	request.setUseUrlSignature(true);
        	request.setHeaders(getObjectRequest.getHeaders());
        	
        	context = createDefaultContext(request.getMethod());
        }

        // Cannot close this response because OSSObject refers to its content
        // stream.
        ResponseMessage response = send(request, context, true);

        OSSObject ossObject = new OSSObject();
        ossObject.setObjectContent(response.getContent());
        ossObject.setBucketName(bucketName);
        ossObject.setKey(key);
        try {
            ossObject.setObjectMetadata(
                    ResponseParser.getObjectMetadata(response.getRequestId(), response.getHeaders()));
        } catch (ClientException e) {
            safeCloseResponse(response);
            throw e;
        }

        return ossObject;
    }

    // For etag constraint to get object.
    private static String joinETags(List<String> etags) {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (String etag : etags) {
            if (!first) result.append(", ");

            result.append(etag);
            first = false;
        }

        return result.toString();
    }

    /**
     * Downloads an object to a local file
     * */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file)
            throws OSSException, ClientException {

        assertParameterNotNull(file, "file");

        OSSObject ossObject = getObject(getObjectRequest);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            int bufSize = 1024 * 4;
            byte[] buffer = new byte[bufSize];
            int bytesRead;
            while ((bytesRead = ossObject.getObjectContent().read(buffer)) > -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new ClientException(OSS_RESOURCE_MANAGER.getString("CannotReadContentStream"), e);
        } finally {
            // Close the output stream
            IOUtils.safeClose(outputStream);
            // Close the response stream
            IOUtils.safeClose(ossObject.getObjectContent());
        }

        return ossObject.getObjectMetadata();
    }

    /**
     * get object matadata
     * */
    public ObjectMetadata getObjectMetadata(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                        .setEndpoint(getEndpoint())
                                        .setMethod(HttpMethod.HEAD)
                                        .setBucket(bucketName)
                                        .setKey(key)
                                        .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, key);
        
        context.insertResponseHandler(0, new ResponseHandler() {
            
            @Override
            public void handle(ResponseMessage responseData) throws ServiceException,
                    ClientException {
                if (responseData.getStatusCode() == 404) {
                    safeCloseResponse(responseData);
                    throw OSSExceptionFactory.create(responseData.getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID), 
                                        OSSErrorCode.NO_SUCH_KEY, 
                                        OSSUtils.OSS_RESOURCE_MANAGER.getString("NoSuchKey"));
                }
            }
        });

        ResponseMessage response = send(request, context, true);

        try{
            ObjectMetadata result =
                    ResponseParser.getObjectMetadata(response.getRequestId(), response.getHeaders());
            return result;
        } finally {
            safeCloseResponse(response);
        }
    }

    /**
     * Copy an existing object to a new object.
     * @param copyObjectRequest
     * @return
     * @throws ClientException 
     * @throws OSSException 
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)
            throws OSSException, ClientException{

        assertParameterNotNull(copyObjectRequest, "copyObjectRequest");

        Map<String, String> headers = new HashMap<String, String>();
        populateCopyObjectHeaders(copyObjectRequest, headers);

        // The header of Content-Length should not be specified on copying an object.
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                        .setEndpoint(getEndpoint())
                                        .setMethod(HttpMethod.PUT)
                                        .setBucket(copyObjectRequest.getDestinationBucketName())
                                        .setKey(copyObjectRequest.getDestinationKey())
                                        .setHeaders(headers)
                                        .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), 
                                                copyObjectRequest.getDestinationBucketName(), 
                                                copyObjectRequest.getDestinationKey());

        ResponseMessage response = send(request, context, true);
        
        try {
            return ResponseParser.parseCopyObjectResult(response.getRequestId(), response.getContent());
        } finally {
            safeCloseResponse(response);
        }
    }

    private void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest,
            Map<String, String> headers) {
        String sourceHeader = "/" + copyObjectRequest.getSourceBucketName() + "/"
                + copyObjectRequest.getSourceKey();
        headers.put(OSSHeaders.COPY_OBJECT_SOURCE, sourceHeader);

        addDateHeader(headers,
                OSSHeaders.COPY_OBJECT_SOURCE_IF_MODIFIED_SINCE,
                copyObjectRequest.getModifiedSinceConstraint());
        addDateHeader(headers,
                OSSHeaders.COPY_OBJECT_SOURCE_IF_UNMODIFIED_SINCE,
                copyObjectRequest.getUnmodifiedSinceConstraint());

        addListHeader(headers,
                OSSHeaders.COPY_OBJECT_SOURCE_IF_MATCH,
                copyObjectRequest.getMatchingETagConstraints());
        addListHeader(headers,
                OSSHeaders.COPY_OBJECT_SOURCE_IF_NONE_MATCH,
                copyObjectRequest.getNonmatchingEtagConstraints());

        addHeader(headers, 
               OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, 
               copyObjectRequest.getServerSideEncryption());

        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata != null){
            headers.put(OSSHeaders.COPY_OBJECT_METADATA_DIRECTIVE, "REPLACE");
            populateRequestMetadata(headers, newObjectMetadata);
        }
    }

    /**
     * delete object
     * */
    public void deleteObject(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                                        .setEndpoint(getEndpoint())
                                        .setMethod(HttpMethod.DELETE)
                                        .setBucket(bucketName)
                                        .setKey(key)
                                        .build();
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, key);

        send(request, context);

    }

}
