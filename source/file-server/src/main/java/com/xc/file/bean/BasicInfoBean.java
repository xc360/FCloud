package com.xc.file.bean;

import lombok.Data;

/**
 * <p>路径bean</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class BasicInfoBean {
    /**
     * 文件夹path
     */
    private String folderPath;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 磁盘编号
     */
    private String diskNo;
}
