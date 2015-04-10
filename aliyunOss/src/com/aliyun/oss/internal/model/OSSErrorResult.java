/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Error")
public class OSSErrorResult {
    @XmlElement(name="Code")
    public String Code;
    
    @XmlElement(name="Message")
    public String Message;
    
    @XmlElement(name="RequestId")
    public String RequestId;
    
    @XmlElement(name="HostId")
    public String HostId;
    
    @XmlElement(name="ResourceType")
    public String ResourceType;
    
    @XmlElement(name="Method")
    public String Method;
    
    @XmlElement(name="Header")
    public String Header;
    
}