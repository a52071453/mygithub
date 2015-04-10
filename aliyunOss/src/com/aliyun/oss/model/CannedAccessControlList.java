/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 表示一组常用的用户访问权限。
 * <p>
 * 这一组常用权限相当于给所有用户指定权限的快捷方法。
 * </p>
 *
 */
public enum CannedAccessControlList {

    /**
     * 指定只有所有者具有完全控制权限 {@link Permission#FullControl}，
     * 其他用户{@link GroupGrantee#AllUsers}无权访问。
     */
    Private("private"),

    /**
     * 指定所有者具有完全控制权限 {@link Permission#FullControl}，
     * 其他用户{@link GroupGrantee#AllUsers}只有只读权限 {@link Permission#Read}。
     */
    PublicRead("public-read"),

    /**
     * 指定所有者和其他用户{@link GroupGrantee#AllUsers}均有完全控制权限{@link Permission#FullControl}。
     * 不推荐使用。
     */
    PublicReadWrite("public-read-write");

    private String cannedAclString;

    private CannedAccessControlList(String cannedAclString){
        this.cannedAclString = cannedAclString;
    }

    @Override
    public String toString() {
        return this.cannedAclString;
    }
    
    public static CannedAccessControlList parse(String acl){
        for(CannedAccessControlList cacl : CannedAccessControlList.values()){
            if (cacl.toString().equals(acl)){
                return cacl;
            }
        }
        
        return null;
    }
}
