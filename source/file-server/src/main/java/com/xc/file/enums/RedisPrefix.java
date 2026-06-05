package com.xc.file.enums;

/**
 * <p>redis前缀</p>
 *
 * @author xc
 * @version v1.0.0
 */
public enum RedisPrefix {
    SHARE_DOWNLOAD("shareDownload:"), //共享下载文件
    USER_DOWNLOAD("userDownload:"), // 用户下载文件
    OPEN_DOWNLOAD("openDownload:"), // 开放下载文件
    SERVER("server:"); // 文件服务key

    /**
     * redis前缀
     */
    private String key;

    RedisPrefix(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
