package com.xc.file.enums;

/**
 * <p>上传类型</p>
 *
 * @author xc
 * @version 1.0.0
 */
public enum UploadType {
    SIGN("sign"),
    TOKEN("token");

    private String type;

    UploadType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
