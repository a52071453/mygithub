/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */
package com.aliyun.oss.model;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 表示{@link Bucket}的http referer信息。
 * <p>
 * 该配置会指明是否允许空的http referer，以及能够访问{@link Bucket}的白名单，常用于防盗链。
 * </p>
 *
 */
public class BucketReferer {
	private boolean allowEmptyReferer;
	private List<String> refererList;

	public BucketReferer() {
		this.allowEmptyReferer = true;
		this.refererList = new ArrayList<String>();
	}
	
	public BucketReferer(boolean allowEmptyReferer, List<String> refererList) {
		this.allowEmptyReferer = allowEmptyReferer;
		this.refererList = refererList;
	}
	
	public boolean allowEmpty() {
		return this.allowEmptyReferer;
	}
	
	public List<String> getRefererList() {
		return refererList;
	}

	public String toXmlString() {
    	StringBuffer xml = new StringBuffer();
    	xml.append("<RefererConfiguration>");
    	xml.append("<AllowEmptyReferer>" + String.valueOf(allowEmptyReferer) + "</AllowEmptyReferer>");
    	xml.append("<RefererList>");
    	for (String e : refererList) {
    		xml.append("<Referer>" + e + "</Referer>");
    	}
    	xml.append("</RefererList>");
    	xml.append("</RefererConfiguration>");
    	return xml.toString();
    }

	@SuppressWarnings("unchecked")
	public static BucketReferer createFromXmlRootElement(Element root) {
        Namespace ns = root.getNamespace();
        boolean allowEmptyReferer = Boolean.valueOf(root.getChildText("AllowEmptyReferer", ns));
        ArrayList<String> refererList = new ArrayList<String>();
        Element tmpElement = root.getChild("RefererList", ns);
        List<Element> tmpList = tmpElement.getChildren("Referer", ns);
        for (Element e : tmpList) {
            refererList.add(e.getText());
        }
        return new BucketReferer(allowEmptyReferer, refererList);
	}
}
