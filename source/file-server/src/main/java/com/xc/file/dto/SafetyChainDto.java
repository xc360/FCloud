package com.xc.file.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>共享文件返回参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class SafetyChainDto {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "允许访问地址")
    private String url;

    @ApiModelProperty(value = "链接名称")
    private String name;

    @ApiModelProperty(value = "文件夹路径")
    private String path;

    @ApiModelProperty(value = "是否验证签名，0：是，1，不是")
    private String verifySign;

    @ApiModelProperty(value = "允许访问的后缀")
    private String allowSuffix;

    @ApiModelProperty(value = "磁盘主键")
    private String diskId;

    @ApiModelProperty(value = "状态，对应字典表的effectStatus，0：有效，1：无效")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
