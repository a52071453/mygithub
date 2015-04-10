/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

public class SetBucketLoggingRequest extends WebServiceRequest {
	
    private String bucketName;
	private String targetBucket;
	private String targetPrefix;
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getTargetBucket() {
		return targetBucket;
	}
	public void setTargetBucket(String targetBucket) {
		this.targetBucket = targetBucket;
	}
	public String getTargetPrefix() {
		return targetPrefix;
	}
	public void setTargetPrefix(String targetPrefix) {
		this.targetPrefix = targetPrefix;
	}
}
