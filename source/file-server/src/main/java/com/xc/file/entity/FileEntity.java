package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;


/**
 * <p>文件实体类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_file")
public class FileEntity {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 文件名称
     */
    private String name;
    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件夹主键
     */
    private String folderId;
    /**
     * 磁盘主键
     */
    private String diskId;
    /**
     * 哈希主键
     */
    private String hashId;
    /**
     * 状态：0：有效，1：无效
     */
    private String status;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 乐观锁,默认值0
     */
    @Version
    private Integer version;
}
