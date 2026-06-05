package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * <p>磁盘实体</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
@TableName("xc_disk")
public class DiskEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 磁盘名称
     */
    private String name;
    /**
     * 磁盘编号
     */
    private String diskNo;
    /**
     * 应用程序秘钥
     */
    private String diskSecret;
    /**
     * 用户主键
     */
    private String userId;
    /**
     * 磁盘组主键
     */
    private String groupId;
    /**
     * 网盘空间
     */
    private Long cloudSpace;
    /**
     * 可用流量
     */
    private Long freeFlow;
    /**
     * 共享code
     */
    private String shareCode;
    /**
     * 签名有效时间
     */
    private Long signValidTime;
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
     * 版本
     */
    @Version
    private Integer version;

}
