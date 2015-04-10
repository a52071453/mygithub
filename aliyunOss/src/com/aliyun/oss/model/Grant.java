/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.model;

/**
 * 访问控制的授权信息。
 *
 */
public class Grant {
    private Grantee grantee;
    private Permission permission;
    
    /**
     * 构造函数。
     * @param grantee
     *          被授权者。
     *          目前只支持 {@link GroupGrantee#AllUsers}。
     * @param permission
     *          权限。
     */
    public Grant(Grantee grantee, Permission permission){
        if (grantee == null || permission == null){
            throw new NullPointerException();
        }
        
        this.grantee = grantee;
        this.permission = permission;
    }

    /**
     * 返回被授权者信息{@link Grantee}。
     * @return 被授权者信息{@link Grantee}。
     */
    public Grantee getGrantee() {
        return grantee;
    }

    /**
     * 返回权限{@link Permission}。
     * @return 权限{@link Permission}。
     */
    public Permission getPermission() {
        return permission;
    }
    
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Grant)){
            return false;
        }
        Grant g = (Grant)o;
        return this.getGrantee().getIdentifier().equals(g.getGrantee().getIdentifier())
                && this.getPermission().equals(g.getPermission());
    }
    
    @Override
    public int hashCode(){
        return (grantee.getIdentifier() + ":" + this.getPermission().toString()).hashCode();
    }

    @Override
    public String toString() {
        return "Grant [grantee=" + getGrantee() + ",permission=" + getPermission() + "]";
    }
}
