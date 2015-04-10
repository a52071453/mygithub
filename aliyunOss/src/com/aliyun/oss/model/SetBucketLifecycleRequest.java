/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class SetBucketLifecycleRequest extends WebServiceRequest {
    private String bucketName;
	private List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();
	
	private static final int LifecycleRuleLimit = 1000;

	public SetBucketLifecycleRequest(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public List<LifecycleRule> getLifecycleRules() {
		return lifecycleRules;
	}

	public void setLifecycleRules(List<LifecycleRule> lifecycleRules) {
		if (lifecycleRules.size() > LifecycleRuleLimit) {
			throw new IllegalArgumentException("One bucket not allow exceed one thousand items of LifecycleRules.");
		}
		
		this.lifecycleRules = lifecycleRules;
	}
	
	public void AddLifecycleRule(LifecycleRule lifecycleRule)
	{
		if (lifecycleRule == null) {
			throw new IllegalArgumentException("lifecycleRule should not be null or empty.");
		}
		
		if (this.lifecycleRules.size() >= LifecycleRuleLimit) {
			throw new IllegalArgumentException("One bucket not allow exceed one thousand items of LifecycleRules.");
		}
		
		boolean hasSetExpirationTime = (lifecycleRule.getExpirationTime() != null);
		boolean hasSetExpirationDays =(lifecycleRule.getExpriationDays() != 0);
		if ((!hasSetExpirationTime && !hasSetExpirationDays) 
				|| (hasSetExpirationTime && hasSetExpirationDays)) {
			throw new IllegalArgumentException("Only one expiration property should be specified.");
		}
		
		if (lifecycleRule.getStatus() == LifecycleRule.RuleStatus.Unknown) {
			throw new IllegalArgumentException("RuleStatus property should be specified with 'Enabled' or 'Disabled'.");
		}
		
		this.lifecycleRules.add(lifecycleRule);
	}
}





