/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.CodingUtils.assertStringNotNullOrEmpty;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.comm.DefaultServiceClient;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.internal.CORSOperation;
import com.aliyun.oss.internal.OSSBucketOperation;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.OSSMultipartOperation;
import com.aliyun.oss.internal.OSSObjectOperation;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.internal.SignUtils;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListMultipartUploadsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ListPartsRequest;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OptionsRequest;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * 访问阿里云开放存储服务（Open Storage Service， OSS）的入口类。
 */
public class OSSClient implements OSS {

	// 用户身份信息。
	private ServiceCredentials credentials = new ServiceCredentials();

	// OSS 服务的地址。
	private URI endpoint;

	// 访问OSS服务的client
	private ServiceClient serviceClient;

	// OSS Operations.
	private OSSBucketOperation bucketOperation;
	private OSSObjectOperation objectOperation;
	private OSSMultipartOperation multipartOperation;
	private CORSOperation corsOperation;

	/**
	 * 使用默认的OSS Endpoint构造一个新的{@link OSSClient}对象。
	 * 
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param accessKeySecret
	 *            访问OSS的Access Key Secret。
	 */
	public OSSClient(String accessKeyId, String accessKeySecret) {
		this(OSSConstants.DEFAULT_OSS_ENDPOINT, accessKeyId, accessKeySecret, null);
	}

	/**
	 * 使用指定的OSS Endpoint构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。必须以"http://"开头。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param accessKeySecret
	 *            访问OSS的Access Key Secret。
	 */
	public OSSClient(String endpoint, String accessKeyId, String accessKeySecret) {
		this(endpoint, accessKeyId, accessKeySecret, null);
	}

	/**
	 * 使用指定的OSS Endpoint和配置构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。必须以"http://"开头。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param accessKeySecret
	 *            访问OSS的Access Key Secret。
	 * @param config
	 *            客户端配置 {@link ClientConfiguration}。 如果为null则会使用默认配置。
	 */
	public OSSClient(String endpoint, String accessKeyId, String accessKeySecret, ClientConfiguration config) {

		assertStringNotNullOrEmpty(endpoint, "endpoint");

		try {
			if (!endpoint.startsWith("http://")) {
				throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("EndpointProtocolInvalid"));
			}
			this.endpoint = new URI(endpoint);

		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}

		this.credentials = new ServiceCredentials(accessKeyId, accessKeySecret);
		this.serviceClient = new DefaultServiceClient(config != null ? config : new ClientConfiguration());

