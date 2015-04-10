/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * Post Policy Conditions匹配方式。
 */
public enum MatchMode {
	Unknown,
	Exact,  // 精确匹配
    StartWith,  // Starts With
    Range   // 指定文件大小
}
