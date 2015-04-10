/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import java.util.Date;

/**
 * 拷贝一个在OSS上已经存在的Object成另外一个Object的请求结果。
 */
public class CopyObjectResult {

    // 新Object的ETag值。
    private String etag;

    // 新Object的最后修改时间。
    private Date lastModified;

    /**
     * 初始化一个新的{@link CopyObjectResult}实例。
     */
    public CopyObjectResult(){
    }

    /**
     * 返回新Object的ETag值。
     * @return 新Object的ETag值。
     */
    public String getETag() {
        return etag;
    }

    /**
     * 设置新Object的ETag值。
     * @param etag
     *          新Object的ETag值。
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * 返回新Object的最后修改时间。
     * @return 新Object的最后修改时间。
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * 设置新Object的最后修改时间。
     * @param lastModified
     *          新Object的最后修改时间。
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}
