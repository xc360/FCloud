package com.xc.file.enums;

/**
 * <p>图片处理类型</p>
 *
 * @author xc
 * @version v1.0.0
 */
public enum ImageHandleType {
    CROP("crop"), // 裁剪
    COMPRESS("compress"); // 压缩

    private String type;

    ImageHandleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
