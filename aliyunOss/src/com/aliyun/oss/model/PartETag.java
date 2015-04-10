/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 包含Multipart上传的Part的返回结果信息。
 *
 */
public class PartETag {

    private int partNumber;

    private String eTag;

    /**
     * 构造函数。
     * @param partNumber
     *          Part标识号码。
     * @param eTag
     *          Part的ETag值。
     */
    public PartETag(int partNumber, String eTag) {
        this.partNumber = partNumber;
        this.eTag = eTag;
    }

    /**
     * 返回Part标识号码。
     * @return Part标识号码。
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置Part标识号码。
     * @param partNumber
     *          Part标识号码。
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 返回Part的ETag值。
     * @return Part的ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置Part的ETag值。
     * @param eTag
     *          Part的ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

}
