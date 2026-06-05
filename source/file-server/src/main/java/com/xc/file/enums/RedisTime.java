package com.xc.file.enums;

/**
 * <p>redis有效期</p>
 *
 * @author xc
 * @version v1.0.0
 */
public enum RedisTime {
    /**
     * 共享下载文件缓存时间，24小时
     */
    SHARE_DOWNLOAD_CACHE(86400000),
    /**
     * 用户下载文件缓存时间，24小时
     */
    USER_DOWNLOAD_CACHE(86400000),
    /**
     * 公开下载文件缓存时间，24小时
     */
    OPEN_DOWNLOAD_CACHE(86400000);

    /**
     * 时间
     */
    private final long time;

    RedisTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
