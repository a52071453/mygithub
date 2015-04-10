/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 上传object操作的返回结果。
 */
public class PutObjectResult {

    // Object的ETag值。
    private String eTag;

    /**
     * 构造函数。
     */
    public PutObjectResult(){
    }

    /**
     * 返回新创建的{@link OSSObject}的ETag值。
     * @return 新创建的{@link OSSObject}的ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置新创建的{@link OSSObject}的ETag值。
     * @param eTag
     *          新创建的{@link OSSObject}的ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
