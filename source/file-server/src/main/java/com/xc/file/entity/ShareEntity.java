package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * <p>共享文件实体</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_share")
public class ShareEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 共享名称
     */
    private String name;
    /**
     * 文件提取码
     */
    private String code;
    /**
     * 有效期,0:永久有效
     */
    private Long validTime;
    /**
     * 提取码
     */
    private String drawCode;

    /**
     * 磁盘主键
     */
    private String diskId;

    /**
     * 保存次数
     */
    private Integer preserveNum;
    /**
     * 浏览次数
     */
    private Integer browseNum;
    /**
     * 下载次数
     */
    private Integer downloadNum;
    /**
     * 共享备注
     */
    private String remark;
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
