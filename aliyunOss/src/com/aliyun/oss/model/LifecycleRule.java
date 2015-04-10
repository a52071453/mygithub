/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.Date;

/**
 * 表示一条Lifecycle规则。
 */
public class LifecycleRule {
	
	public static enum RuleStatus
    {
		Unknown,
        Enabled,    // 启用规则
        Disabled    // 禁用规则
    };
	
	private String id;
	private String prefix;
	private RuleStatus status;
	private int expriationDays;
	private Date expirationTime;
	
	public LifecycleRule() {
		status = RuleStatus.Unknown;
	}
	
	public LifecycleRule(String id, String prefix, RuleStatus status,
			int expriationDays) {
		this.id = id;
		this.prefix = prefix;
		this.status = status;
		this.expriationDays = expriationDays;
	}

	public LifecycleRule(String id, String prefix, RuleStatus status,
			Date expirationTime) {
		this.id = id;
		this.prefix = prefix;
		this.status = status;
		this.expirationTime = expirationTime;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public RuleStatus getStatus() {
		return status;
	}
	
	public void setStatus(RuleStatus status) {
		this.status = status;
	}
	
	public int getExpriationDays() {
		return expriationDays;
	}
	
	public void setExpriationDays(int expriationDays) {
		this.expriationDays = expriationDays;
	}
	
	public Date getExpirationTime() {
		return expirationTime;
	}
	
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
}
