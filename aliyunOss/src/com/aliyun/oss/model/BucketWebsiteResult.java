/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

public class BucketWebsiteResult {
	private String indexDocument;
	private String errorDocument;
	
	
	public String getIndexDocument() {
		return indexDocument;
	}
	public void setIndexDocument(String indexDocument) {
		this.indexDocument = indexDocument;
	}
	public String getErrorDocument() {
		return errorDocument;
	}
	public void setErrorDocument(String errorDocument) {
		this.errorDocument = errorDocument;
	}
}
