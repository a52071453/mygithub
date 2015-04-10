/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含Multipart上传事件的列表。
 *
 */
public class MultipartUploadListing extends WebServiceRequest {

    private String bucketName;

    private String keyMarker;

    private String delimiter;

    private String prefix;

    private String uploadIdMarker;

    private int maxUploads;

    private boolean isTruncated;

    private String nextKeyMarker;

    private String nextUploadIdMarker;

    private List<MultipartUpload> multipartUploads;

    private List<String> commonPrefixes = new ArrayList<String>();

    /**
     * 构造函数。
     */
    public MultipartUploadListing(){
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

    public String getKeyMarker() {
        return keyMarker;
    }

    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    public String getNextKeyMarker() {
        return nextKeyMarker;
    }

    public void setNextKeyMarker(String nextKeyMarker) {
        this.nextKeyMarker = nextKeyMarker;
    }

    public String getNextUploadIdMarker() {
        return nextUploadIdMarker;
    }

    public void setNextUploadIdMarker(String nextUploadIdMarker) {
        this.nextUploadIdMarker = nextUploadIdMarker;
    }

    public int getMaxUploads() {
        return maxUploads;
    }

    public void setMaxUploads(int maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * 返回一个值表示是否还有更多的记录没有返回。
     * @return 表示是否还有更多的记录没有返回。
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * 设置一个值表示是否还有更多的记录没有返回。
     * @param isTruncated
     *          表示是否还有更多的记录没有返回。
     */
    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    public List<MultipartUpload> getMultipartUploads() {
        if (multipartUploads == null) multipartUploads = new ArrayList<MultipartUpload>();
        return multipartUploads;
    }

    public void setMultipartUploads(List<MultipartUpload> multipartUploads) {
        this.multipartUploads = multipartUploads;
    }

    public List<String> getCommonPrefixes() {
        return commonPrefixes;
    }

    public void setCommonPrefixes(List<String> commonPrefixes) {
        this.commonPrefixes = commonPrefixes;
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
