/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

public class UploadPartCopyResult {
	private int partNumber;

    private String eTag;

    /**
     * 构造函数。
     */
    public UploadPartCopyResult(){
    }

    /**
     * 返回分块（Part）标识号。
     * @return 分块（Part）标识号。
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置分块（Part）标识号。
     * @param partNumber
     *          分块（Part）标识号。
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 返回OSS生成的ETag值。
     * <p>
     * OSS 会将服务器端收到 Part 数据的 MD5 值放在 ETag 头内返回给用户。为了
     * 保证数据在网络传输过程中不出现错误，强烈推荐用户在收到 OSS的返回请求后，
     * 用该MD5值验证上传数据的正确性。
     * </p>
     * @return ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置OSS生成的ETag值。
     * <p>
     * OSS 会将服务器端收到 Part 数据的 MD5 值放在 ETag 头内返回给用户。为了
     * 保证数据在网络传输过程中不出现错误，强烈推荐用户在收到 OSS的返回请求后，
     * 用该MD5值验证上传数据的正确性。
     * </p>
     * @param eTag
     *          ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * 返回包含Part标识号码和ETag值的{@link PartETag}对象。
     * @return 包含Part标识号码和ETag值的{@link PartETag}对象。
     */
    public PartETag getPartETag() {
        return new PartETag(partNumber, eTag);
    }
}