		// 创建oss 操作类
		bucketOperation = new OSSBucketOperation(this.endpoint, this.serviceClient, this.credentials);
		objectOperation = new OSSObjectOperation(this.endpoint, this.serviceClient, this.credentials);
		multipartOperation = new OSSMultipartOperation(this.endpoint, this.serviceClient, this.credentials);
		corsOperation = new CORSOperation(this.endpoint, this.serviceClient, this.credentials);
	}

	/**
	 * 返回访问的OSS Endpoint。
	 * 
	 * @return OSS Endpoint。
	 */
	public URI getEndpoint() {
		return endpoint;
	}

	/**
	 * 返回使用的Access Key ID。
	 * 
	 * @return 使用的Access Key ID。
	 */
	public String getAccessKeyId() {
		return credentials.getAccessKeyId();
	}

	/**
	 * 返回使用的Access Key Secret。
	 * 
	 * @return 使用的Access Key Secret。
	 */
	public String getAccessKeySecret() {
		return credentials.getAccessKeySecret();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#createBucket(java.lang.String)
	 */
	@Override
	public Bucket createBucket(String bucketName) throws OSSException, ClientException {

		return this.createBucket(new CreateBucketRequest(bucketName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#createBucket(com.aliyun.api.
	 * oss.model.ListObjectsRequest.CreateBucketRequest)
	 */
	@Override
	public Bucket createBucket(CreateBucketRequest createBucketRequest) {
		return bucketOperation.createBucket(createBucketRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#deleteBucket(java.lang.String)
	 */
	@Override
	public void deleteBucket(String bucketName) throws OSSException, ClientException {

		bucketOperation.deleteBucket(bucketName);
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#listBuckets()
	 */
	@Override
    public List<Bucket> listBuckets() throws OSSException, ClientException {
        BucketList bucketList = bucketOperation.listBuckets(new ListBucketsRequest(null, null, null));
        List<Bucket> buckets = bucketList.getBucketList();
        while (bucketList.isTruncated())
        {
            bucketList = bucketOperation.listBuckets(new ListBucketsRequest(null, bucketList.getNextMarker(), null));
            buckets.addAll(bucketList.getBucketList());
        }
        return buckets;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aliyun.oss.OSS#listBuckets(com.aliyun.oss.model.ListBucketsRequest)
     */
    @Override
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) throws OSSException, ClientException {
        return bucketOperation.listBuckets(listBucketsRequest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aliyun.oss.OSS#listBuckets(java.lang.String,
     * java.lang.String,java.lang.Integer)
     */
    @Override
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) throws OSSException, ClientException {
        return bucketOperation.listBuckets(new ListBucketsRequest(prefix, marker, maxKeys));
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#setBucketAcl(java.lang.String,
	 * com.aliyun.oss.model.CannedAccessControlList)
	 */
	@Override
	public void setBucketAcl(String bucketName, CannedAccessControlList acl) throws OSSException, ClientException {

		bucketOperation.setBucketAcl(bucketName, acl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#getBucketAcl(java.lang.String)
	 */
	@Override
	public AccessControlList getBucketAcl(String bucketName) throws OSSException, ClientException {

		return bucketOperation.getBucketAcl(bucketName);
	}
 	
 	/*
 	 * (non-Javadoc)
 	 * 
 	 * @see com.aliyun.oss.OSS#setBucketReferer(java.lang.String,
 	 * com.aliyun.oss.model.BucketReferer)
 	 */
 	@Override
 	public void setBucketReferer(String bucketName, BucketReferer referer) throws OSSException, ClientException {
 		bucketOperation.setBucketReferer(bucketName, referer);
 	}
  

	/*
	 * (non-Javadoc)
	 * 
 	 * @see com.aliyun.oss.OSS#getBucketBucketReferer(java.lang.String)
 	 */
 	@Override
 	public BucketReferer getBucketReferer(String bucketName) throws OSSException, ClientException {
 		return bucketOperation.getBucketReferer(bucketName);
 	}
 	
 	/*
 	 * (non-Javadoc)
 	 * 
	 * @see com.aliyun.oss.OSS#getBucketLocation(java.lang.String)
	 */
	@Override
	public String getBucketLocation(String bucketName) throws OSSException, ClientException {
		return bucketOperation.getBucketLocation(bucketName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#isBucketExist(java.lang.String)
	 */
	@Override
	public boolean doesBucketExist(String bucketName) throws OSSException, ClientException {

		return bucketOperation.bucketExists(bucketName);
	}

	/**
	 * 已过时。请使用{@link OSSClient#doesBucketExist(String)}。
	 * 
	 * @param bucketName
	 * @return
	 * @throws OSSException
	 * @throws ClientException
	 */
	@Deprecated
	public boolean isBucketExist(String bucketName) throws OSSException, ClientException {
		return this.doesBucketExist(bucketName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#listObjects(java.lang.String)
	 */
	@Override
	public ObjectListing listObjects(String bucketName) throws OSSException, ClientException {

		return listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#listObjects(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ObjectListing listObjects(String bucketName, String prefix) throws OSSException, ClientException {

		return listObjects(new ListObjectsRequest(bucketName, prefix, null, null, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#listObjects(com.aliyun.oss
	 * .model.ListObjectsRequest)
	 */
	@Override
	public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws OSSException, ClientException {

		return bucketOperation.listObjects(listObjectsRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#putObject(java.lang.String,
	 * java.lang.String, java.io.InputStream,
	 * com.aliyun.oss.model.ObjectMetadata)
	 */
	@Override
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
			throws OSSException, ClientException {
		return objectOperation.putObject(bucketName, key, input, metadata);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> customHeaders)
			throws OSSException, ClientException {
		return putObject(signedUrl, filePath, customHeaders, false);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> customHeaders,
			boolean useChunkEncoding) throws OSSException, ClientException {
		if (!IOUtils.checkFile(filePath)) {
			throw new IllegalArgumentException(String.format("Illegal file path %s", filePath));
		}
		
		FileInputStream requestContent = null;
		try {
			File fileToUpload = new File(filePath);
			long fileSize = fileToUpload.length();
			requestContent = new FileInputStream(fileToUpload);
			
			return putObject(signedUrl, requestContent, fileSize, customHeaders, useChunkEncoding);
		} catch (FileNotFoundException e) {
			throw new ClientException(e);
		} finally {
			if (requestContent != null) {
				try {
					requestContent.close();
				} catch (IOException e) {
					//TODO:
				}
			}
		}
	}

	@Override
	public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
			Map<String, String> customHeaders) throws OSSException, ClientException {
		return putObject(signedUrl, requestContent, contentLength, customHeaders, false);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
			Map<String, String> customHeaders, boolean useChunkEncoding) throws OSSException, ClientException {
		return objectOperation.putObject(signedUrl, requestContent, contentLength, customHeaders, useChunkEncoding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#copyObject(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws OSSException, ClientException {

		return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#copyObject(com.aliyun.oss
	 * .model.CopyObjectRequest)
	 */
	@Override
	public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws OSSException, ClientException {

		return objectOperation.copyObject(copyObjectRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#getObject(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public OSSObject getObject(String bucketName, String key) throws OSSException, ClientException {

		return this.getObject(new GetObjectRequest(bucketName, key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#getObject(com.aliyun.oss
	 * .model.GetObjectRequest, java.io.File)
	 */
	@Override
	public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws OSSException, ClientException {

		return objectOperation.getObject(getObjectRequest, file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#getObject(com.aliyun.oss
	 * .model.GetObjectRequest)
	 */
	@Override
	public OSSObject getObject(GetObjectRequest getObjectRequest) throws OSSException, ClientException {

		return objectOperation.getObject(getObjectRequest);
	}

	@Override
	public OSSObject getObject(URL signedUrl, Map<String, String> customHeaders) 
			throws OSSException, ClientException
	{
		GetObjectRequest getObjectRequest = new GetObjectRequest(signedUrl, customHeaders);
		return objectOperation.getObject(getObjectRequest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#getObjectMetadata(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ObjectMetadata getObjectMetadata(String bucketName, String key) throws OSSException, ClientException {

		return objectOperation.getObjectMetadata(bucketName, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#deleteObject(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void deleteObject(String bucketName, String key) throws OSSException, ClientException {

		objectOperation.deleteObject(bucketName, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#generatePresignedUrl(java.lang.String,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws ClientException {
		return generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#generatePresignedUrl(java.lang.String,
	 * java.lang.String, java.util.Date, com.aliyun.api.HttpMethod)
	 */
	@Override
	public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
			throws ClientException {
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
		request.setExpiration(expiration);
		request.setMethod(method);

		return generatePresignedUrl(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#generatePresignedUrl(com.aliyun.api
	 * .oss.model.GeneratePresignedUrlRequest)
	 */
	@Override
	public URL generatePresignedUrl(GeneratePresignedUrlRequest request) throws ClientException {

		assertParameterNotNull(request, "request");
		if (request.getBucketName() == null) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetBucketName"));
		}
        OSSUtils.ensureBucketNameValid(request.getBucketName());
		if (request.getExpiration() == null) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetExpiration"));
		}

		String bucketName = request.getBucketName();
		String key = request.getKey();

		String accessId = credentials.getAccessKeyId();
		String accessKey = credentials.getAccessKeySecret();
		HttpMethod method = request.getMethod() != null ? request.getMethod() : HttpMethod.GET;

		String expires = String.valueOf(request.getExpiration().getTime() / 1000L);
		String resourcePath = OSSUtils.makeResourcePath(key);

		RequestMessage requestMessage = new RequestMessage();
		ClientConfiguration cc = serviceClient.getClientConfiguration();
		requestMessage.setEndpoint(OSSUtils.makeBukcetEndpoint(endpoint, bucketName, cc));
		requestMessage.setMethod(method);
		requestMessage.setResourcePath(resourcePath);
        requestMessage.addHeader(HttpHeaders.DATE, expires);
        if (request.getContentType() != null && request.getContentType().trim() != "") {
            requestMessage.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        }
        if (request.getContentMD5() != null && request.getContentMD5().trim() != "") {
            requestMessage.addHeader(HttpHeaders.CONTENT_MD5, request.getContentMD5());
        }
		for (Map.Entry<String, String> h : request.getUserMetadata().entrySet()) {
			requestMessage.addHeader(OSSHeaders.OSS_USER_METADATA_PREFIX + h.getKey(), h.getValue());
		}

		Map<String, String> responseHeadersParams = OSSUtils.getResponseHeaderParameters(request.getResponseHeaders());
		if (responseHeadersParams.size() > 0) {
			requestMessage.setParameters(responseHeadersParams);
		}

		if (request.getQueryParameter() != null && request.getQueryParameter().size() > 0) {
			for (Map.Entry<String, String> entry : request.getQueryParameter().entrySet()) {
				requestMessage.addParameter(entry.getKey(), entry.getValue());
			}
		}

		String canonicalResource = "/" + ((bucketName != null) ? bucketName : "") + ((key != null ? "/" + key : ""));
		String canonicalString = SignUtils.buildCanonicalString(method.toString(), canonicalResource, requestMessage,
				expires);
		String signature = ServiceSignature.create().computeSignature(accessKey, canonicalString);

		Map<String, String> params = new HashMap<String, String>();
        params.put(HttpHeaders.EXPIRES, expires);
		params.put("OSSAccessKeyId", accessId);
		params.put("Signature", signature);
		params.putAll(requestMessage.getParameters());

		// 生成URL
		String queryString;
		try {
			queryString = HttpUtil.paramToQueryString(params, OSSConstants.DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			throw new ClientException(OSS_RESOURCE_MANAGER.getString("FailedToEncodeUri"), e);
		}

		String url = requestMessage.getEndpoint().toString();
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += resourcePath + "?" + queryString;

		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new ClientException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#abortMultipartUpload(com.aliyun.api
	 * .oss.model.AbortMultipartUploadRequest)
	 */
	@Override
	public void abortMultipartUpload(AbortMultipartUploadRequest request) throws OSSException, ClientException {

		multipartOperation.abortMultipartUpload(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#completeMultipartUpload(com.aliyun.
	 * api.oss.model.CompleteMultipartUploadRequest)
	 */
	@Override
	public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
			throws OSSException, ClientException {
		return multipartOperation.completeMultipartUpload(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aliyun.oss.OSS#initiateMultipartUpload(com.aliyun.
	 * api.oss.model.InitiateMultipartUploadRequest)
	 */
	@Override
	public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
			throws OSSException, ClientException {

		return multipartOperation.initiateMultipartUpload(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#listMultipartUploads(com.aliyun.api
	 * .oss.model.ListMultipartUploadsRequest)
	 */
	@Override
	public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) throws OSSException,
			ClientException {

		return multipartOperation.listMultipartUploads(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#listParts(com.aliyun.oss
	 * .model.ListPartsRequest)
	 */
	@Override
	public PartListing listParts(ListPartsRequest request) throws OSSException, ClientException {

		return multipartOperation.listParts(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#uploadPart(com.aliyun.oss
	 * .model.UploadPartRequest)
	 */
	@Override
	public UploadPartResult uploadPart(UploadPartRequest request) throws OSSException, ClientException {

		return multipartOperation.uploadPart(request);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aliyun.oss.OSS#uploadPartCopy(com.aliyun.oss
	 * .model.UploadPartCopyRequest)
	 */
	@Override
	public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest request) throws OSSException, ClientException {
		return multipartOperation.uploadPartCopy(request);
	}

	@Override
	public void setBucketCORS(SetBucketCORSRequest request) throws OSSException, ClientException {
		corsOperation.setBucketCORS(request);

	}

	@Override
	public List<CORSRule> getBucketCORSRules(String bucketName) throws OSSException, ClientException {
		return corsOperation.getBucketCORSRules(bucketName);
	}

	@Override
	public void deleteBucketCORSRules(String bucketName) throws OSSException, ClientException {
		corsOperation.deleteBucketCORS(bucketName);
	}

	@Override
	public ResponseMessage optionsObject(OptionsRequest request) throws OSSException, ClientException {
		return corsOperation.optionsObject(request);
	}
    
	@Override
	public void setBucketLogging(SetBucketLoggingRequest request) 
			throws OSSException, ClientException {
		 bucketOperation.setBucketLogging(request);
	}
	
	@Override
	public BucketLoggingResult getBucketLogging(String bucketName) {
		return bucketOperation.getBucketLogging(bucketName);
	}
	
	@Override
	public void deleteBucketLogging(String bucketName) throws OSSException,
			ClientException {
		bucketOperation.deleteBucketLogging(bucketName);
	}

	@Override
	public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest)
			throws OSSException, ClientException {
		bucketOperation.setBucketWebsite(setBucketWebSiteRequest);
	}

	@Override
	public BucketWebsiteResult getBucketWebsite(String bucketName)
			throws OSSException, ClientException {
		return	bucketOperation.getBucketWebsite(bucketName);
	}

	@Override
	public void deleteBucketWebsite(String bucketName) throws OSSException,
			ClientException {
		bucketOperation.deleteBucketWebsite(bucketName);
	}
	
	@Override
	public String generatePostPolicy(Date expiration, PolicyConditions conds) {
		String formatedExpiration = DateUtil.formatIso8601Date(expiration);
		String jsonizedExpiration = String.format("\"expiration\":\"%s\"", formatedExpiration);
		String jsonizedConds = conds.jsonize();
        
        StringBuilder postPolicy = new StringBuilder();
        postPolicy.append("{");
        postPolicy.append(String.format("%s,%s", jsonizedExpiration, jsonizedConds));
        postPolicy.append("}");

        return postPolicy.toString();
	}
	
	@Override
	public String calculatePostSignature(String postPolicy) {
		try {
			byte[] binaryData = postPolicy.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
			String encPolicy = BinaryUtil.toBase64String(binaryData);
			return ServiceSignature.create().computeSignature(getAccessKeySecret(), encPolicy);
		} catch (UnsupportedEncodingException ex) {
			throw new ClientException("Unsupported charset: " + ex.getMessage());
		}
	}
	
	@Override
	public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest) {
		bucketOperation.setBucketLifecycle(setBucketLifecycleRequest);
	}
	
	@Override
	public List<LifecycleRule> getBucketLifecycle(String bucketName) {
		return bucketOperation.getBucketLifecycle(bucketName);
	}

	@Override
	public void deleteBucketLifecycle(String bucketName) {
		bucketOperation.deleteBucketLifecycle(bucketName);
	}
}
