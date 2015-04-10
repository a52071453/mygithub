package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

public class BucketList {
   
    private List<Bucket> buckets = new ArrayList<Bucket>();

    private String prefix;
    
    private String marker;
    
    private Integer maxKeys;

    private boolean isTruncated;

    private String nextMarker;

    public List<Bucket> getBucketList() {
        return buckets;
    }
    
    public void setBucketList(List<Bucket> buckets) {
       this.buckets = buckets;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Integer getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {
        this.maxKeys = maxKeys;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    public String getNextMarker() {
        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }
}
