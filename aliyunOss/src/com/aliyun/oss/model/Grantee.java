/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 表示被授权者的信息。
 *
 */
public interface Grantee {

    /**
     * 返回被授权者的ID。
     * @return 被授权者的ID。
     */
    public String getIdentifier();

    /**
     * 设置被授权者的ID。
     * @param id
     *          被授权者的ID。
     */
    public void setIdentifier(String id);
}
