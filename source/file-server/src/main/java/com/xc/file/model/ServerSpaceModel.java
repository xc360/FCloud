package com.xc.file.model;

import lombok.Data;

/**
 * <p>目录信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ServerSpaceModel {
    /**
     * 文件目录
     */
    private String path;

    /**
     * 可用空间
     */
    private Long availableSpace;
}
