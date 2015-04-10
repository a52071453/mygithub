/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss;

/**
 * OSS定义的错误代码。
 */
public interface OSSErrorCode {

    /**
     * 拒绝访问。
     */
    static final String ACCESS_DENIED = "AccessDenied";

    /**
     * Bucket 已经存在 。
     */
    static final String BUCKES_ALREADY_EXISTS = "BucketAlreadyExists";

    /**
     * Bucket 不为空。
     */
    static final String BUCKETS_NOT_EMPTY = "BucketNotEmpty";

    /**
     * 文件组过大。
     */
    static final String FILE_GROUP_TOO_LARGE = "FileGroupTooLarge";

    /**
     * 文件Part过时。
     */
    static final String FILE_PART_STALE = "FilePartStale";

    /**
     * 参数格式错误。
     */
    static final String INVALID_ARGUMENT = "InvalidArgument";

    /**
     * Access ID不存在。
     */
    static final String INVALID_ACCESS_KEY_ID = "InvalidAccessKeyId";

    /**
     * 无效的 Bucket 名字。
     */
    static final String INVALID_BUCKET_NAME = "InvalidBucketName";

    /**
     * 无效的 Object 名字 。
     */
    static final String INVALID_OBJECT_NAME = "InvalidObjectName";

    /**
     * 无效的 Part。
     */
    static final String INVALID_PART = "InvalidPart";

    /**
     * 无效的 Part顺序。
     */
    static final String INVALID_PART_ORDER = "InvalidPartOrder";

    /**
     * OSS 内部发生错误。
     */
    static final String INTERNAL_ERROR = "InternalError";

    /**
     * 缺少内容长度。
     */
    static final String MISSING_CONTENT_LENGTH = "MissingContentLength";

    /**
     * Bucket 不存在。
     */
    static final String NO_SUCH_BUCKET = "NoSuchBucket";

    /**
     * 文件不存在。
     */
    static final String NO_SUCH_KEY = "NoSuchKey";

    /**
     * 无法处理的方法。
     */
    static final String NOT_IMPLEMENTED = "NotImplemented";

    /**
     * 预处理错误。
     */
    static final String PRECONDITION_FAILED = "PreconditionFailed";

    /**
     * 发起请求的时间和服务器时间超出15分钟。
     */
    static final String REQUEST_TIME_TOO_SKEWED = "RequestTimeTooSkewed";

    /**
     * 请求超时。
     */
    static final String REQUEST_TIMEOUT = "RequestTimeout";

    /**
     * 签名错误。
     */
    static final String SIGNATURE_DOES_NOT_MATCH = "SignatureDoesNotMatch";

    /**
     * 用户的 Bucket 数目超过限制 。
     */
    static final String TOO_MANY_BUCKETS = "TooManyBuckets";
    
    /**
     * 源Bucket未设置CORS
     */
    static final String NO_SUCH_CORS_CONFIGURATION="NoSuchCORSConfiguration";
    
    /**
     * 源Bucket未设置静态网站托管功能
     */
    static final String NO_SUCH_WEBSITE_CONFIGURATION="NoSuchWebsiteConfiguration";
    
    /**
     * 源Bucket未设置Lifecycle
     */
    static final String NO_SUCH_LIFECYCLE = "NoSuchLifecycle";
}
