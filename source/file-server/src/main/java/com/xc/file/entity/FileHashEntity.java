package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>hash信息实体类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_file_hash")
public class FileHashEntity {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 标识，对应真实文件名称
     */
    private String code;
    /**
     * hash值
     */
    private String hashCode;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 存放的服务器地址
     */
    private String serverUrl;
    /**
     * 组标识
     */
    private String groupCode;
}
