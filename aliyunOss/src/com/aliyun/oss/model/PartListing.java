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
 * 包含Multipart上传Part的表示。
 *
 */
public class PartListing {

    private String bucketName;

    private String key;

    private String uploadId;

    private Integer maxParts;

    private Integer partNumberMarker;

    private Owner owner;

    private Owner initiator;

    private String storageClass;

    private boolean isTruncated;

    private Integer nextPartNumberMarker;

    private List<PartSummary> parts;

    /**
     * 构造函数。
     */
    public PartListing(){
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
     * 返回相关Multipart上传事件的所有者（{@link Owner}信息。
     * @return 相关Multipart上传事件的所有者（{@link Owner}信息。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置相关Multipart上传事件的所有者（{@link Owner}信息。
     * @param owner
     *          相关Multipart上传事件的所有者（{@link Owner}信息。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 返回相关Multipart上传事件的初始化者（{@link Owner}信息。
     * @return 相关Multipart上传事件的初始化者（{@link Owner}信息。
     */
    public Owner getInitiator() {
        return initiator;
    }

    /**
     * 设置相关Multipart上传事件的初始化者（{@link Owner}信息。
     * @param initiator
     *          相关Multipart上传事件的初始化者（{@link Owner}信息。
     */
    public void setInitiator(Owner initiator) {
        this.initiator = initiator;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * 返回请求中给定的{@link ListPartsRequest#getPartNumberMarker()}。
     * @return Part number marker。
     */
    public Integer getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * 设置请求中给定的{@link ListPartsRequest#getPartNumberMarker()}。
     * @param partNumberMarker
     *          Part number marker。
     */
    public void setPartNumberMarker(int partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
    }

    /**
     * 返回一个值表示如果返回结果被截取，那么下一个Part的号码是多少。
     * @return 值表示如果返回结果被截取，那么下一个Part的号码是多少。
     */
    public Integer getNextPartNumberMarker() {
        return nextPartNumberMarker;
    }

    /**
     * 设置一个值值表示如果返回结果被截取，那么下一个Part的号码是多少。
     * @param nextPartNumberMarker
     *          值表示如果返回结果被截取，那么下一个Part的号码是多少。
     */
    public void setNextPartNumberMarker(int nextPartNumberMarker) {
        this.nextPartNumberMarker = nextPartNumberMarker;
    }

    /**
     * 返回请求中指定返回Part的最大个数（{@link ListPartsRequest#getMaxParts()}）。
     * @return 返回Part的最大个数。
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * 设置请求中指定返回Part的最大个数（{@link ListPartsRequest#getMaxParts()}）。
     * @param maxParts
     *          返回Part的最大个数。返回Part的最大个数。
     */
    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * 返回一个值表示返回结果是否被截取，即是否还有其他记录没有返回。
     * @return 返回结果是否被截取，即是否还有其他记录没有返回。
     */
    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * 设置一个值表示返回结果是否被截取，即是否还有其他记录没有返回。
     * @param isTruncated
     *          返回结果是否被截取，即是否还有其他记录没有返回。
     */
    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    /**
     * 返回{@link PartSummary}的列表。
     * @return {@link PartSummary}的列表。
     */
    public List<PartSummary> getParts() {
        if (parts == null) parts = new ArrayList<PartSummary>();
        return parts;
    }

    /**
     * 设置{@link PartSummary}的列表。
     * @param parts
     *      {@link PartSummary}的列表。
     */
    public void setParts(List<PartSummary> parts) {
        this.parts = parts;
    }

}
