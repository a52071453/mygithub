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
 * 包含完成一个Multipart上传事件的请求参数。
 *
 */
public class CompleteMultipartUploadRequest extends WebServiceRequest {

    /** The name of the bucket containing the multipart upload to complete */
    private String bucketName;

    /** The key of the multipart upload to complete */
    private String key;

    /** The ID of the multipart upload to complete */
    private String uploadId;

    /** The list of part numbers and ETags to use when completing the multipart upload */
    private List<PartETag> partETags = new ArrayList<PartETag>();

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param uploadId
     *          Mutlipart上传事件的Upload ID。
     * @param partETags
     *          标识上传Part结果的{@link PartETag}列表。
     */
    public CompleteMultipartUploadRequest(String bucketName, String key, String uploadId, List<PartETag> partETags) {
        this.bucketName = bucketName;
        this.key = key;
        this.uploadId = uploadId;
        this.partETags = partETags;
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
     * 返回标识上传Part结果的{@link PartETag}列表。
     * @return 标识上传Part结果的{@link PartETag}列表。
     */
    public List<PartETag> getPartETags() {
        return partETags;
    }

    /**
     * 设置标识上传Part结果的{@link PartETag}列表。
     * @param partETags
     *          标识上传Part结果的{@link PartETag}列表。
     */
    public void setPartETags(List<PartETag> partETags) {
        this.partETags = partETags;
    }
}
