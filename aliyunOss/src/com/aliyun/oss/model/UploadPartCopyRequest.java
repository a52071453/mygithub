/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

public class UploadPartCopyRequest extends WebServiceRequest {
	 private String bucketName;

	    private String key;

	    private String uploadId;

	    private int partNumber;

	    private long partSize;

	    private String md5Digest;
        
	    private String sourceKey;
	    
	    private long beginIndex; 
	    
	    
	    /**
	     * 构造函数。
	     */
	    public UploadPartCopyRequest(){
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
	     * 返回上传分块（Part）的标识号码（Part Number）。
	     * 每一个上传分块（Part）都有一个标识它的号码（范围1~10000）。
	     * 对于同一个Upload ID，该号码不但唯一标识这一块数据，也标识了这块数据在整个文件中的
	     * 相对位置。如果你用同一个Part号码上传了新的数据，那么OSS上已有的这个号码的Part数据
	     * 将被覆盖。
	     * @return 上传分块（Part）的标识号码（Part Number）。
	     */
	    public int getPartNumber() {
	        return partNumber;
	    }

	    /**
	     * 设置上传分块（Part）的标识号码（Part Number）。
	     * 每一个上传分块（Part）都有一个标识它的号码（范围1~10000）。
	     * 对于同一个Upload ID，该号码不但唯一标识这一块数据，也标识了这块数据在整个文件中的
	     * 相对位置。如果你用同一个Part号码上传了新的数据，那么OSS上已有的这个号码的Part数据
	     * 将被覆盖。
	     * @param partNumber
	     *          上传分块（Part）的标识号码（Part Number）。
	     */
	    public void setPartNumber(int partNumber) {
	        this.partNumber = partNumber;
	    }

	    /**
	     * 返回分块（Part）数据的字节数。
	     * 除最后一个Part外，其他Part最小为5MB。
	     * @return 分块（Part）数据的字节数。
	     */
	    public long getPartSize() {
	        return partSize;
	    }

	    /**
	     * 设置返回分块（Part）数据的字节数。
	     * 除最后一个Part外，其他Part最小为5MB。
	     * @param partSize
	     *          分块（Part）数据的字节数。
	     */
	    public void setPartSize(long partSize) {
	        this.partSize = partSize;
	    }

	    /**
	     * 返回分块（Part）数据的MD5校验值。
	     * @return 分块（Part）数据的MD5校验值。
	     */
	    public String getMd5Digest() {
	        return md5Digest;
	    }

	    /**
	     * 设置分块（Part）数据的MD5校验值。
	     * @param md5Digest
	     *          分块（Part）数据的MD5校验值。
	     */
	    public void setMd5Digest(String md5Digest) {
	        this.md5Digest = md5Digest;
	    }

		public String getSourceKey() {
			return sourceKey;
		}

		public void setSourceKey(String sourceKey) {
			this.sourceKey = sourceKey;
		}

		public long getBeginIndex() {
			return beginIndex;
		}

		public void setBeginIndex(long beginIndex) {
			this.beginIndex = beginIndex;
		}
}
