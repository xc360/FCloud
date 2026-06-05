package com.xc.file.model;

import com.xc.file.enums.UploadType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>上传文件model</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class UploadModel {
    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件hash值，文件的唯一值，md5加密计算
     */
    private String hashCode;

    /**
     * 文件续传开始的位置
     */
    private Long fileIndex;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 是否开启断点续传，0:开启，1:不开启
     */
    private String resumed = "0";
    /**
     * 文件夹路径
     */
    private String folderPath;
    /**
     * 上传类型
     */
    private UploadType uploadType;
    /**
     * 文件夹主键id
     */
    private String folderId;
    /**
     * 文件夹固定地址
     */
    private String fixedPath;
    /**
     * 状态：0：有效，1：无效
     */
    private String status;
    /**
     * 磁盘主键
     */
    private String diskId;
    /**
     * 文件数据
     */
    private MultipartFile file;
    /**
     * 组标识
     */
    private String groupCode;
}
