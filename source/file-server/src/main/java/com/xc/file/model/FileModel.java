package com.xc.file.model;

import lombok.Data;

import java.util.List;

/**
 * <p>文件配置信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class FileModel {
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 文件上传index
     */
    private List<IndexModel> index;
}
