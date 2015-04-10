/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.iterable;

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;

import java.util.Iterator;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

public class OSSObjects implements Iterable<OSSObjectSummary> {
    
    private OSS oss;
    private ObjectListing objectListing;
    
    private OSSObjects(OSS oss, ObjectListing objectListing) {
        assertParameterNotNull(oss, "oss");
        assertParameterNotNull(objectListing, "objectListing");
        assertParameterNotNull(objectListing.getBucketName(), "objectListing.bucketName");

        this.oss = oss;
        this.objectListing = objectListing;
    }
    
    /**
     * 构造一个OSSObjects对象用来遍历{@link OSS#listObjects(com.aliyun.oss.model.ListObjectsRequest)}接口请求的object     * 
     * 
     * @param oss
     *          实现{@link OSS}接口的OSS的Client
     * @param objectListing
     *          请求OSS的listObjects服务返回的{@link ObjectListing}对象
     * @return 构造的OSSObjects对象
     */
    public static OSSObjects withObjectListing(OSS oss, ObjectListing objectListing) {
        return new OSSObjects(oss, objectListing);
    }
    

    @Override
    public Iterator<OSSObjectSummary> iterator() {
        return new OSSObjectIterator();
    }
    
    private class OSSObjectIterator implements Iterator<OSSObjectSummary> {
        
        private ObjectListing currentListing = objectListing;
        
        private Iterator<OSSObjectSummary> currentIter = objectListing.getObjectSummaries().iterator();

        @Override
        public boolean hasNext() {
            prepare();
            return currentIter.hasNext();
        }

        @Override
        public OSSObjectSummary next() {
            prepare();
            return currentIter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private void prepare() {
            if (currentListing.isTruncated() && !currentIter.hasNext()) {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(objectListing.getBucketName());
                listObjectsRequest.setDelimiter(currentListing.getDelimiter());
                listObjectsRequest.setMarker(currentListing.getNextMarker());
                listObjectsRequest.setMaxKeys(currentListing.getMaxKeys());
                listObjectsRequest.setPrefix(currentListing.getPrefix());
                ObjectListing nextObjectListing = oss.listObjects(listObjectsRequest);
                
                currentListing = nextObjectListing;
                currentIter = nextObjectListing.getObjectSummaries().iterator();
            }
        }
        
    }

}
