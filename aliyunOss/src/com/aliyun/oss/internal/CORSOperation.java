/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.assertListNotNullOrEmpty;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.CodingUtils.assertStringNotNullOrEmpty;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.common.auth.ServiceCredentials;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.model.OptionsRequest;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

/**
 * 跨域资源共享操作类
 */
public class CORSOperation extends OSSOperation {
    
	public CORSOperation(URI endpoint, ServiceClient client, ServiceCredentials cred) {
		super(endpoint, client, cred);
	}

	public void setBucketCORS(SetBucketCORSRequest setBucketCORSRequest) {
		String bucketName = setBucketCORSRequest.getBucketName();
		List<CORSRule> corsRules = setBucketCORSRequest.getCorsRules();
		
		assertCreateBucketCORSRequestValid(setBucketCORSRequest);

		String xmlBody = buildBucketCORSXml(corsRules);
		byte[] inputBytes = xmlBody.getBytes();
		 Map<String, String> parameters = new LinkedHashMap<String, String>();
		 parameters.put("cors", null);
		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
				.setEndpoint(getEndpoint())
				.setMethod(HttpMethod.PUT)
				.setBucket(bucketName)
				.setParameters(parameters)
				.setInputStream(new ByteArrayInputStream(inputBytes))
				.setInputSize(inputBytes.length).build();

		ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);
		send(request, context);
	}
	
	/**
	 * list bucket CORS rules of the bucket
	 * @param bucketName
	 * @return
	 */
	public List<CORSRule> getBucketCORSRules(String bucketName){
		
		assertParameterNotNull(bucketName, "bucketName");
		 Map<String, String> parameters = new LinkedHashMap<String, String>();
         parameters.put("cors", null);
         
         ResponseMessage response = null;
         try{
		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		.setEndpoint(getEndpoint())
		.setMethod(HttpMethod.GET)
		.setParameters(parameters)
		.setBucket(bucketName).build();

		ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);
		
		response  = send(request, context,true);
		
        return ResponseParser.parseListBucketCORS(response.getRequestId(), response.getContent());
		}finally {
            if (response != null){
                safeCloseResponse(response);
            }
        }
	}
	
	/**
	 * delete all the CORS rules of the specified bucket. 
	 * @param bucketName
	 */
	public void deleteBucketCORS(String bucketName){
		assertParameterNotNull(bucketName, "bucketName");
		Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("cors", null);
		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
				.setEndpoint(getEndpoint())
				.setMethod(HttpMethod.DELETE)
				.setParameters(parameters)
				.setBucket(bucketName)
				.build();
		ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);
		send(request, context);
	}
	
	/**
	 * 判断跨域规则是否合适
	 * @param optionsRequest
	 * @return
	 */
	public ResponseMessage optionsObject(OptionsRequest optionsRequest){
		String bucketName = optionsRequest.getBucketName();
		assertParameterNotNull(bucketName, "bucketName");
		RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
				.setEndpoint(getEndpoint())
				.setMethod(HttpMethod.OPTIONS)
				.setBucket(bucketName)
				.setKey(optionsRequest.getObjectName())
				.addHeader(OSSHeaders.ORIGIN, optionsRequest.getOrigin())
				.addHeader(OSSHeaders.ACCESS_CONTROL_REQUEST_METHOD, optionsRequest.getRequestMethod().name())
				.addHeader(OSSHeaders.ACCESS_CONTROL_REQUEST_HEADER, optionsRequest.getRequestHeaders())
				.build();
		ExecutionContext context = createDefaultContext(request.getMethod(), bucketName);
		return send(request, context);			
	}
	
	private String buildBucketCORSXml(List<CORSRule> corsRules) {
		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append("<CORSConfiguration>");
		for (CORSRule rule : corsRules) {
			sbBuffer.append("<CORSRule>");
			
			for (String allowedOrigin : rule.getAllowedOrigins()) {
				sbBuffer.append("<AllowedOrigin>" + allowedOrigin + "</AllowedOrigin>");
			}
			
			for (String allowedMethod : rule.getAllowedMethods()) {
				sbBuffer.append("<AllowedMethod>" + allowedMethod + "</AllowedMethod>");
			}
			
			if(rule.getAllowedHeaders().size()>0){
				for(String allowedHeader : rule.getAllowedHeaders()){
					sbBuffer.append("<AllowedHeader>" +allowedHeader + "</AllowedHeader>");
				}
			}
			
			if(rule.getExposeHeaders().size()>0){
				for(String exposeHeader : rule.getExposeHeaders()){
					sbBuffer.append("<ExposeHeader>" +exposeHeader + "</ExposeHeader>");
				}
			}
			if(null!=rule.getMaxAgeSeconds()){
				sbBuffer.append("<MaxAgeSeconds>" +rule.getMaxAgeSeconds() + "</MaxAgeSeconds>");
			}
			
			sbBuffer.append("</CORSRule>");
		}
		sbBuffer.append("</CORSConfiguration>");
		return sbBuffer.toString();
	}
	
	private void assertCreateBucketCORSRequestValid(SetBucketCORSRequest request){
		String bucketName = request.getBucketName();
		List<CORSRule> corsRules = request.getCorsRules();
		
		assertStringNotNullOrEmpty(bucketName, "bucketName");
		assertListNotNullOrEmpty(corsRules, "corsRules");
		
		for(CORSRule rule : request.getCorsRules()){
			assertListNotNullOrEmpty(rule.getAllowedOrigins(), "allowedOrigin");
			assertListNotNullOrEmpty(rule.getAllowedMethods(), "allowedMethod");
		}
	}
	
	
}
