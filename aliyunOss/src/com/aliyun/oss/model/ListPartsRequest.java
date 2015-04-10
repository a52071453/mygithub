/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 包含列出Part的请求参数。
 *
 */
public class ListPartsRequest extends WebServiceRequest {

    private String bucketName;

    private String key;

    private String uploadId;

    private Integer maxParts;

    private Integer partNumberMarker;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param uploadId
     *          Mutlipart上传事件的Upload ID。
     */
    public ListPartsRequest(String bucketName, String key, String uploadId) {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
    }

    /**
     * 返回{@link Bucket}名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置{@link Bucket}名称。
     * @param bucketName
     *          Bucket名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回{@link OSSObject} key。
     * @return Object key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置{@link OSSObject} key。
     * @param key
     *          Object key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回标识Multipart上传事件的Upload ID。
     * @return 标识Multipart上传事件的Upload ID。
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 设置标识Multipart上传事件的Upload ID。
     * @param uploadId
     *          标识Multipart上传事件的Upload ID。
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 返回一个值表示最大返回多少条记录。（默认值1000）
     * @return 最大返回多少条记录。
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * 设置一个值最大返回多少条记录。（可选）
     * 最大值和默认值均为1000。
     * @param maxParts
     *          最大返回多少条记录。
     */
    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * 返回一个值表示从哪个Part号码开始获取列表。
     * @return 表示从哪个Part号码开始获取列表。
     */
    public Integer getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * 设置一个值表示从哪个Part号码开始获取列表。
     * @param partNumberMarker
     *          表示从哪个Part号码开始获取列表。
     */
    public void setPartNumberMarker(Integer partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
    }

}
