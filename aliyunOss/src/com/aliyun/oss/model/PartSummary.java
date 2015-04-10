/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.Date;

/**
 * 包含通过Multipart上传模式上传的Part的摘要信息。
 *
 */
public class PartSummary {

    private int partNumber;

    private Date lastModified;

    private String eTag;

    private long size;
    
    /**
     * 构造函数。
     */
    public PartSummary(){
    }

    /**
     * 返回Part的标识号码。
     * @return Part的标识号码。
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置Part的标识号码。
     * @param partNumber
     *          Part的标识号码。
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 返回Part的最后修改时间。
     * @return Part的最后修改时间。
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * 设置Part的最后修改时间。
     * @param lastModified
     *          Part的最后修改时间。
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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

    /**
     * 返回Part数据的字节数。
     * @return Part数据的字节数。
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置Part数据的字节数。
     * @param size
     *          Part数据的字节数。
     */
    public void setSize(long size) {
        this.size = size;
    }

}
