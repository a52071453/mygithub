/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.parser;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aliyun.oss.common.utils.DateUtil;

public class JaxbDateSerializer extends XmlAdapter<String, Date> {

    @Override
    public String marshal(Date date) throws Exception {
        return DateUtil.formatRfc822Date(date);
    }

    @Override
    public Date unmarshal(String date) throws Exception {
        return DateUtil.parseRfc822Date(date);
    }
}