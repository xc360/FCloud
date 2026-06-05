package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * <p>允许访问链接实体</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_safety_chain")
public class SafetyChainEntity {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 链接名称
     */
    private String name;
    /**
     * 允许访问地址
     */
    private String url;
    /**
     * 文件夹路径
     */
    private String path;
    /**
     * 是否验证签名，0：是，1，不是
     */
    private String verifySign;
    /**
     * 允许访问的后缀
     */
    private String allowSuffix;
    /**
     * 磁盘主键
     */
    private String diskId;
    /**
     * 状态，对应字典表的effectStatus，0：有效，1：无效
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
     * 版本
     */
    @Version
    private Integer version;

}
