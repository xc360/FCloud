package com.xc.file.model;

import lombok.Data;

/**
 * <p>hash信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class HashModel {

    /**
     * 文件标识
     */
    private String code;
    /**
     * 文件大小
     */
    private Long size;

    public HashModel() {
    }

    public HashModel(String code, Long size) {
        this.code = code;
        this.size = size;
    }
}
