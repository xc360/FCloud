package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>文件夹实体类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_folder")
public class FolderEntity implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 节点
     */
    private String node;
    /**
     * 父节点
     */
    private String parentNode;
    /**
     * 磁盘主键
     */
    private String diskId;
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
