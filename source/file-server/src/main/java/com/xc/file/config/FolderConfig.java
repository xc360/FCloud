package com.xc.file.config;

import lombok.Data;

/**
 * <p>目录信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class FolderConfig {
    /**
     * 储存文件地址,必须配置
     */
    private String path;

    /**
     * 硬盘预留空间
     */
    private long reserveSpace;
}
