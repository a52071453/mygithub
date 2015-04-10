/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class SetBucketCORSRequest extends WebServiceRequest {

	private String bucketName;
	private List<CORSRule> corsRules=new ArrayList<SetBucketCORSRequest.CORSRule>();

	public static class CORSRule {
		
		private List<String> allowedOrigins = new ArrayList<String>();
		private List<String> allowedMethods = new ArrayList<String>();
		private List<String> allowedHeaders = new ArrayList<String>();
		private List<String> exposeHeaders = new ArrayList<String>();
		private Integer maxAgeSeconds;
		
		public void addAllowedOrigin(String allowedOrigin){
		    this.allowedOrigins.add(allowedOrigin);
		}
		
		public void addAllowedMethod(String allowedMethod){
		    this.allowedMethods.add(allowedMethod);
		}
		
		
		public List<String> getAllowedOrigins() {
			return allowedOrigins;
		}

		public void setAllowedOrigins(List<String> allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}

		public List<String> getAllowedHeaders() {
			return allowedHeaders;
		}

		public void setAllowedHeaders(List<String> allowedHeaders) {
			this.allowedHeaders = allowedHeaders;
		}

		public List<String> getAllowedMethods() {
			return allowedMethods;
		}

		public void setAllowedMethods(List<String> allowedMethods) {
			this.allowedMethods = allowedMethods;
		}

		public List<String> getExposeHeaders() {
			return exposeHeaders;
		}

		public void setExposeHeaders(List<String> exposeHeaders) {
			this.exposeHeaders = exposeHeaders;
		}

        public Integer getMaxAgeSeconds() {
            return maxAgeSeconds;
        }

        public void setMaxAgeSeconds(Integer maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
        }
	}
	
	public void addCorsRule(CORSRule corsRule)
	{
		this.corsRules.add(corsRule);
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public List<CORSRule> getCorsRules() {
		return corsRules;
	}

	public void setCorsRules(List<CORSRule> corsRules) {
		this.corsRules = corsRules;
	}
}
