/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 包含列出所有执行中Multipart上传事件的请求参数。
 *
 */
public class ListMultipartUploadsRequest {

    private String bucketName;

    private String delimiter;

    private String prefix;

    private Integer maxUploads;

    private String keyMarker;

    private String uploadIdMarker;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     */
    public ListMultipartUploadsRequest(String bucketName) {
        this.bucketName = bucketName;
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
     * 返回限制的最大返回记录数。
     * @return 限制的最大返回记录数。
     */
    public Integer getMaxUploads() {
        return maxUploads;
    }

    /**
     * 设置限制的最大返回记录数。
     * 最大值和默认值均为1000。
     * @param maxUploads
     *          限制的最大返回记录数。
     */
    public void setMaxUploads(Integer maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * 返回一个标识表示从哪里返回列表。
     * @return 标识表示从哪里返回列表。
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 设置一个标识表示从哪里返回列表。（可选）
     * @param keyMarker
     *          标识表示从哪里返回列表。
     */
    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }
    
    /**
     * 返回一个标识表示从哪里返回列表。
     * @return 标识表示从哪里返回列表。
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    /**
     * 设置一个标识表示从哪里返回列表。（可选）
     * @param uploadIdMarker
     *          标识表示从哪里返回列表。
     */
    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
