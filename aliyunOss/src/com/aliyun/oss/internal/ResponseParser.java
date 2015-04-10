/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.utils.CodingUtils.isNullOrEmpty;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GroupGrantee;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.MultipartUpload;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.Owner;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PartSummary;
import com.aliyun.oss.model.Permission;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

public final class ResponseParser {

    /**
     * root
     * */
    private static Element getRootElement(String requestId, InputStream in)throws ClientException{

        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(in);
            return doc.getRootElement();
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId,
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("ParseError"), 
                    e);
        }

    }

    /**
     * 解析列出bucket下的所有object
     * **/
    @SuppressWarnings("unchecked")
    public static ObjectListing parseListObjects(String requestId, InputStream in)throws ClientException{

        ObjectListing objectListing = new ObjectListing();

        try {
            Element root = getRootElement(requestId, in);

            Namespace ns = root.getNamespace();
            objectListing.setBucketName(root.getChildText("Name", ns));
            objectListing.setPrefix(root.getChildText("Prefix", ns));
            objectListing.setMarker(root.getChildText("Marker", ns));
            objectListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxKeys", ns)));
            objectListing.setDelimiter(root.getChildText("Delimiter", ns));
            objectListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated", ns)));
            objectListing.setNextMarker(root.getChildText("NextMarker", ns));

            List<OSSObjectSummary> contents = objectListing
                    .getObjectSummaries();
            List<Element> tempList = root.getChildren("Contents", ns);
            for (Element e : tempList) {

                OSSObjectSummary ossObjectSummary = new OSSObjectSummary();

                ossObjectSummary.setKey(e.getChildText("Key", ns));
                ossObjectSummary.setETag(OSSUtils.trimQuotes(e.getChildText("ETag", ns)));
                ossObjectSummary.setLastModified(DateUtil.parseIso8601Date(e.getChildText("LastModified", ns)));
                ossObjectSummary.setSize(Long.valueOf(e.getChildText("Size", ns)));
                ossObjectSummary.setStorageClass(e.getChildText("StorageClass", ns));
                ossObjectSummary.setBucketName(objectListing.getBucketName());
                
                String id = e.getChild("Owner", ns).getChildText("ID", ns);
                String displayName = e.getChild("Owner", ns).getChildText("DisplayName", ns);
                ossObjectSummary.setOwner(new Owner(id, displayName));

                contents.add(ossObjectSummary);

            }

            tempList = root.getChildren("CommonPrefixes", ns);
            for (Element e : tempList) {
                objectListing.getCommonPrefixes().add(e.getChildText("Prefix", ns));
            }
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return objectListing;
    }

    /**
     * 解析取得bucket下的acl权限
     * **/
    public static AccessControlList parseGetBucketAcl(String requestId, InputStream in)throws ClientException{

        try {
            Element root = getRootElement(requestId, in);

            AccessControlList aclList = new AccessControlList();

            String id = root.getChild("Owner").getChildText("ID");
            String displayName = root.getChild("Owner").getChildText("DisplayName");
            Owner owner = new Owner(id, displayName);
            aclList.setOwner(owner);

            String acl = root.getChild("AccessControlList").getChildText("Grant");
            CannedAccessControlList cAcl = CannedAccessControlList.parse(acl);

            // Do not grantPermission if acl is "private".
            if (cAcl == CannedAccessControlList.PublicRead) {
                aclList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
            } else if (cAcl == CannedAccessControlList.PublicReadWrite) {
                aclList.grantPermission(GroupGrantee.AllUsers, Permission.FullControl);
            }
            return aclList;

        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId,
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("ParseError"), 
                    e);
        }
    }

    /**
     * 解析取得bucket下的http referer描述
     * **/
    public static BucketReferer parseGetBucketReferer(String requestId, InputStream in)throws ClientException{
        try {
            Element root = getRootElement(requestId, in);
            return BucketReferer.createFromXmlRootElement(root);
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId,
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("ParseError"), 
                    e);
        }
    }
    
    /**
     * 解析取得接口Upload Part Copy返回的结果
     * **/
    public static String parseUploadPartCopy(String requestId, InputStream in)throws ClientException{

        try {
            Element root = getRootElement(requestId, in);
            return  root.getChildText("ETag");            
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId,
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("ParseError"), e);
        }
    }

    /**
     * 解析列出指定用户下的所有bucket
     * **/
    @SuppressWarnings("unchecked")
    public static BucketList parseListBucket(String requestId, InputStream in)throws ClientException{

        BucketList bucketList = new BucketList();

        try {
            Element root = getRootElement(requestId, in);

            Namespace ns = root.getNamespace();
            bucketList.setPrefix(root.getChildText("Prefix", ns));
            bucketList.setMarker(root.getChildText("Marker", ns));
            String tmp = root.getChildText("MaxKeys", ns); 
            bucketList.setMaxKeys(tmp == null ? null : Integer.valueOf(tmp));
            tmp = root.getChildText("IsTruncated", ns);
            bucketList.setTruncated(tmp == null ? false : Boolean.valueOf(tmp));
            bucketList.setNextMarker(root.getChildText("NextMarker", ns));

            Element ownerEle = root.getChild("Owner", ns);
            String id = ownerEle.getChildText("ID", ns);
            String displayName = ownerEle.getChildText("DisplayName", ns);
            Owner owner = new Owner(id, displayName);

            List<Bucket> buckets = new ArrayList<Bucket>();
            Element bucketsEle = root.getChild("Buckets", ns);
            for (Iterator<Element> it = bucketsEle.getChildren("Bucket", ns).iterator(); it.hasNext();) {
                Element ele = it.next();

                Bucket bucket = new Bucket();
                bucket.setOwner(owner);
                bucket.setName(ele.getChildText("Name", ns));
                bucket.setLocation(ele.getChildText("Location", ns));
                bucket.setCreationDate(DateUtil.parseIso8601Date(ele.getChildText("CreationDate", ns)));

                buckets.add(bucket);
            }
            bucketList.setBucketList(buckets);
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return bucketList;
    }
    
    public static String parseGetBucketLocation(String requestId, InputStream in) throws ClientException{
                
        try {
            Element root = getRootElement(requestId, in);
            return root.getText();
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

    }

    /**
     * 从返回的header中解析object的metadata信息
     * **/
    public static ObjectMetadata getObjectMetadata(String requestId, Map<String, String> headers)throws ClientException{

        ObjectMetadata objectMetadata = new ObjectMetadata();

        try {
            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext();) {

                String key = it.next();

                if (key.indexOf(OSSHeaders.OSS_USER_METADATA_PREFIX) >= 0) {
                    key = key.substring(OSSHeaders.OSS_USER_METADATA_PREFIX.length());
                    objectMetadata.addUserMetadata(key, headers.get(OSSHeaders.OSS_USER_METADATA_PREFIX + key));
                } else if (key.equals(OSSHeaders.LAST_MODIFIED) || key.equals(OSSHeaders.DATE) || key.equals(OSSHeaders.EXPIRES)) {
                    try {
                        objectMetadata.setHeader(key, DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException e1) {
                        throw new ClientException(e1);
                    }
                } else if (key.equals(OSSHeaders.CONTENT_LENGTH)) {
                    // for length
                    Long value = Long.valueOf(headers.get(key));
                    objectMetadata.setHeader(key, value);
                } else if (key.equals(OSSHeaders.ETAG)) {
                    objectMetadata.setHeader(key, OSSUtils.trimQuotes(headers.get(key)));
                }else {
                    // 其它meta用string处理
                    objectMetadata.setHeader(key, headers.get(key) );
                }
            }

        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return objectMetadata;
    }

    /**
     * 解析创建multipart的返回结果
     * **/
    public static InitiateMultipartUploadResult parseInitiateMultipartUpload(String requestId,
            InputStream in) throws ClientException {

        Element root = getRootElement(requestId, in);

        InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();
        try {

            String bucketName = root.getChildText("Bucket");
            String key = root.getChildText("Key");
            String uploadId = root.getChildText("UploadId");
            result.setBucketName(bucketName);
            result.setKey(key);
            result.setUploadId(uploadId);
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return result;
    }

    /**
     * 解析列出bucket下的所有multipart
     * **/
    @SuppressWarnings("unchecked")
    public static MultipartUploadListing parseListMultipartUploads(String requestId, InputStream in) throws ClientException {

        Element root = getRootElement(requestId, in);

        MultipartUploadListing result = new MultipartUploadListing();

        try {
            result.setBucketName(root.getChildText("Bucket"));
            List<Element> tempList = root.getChildren("CommonPrefixes");
            for (Element e : tempList) {
                result.getCommonPrefixes().add(e.getChildText("Prefix"));
            }

            result.setDelimiter(root.getChildText("Delimiter"));
            result.setKeyMarker(root.getChildText("KeyMarker"));
            result.setMaxUploads(Integer.valueOf(root.getChildText("MaxUploads")));

            List<MultipartUpload> multipartUploads = new ArrayList<MultipartUpload>();
            result.setMultipartUploads(multipartUploads);
            for (Iterator<Element> it = root.getChildren("Upload").iterator(); it.hasNext();) {
                Element ele = it.next();
                MultipartUpload part = new MultipartUpload();

                if (ele.getChild("Initiated") == null){
                    //oss upload part 有可能不全的情况
                    continue;
                }

                multipartUploads.add(part);

                part.setInitiated(DateUtil.parseIso8601Date(ele.getChildText("Initiated")));
                part.setKey(ele.getChildText("Key"));
                part.setStorageClass(ele.getChildText("StorageClass"));
                part.setUploadId(ele.getChildText("UploadId"));
            }

            result.setNextKeyMarker(root.getChildText("NextKeyMarker"));
            result.setNextUploadIdMarker(root.getChildText("NextUploadIdMarker"));
            result.setPrefix(root.getChildText("Prefix"));
            result.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));
            result.setUploadIdMarker(root.getChildText("UploadIdMarker"));

        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return result;

    }

    /**
     * 解析列出指定multipart下的所有part
     * **/
    @SuppressWarnings("unchecked")
    public static PartListing parseListParts(String requestId, InputStream in) throws ClientException {

        Element root = getRootElement(requestId, in);
        PartListing result = new PartListing();

        try {
            result.setBucketName(root.getChildText("Bucket"));

            Owner initiator = new Owner();
            if (root.getChild("Initiator") != null){
                initiator.setId(root.getChild("Initiator").getChildText("ID"));
                initiator.setDisplayName(root.getChild("Initiator").getChildText("DisplayName"));
            }
            result.setInitiator(initiator);
            result.setKey(root.getChildText("Key"));
            result.setMaxParts(Integer.valueOf(root.getChildText("MaxParts")));
            String nextPartNumberMarkerText = root.getChildText("NextPartNumberMarker");
            if (!isNullOrEmpty(nextPartNumberMarkerText)) {
                result.setNextPartNumberMarker(Integer.valueOf(nextPartNumberMarkerText));
            }

            Owner owner = new Owner();
            if (root.getChild("Owner") != null){
                owner.setId(root.getChild("Owner").getChildText("ID"));
                owner.setDisplayName(root.getChild("Owner").getChildText("DisplayName"));
            }
            result.setOwner(owner);

            String partNumberMarkerText = root.getChildText("PartNumberMarker");
            if (!isNullOrEmpty(partNumberMarkerText)){
                result.setPartNumberMarker(Integer.valueOf(partNumberMarkerText));
            }

            List<PartSummary> parts = new ArrayList<PartSummary>();
            result.setParts(parts);

            for (Iterator<Element> it = root.getChildren("Part").iterator(); it.hasNext();) {

                Element ele = it.next();
                PartSummary part = new PartSummary();
                parts.add(part);

                part.setETag(OSSUtils.trimQuotes(ele.getChildText("ETag")));
                part.setLastModified(DateUtil.parseIso8601Date(ele.getChildText("LastModified")));
                part.setPartNumber(Integer.valueOf(ele.getChildText("PartNumber")));
                part.setSize(Integer.valueOf(ele.getChildText("Size")));
            }

            result.setStorageClass(root.getChildText("StorageClass"));
            result.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));
            result.setUploadId(root.getChildText("UploadId"));

        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }

        return result;
    }

    /**
     * 解析commit multipart的结果
     * **/
    public static CompleteMultipartUploadResult parseCompleteMultipartUpload(String requestId, InputStream in) throws ClientException {

        Element root = getRootElement(requestId, in);

        CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
        try {
            result.setBucketName(root.getChildText("Bucket"));
            result.setETag(OSSUtils.trimQuotes(root.getChildText("ETag")));
            result.setKey(root.getChildText("Key"));
            result.setLocation(root.getChildText("Location"));
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return result;
    }

    /**
     * 
     **/
    public static BucketLoggingResult parseBucketLogging(String requestId, InputStream in) throws ClientException {
        Element root = getRootElement(requestId, in);

        BucketLoggingResult result = new BucketLoggingResult();
        try {
        	if(root.getChild("LoggingEnabled") != null){
        		result.setTargetBucket(root.getChild("LoggingEnabled").getChildText("TargetBucket"));
        	}
        	if(root.getChild("LoggingEnabled") != null){
        		result.setTargetPrefix(root.getChild("LoggingEnabled").getChildText("TargetPrefix"));
        	}
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return result;
    }

    public static BucketWebsiteResult parseBucketWebsite(String requestId, InputStream in) throws ClientException {

        Element root = getRootElement(requestId, in);

        BucketWebsiteResult result = new BucketWebsiteResult();
        try {
        	if(root.getChild("IndexDocument") != null){
        		result.setIndexDocument(root.getChild("IndexDocument").getChildText("Suffix"));
        	}
        	if(root.getChild("ErrorDocument") != null){
        		result.setErrorDocument(root.getChild("ErrorDocument").getChildText("Key"));
        	}
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return result;
    }
    
    public static CopyObjectResult parseCopyObjectResult(String requestId, InputStream content) throws ClientException {
        Element root = getRootElement(requestId, content);
        CopyObjectResult result = new CopyObjectResult();
        try {
            result.setLastModified(DateUtil.parseIso8601Date(root.getChildText("LastModified")));
            result.setEtag(OSSUtils.trimQuotes(root.getChildText("ETag")));
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return result;
    }
    
    /**
     * 解析列出用户指定bucket下的所有CORSRule
     * 		{@link CORSRule}
     **/
    @SuppressWarnings("unchecked")
    public static List<CORSRule> parseListBucketCORS(String requestId, InputStream in)throws ClientException{
    	
        List<CORSRule> corsRules = new ArrayList<CORSRule>();
        try {
            Element root = getRootElement(requestId, in);
        	List<Element> corsRuleElements = root.getChildren("CORSRule");
            
        	for (Element corsRulelement : corsRuleElements) {
        		CORSRule rule = new CORSRule();
        		List<Element> allowedOriginElements =corsRulelement.getChildren("AllowedOrigin");
        		for(Element allowedOriginElement : allowedOriginElements){
        			rule.getAllowedOrigins().add(allowedOriginElement.getValue());
        		}
        		
        		List<Element> allowedMethodElements =corsRulelement.getChildren("AllowedMethod");
        		for(Element allowedMethodElement : allowedMethodElements){
        			rule.getAllowedMethods().add(allowedMethodElement.getValue());
        		}
        		
        		List<Element> allowedHeaderElements =corsRulelement.getChildren("AllowedHeader");
        		for(Element allowedHeaderElement : allowedHeaderElements){
        			rule.getAllowedHeaders().add(allowedHeaderElement.getValue());
        		}
        		
        		List<Element> exposeHeaderElements =corsRulelement.getChildren("ExposeHeader");
        		for(Element exposeHeaderElement : exposeHeaderElements){
        			rule.getExposeHeaders().add(exposeHeaderElement.getValue());
        		}
        		Element maxAgeSecondsElement = corsRulelement.getChild("MaxAgeSeconds");
        		if(maxAgeSecondsElement!=null){
        			rule.setMaxAgeSeconds(parseInteger(maxAgeSecondsElement.getValue()));
        		}
        		
        		corsRules.add(rule);
            }
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return corsRules;
    }
   
    @SuppressWarnings("unchecked")
    public static List<LifecycleRule> parseGetBucketLifecycle(String requestId, InputStream in)throws ClientException{
        List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();
       
        try {
            Element root = getRootElement(requestId, in);
			List<Element> ruleElements = root.getChildren("Rule");
            
        	for (Element ruleElem : ruleElements) {
        		LifecycleRule rule = new LifecycleRule();
        		
        		if (ruleElem.getChild("ID") != null) {
        			rule.setId(ruleElem.getChildText("ID"));
        		}
        		
        		if (ruleElem.getChild("Prefix") != null) {
        			rule.setPrefix(ruleElem.getChildText("Prefix"));
        		}
        		
        		if (ruleElem.getChild("Status") != null) {
        			rule.setStatus(RuleStatus.valueOf(ruleElem.getChildText("Status")));
        		}
        		
        		if (ruleElem.getChild("Expiration") != null) {
        			if (ruleElem.getChild("Expiration").getChild("Date") != null) {
        				Date expirationDate = DateUtil.parseIso8601Date(ruleElem.getChild("Expiration").getChildText("Date"));
        				rule.setExpirationTime(expirationDate);
        			} else {
        				rule.setExpriationDays(Integer.parseInt(ruleElem.getChild("Expiration").getChildText("Days")));
        			}
        		}
        		
        		lifecycleRules.add(rule);
            }
        } catch (Exception e) {
            throw OSSExceptionFactory.createInvalidResponseException(requestId, "respones builder error", e);
        }
        return lifecycleRules;
    }
    
    private static Integer parseInteger(Object obj){
    	try{
    		return Integer.parseInt(obj.toString());
    	}catch(Exception e){
    		return null;
    	}
    }
}
