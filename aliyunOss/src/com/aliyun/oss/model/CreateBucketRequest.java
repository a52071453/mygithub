/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

public class CreateBucketRequest extends WebServiceRequest {
    
    private String bucketName;
    
    private String locationConstraint;
    
    public CreateBucketRequest(String bucketName) {
        setBucketName(bucketName);
    }

    /**
     * 获取要建立的Bucket的名称
     * @return Bucket的名称
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置要建立的Bucket的名称
     * @param bucketName Bucket的名称
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取Bucket所在数据中心
     * @return Bucket所在的数据中心
     */
    public String getLocationConstraint() {
        return locationConstraint;
    }

    /**
     * 设置Bucket所在的数据中心
     * @param locationConstraint Bucket所在的数据中心名称
     */
    public void setLocationConstraint(String locationConstraint) {
        this.locationConstraint = locationConstraint;
    }
}
