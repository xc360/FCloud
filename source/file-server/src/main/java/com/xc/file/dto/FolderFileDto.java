package com.xc.file.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xc.api.file.dto.FileDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>文件文件夹返回参数</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
public class FolderFileDto {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "文件夹名称")
    private String name;

    @ApiModelProperty(value = "类型，0：是，1：不是")
    private String isFile;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "文件夹主键")
    private String folderId;

    @ApiModelProperty(value = "父节点")
    private String parentNode;

    @ApiModelProperty(value = "节点")
    private String node;

    @ApiModelProperty(value = "磁盘主键")
    private String diskId;

    @ApiModelProperty(value = "哈希主键")
    private String hashId;

    @ApiModelProperty(value = "状态：0：有效，1：无效")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "下载地址")
    private String url;
}
