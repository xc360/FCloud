package com.xc.file.model;

import com.xc.api.file.dto.FileDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>下载文件参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class DownloadModel {
    /**
     * 文件集合
     */
    private List<FileDto> files;
    /**
     * 下载id
     */
    private String id;
    /**
     * 固定链接
     */
    private String fixed;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 哈希主键
     */
    private String hashId;
    /**
     * 文件标识
     */
    private String code;
    /**
     * 存放的服务器地址
     */
    private String serverUrl;
    /**
     * 下载范围
     */
    private String range;
    /**
     * open是否直接打开,0:可以直接打开，1:不能直接打开
     */
    private int open;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 磁盘主键
     */
    private String diskId;
    /**
     * 可用流量
     */
    private Long freeFlow;
    /**
     * 共享id
     */
    private String shareId;

    /**
     * 是否计算流量
     */
    private Boolean isCompute = false;
}
