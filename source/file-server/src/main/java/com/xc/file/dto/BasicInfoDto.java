package com.xc.file.dto;

import lombok.Data;

/**
 * <p>路径dto</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class BasicInfoDto {
    /**
     * 文件夹path
     */
    private String folderId;
    /**
     * 文件path
     */
    private String fileId;
    /**
     * 磁盘编号
     */
    private String diskId;
}
