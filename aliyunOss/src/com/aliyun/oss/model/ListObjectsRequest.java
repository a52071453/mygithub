/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

import com.aliyun.oss.internal.OSSUtils;

/**
 * 包含获取object列表的请求信息。
 */
public class ListObjectsRequest extends WebServiceRequest {

    // bucket 名称。
    private String bucketName;
    
    // prefix限定返回的object key必须以prefix作为前缀。
    private String prefix;
    
    // maker用户设定结果从marker之后按字母排序的第一个开始返回。
    private String marker;

    // 用于限定此次返回object的最大数，如果不设定，默认为100。
    private Integer maxKeys;
    
    // delimiter是一个用于对Object名字进行分组的字符。
    private String delimiter;

    /**
     * 构造函数。
     */
    public ListObjectsRequest(){
    }
    
    public ListObjectsRequest(String bucketName){
        this(bucketName, null, null, null, null);
    }
    
    /**
     * 构造函数。
     * @param bucketName
     *          bucket 名称。
     * @param prefix
     *          prefix限定返回的object key必须以prefix作为前缀。
     * @param marker
     *          maker用户设定结果从marker之后按字母排序的第一个开始返回。
     * @param maxKeys
     *          用于限定此次返回object的最大数，如果不设定，默认为100。
     * @param delimiter
     *          delimiter是一个用于对Object名字进行分组的字符。
     */
    public ListObjectsRequest(String bucketName, String prefix, String marker, String delimiter, Integer maxKeys){
        setBucketName(bucketName);
        setPrefix(prefix);
        setMarker(marker);
        setDelimiter(delimiter);
        if (maxKeys != null){
            setMaxKeys(maxKeys);
        }
    }

    /**
     * 返回bucket名称。
     * @return bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置bucket名称。
     * @param bucketName
     *          bucket名称。
     */
    public void setBucketName(String bucketName) {
        if (bucketName == null){
            throw new NullPointerException();
        }
        if (!OSSUtils.validateBucketName(bucketName)){
            throw new IllegalArgumentException();
        }
        this.bucketName = bucketName;
    }

    /**
     * 返回prefix，限定返回的object key必须以prefix作为前缀。
     * @return
     *      prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置prefix，限定返回的object key必须以prefix作为前缀。
     * @param prefix
     *          前缀prefix。
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 返回marker，用户设定结果从marker之后按字母排序的第一个开始返回。
     * @return
     *          marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * 设置marker, 用户设定结果从marker之后按字母排序的第一个开始返回。
     * @param marker
     *          marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * 返回用于限定此次返回object的最大数，如果不设定，默认为100。
     * @return
     *      用于限定此次返回object的最大数。
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * 设置用于限定此次返回object的最大数，如果不设定，默认为100。最大值为1000。
     * @param maxKeys
     *      用于限定此次返回object的最大数。最大值为1000。
     */
    public void setMaxKeys(Integer maxKeys) {
        if (maxKeys < 0 || maxKeys > 1000){
            throw new IllegalArgumentException(
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }

        this.maxKeys = maxKeys;
    }

    /**
     * 获取一个用于对Object名字进行分组的字符。
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 设置一个用于对Object名字进行分组的字符。
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
