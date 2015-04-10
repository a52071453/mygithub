/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 包含完成一个Multipart上传事件的返回结果。
 *
 */
public class CompleteMultipartUploadResult{

    /** The name of the bucket containing the completed multipart upload. */
    private String bucketName;

    /** The key by which the object is stored. */
    private String key;

    /** The URL identifying the new multipart object. */
    private String location;

    private String eTag;
    
    /**
     * 构造函数。
     */
    public CompleteMultipartUploadResult(){
    }

    /**
     * 返回标识Multipart上传的{@link OSSObject}的URL地址。
     * @return 标识Multipart上传的{@link OSSObject}的URL地址。
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置标识Multipart上传的{@link OSSObject}的URL地址。
     * @param location
     *          标识Multipart上传的{@link OSSObject}的URL地址。
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 返回包含Multipart上传的{@link OSSObject}的{@link Bucket}名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置包含Multipart上传的{@link OSSObject}的{@link Bucket}名称。
     * @param bucketName
     *          Bucket名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回新创建的{@link OSSObject}的Key。
     * @return 新创建的{@link OSSObject}的Key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置新创建的{@link OSSObject}的Key。
     * @param key
     *          新创建的{@link OSSObject}的Key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回ETag值。
     * @return ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置ETag值。
     * @param etag ETag值。
     */
    public void setETag(String etag) {
        this.eTag = etag;
    }

}
