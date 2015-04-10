/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;

/**
 * 定义了可以被授权的一组OSS用户。
 */
public enum GroupGrantee implements Grantee{
    /**
     * 表示为OSS的{@link Bucket}或{@link OSSObject}指定匿名访问的权限。
     * 任何用户都可以根据被授予的权限进行访问。
     */
    AllUsers("http://oss.service.aliyun.com/acl/group/ALL_USERS");

    private String groupUri;

    private GroupGrantee(String groupUri){
        this.groupUri = groupUri;
    }

    /**
     * 获取被授权者的ID。
     */
    public String getIdentifier(){
        return this.groupUri;
    }

    /**
     * 不支持该操作。
     */
    public void setIdentifier(String id){
        throw new UnsupportedOperationException(OSS_RESOURCE_MANAGER.getString("GroupGranteeNotSupportId"));
    }
}
