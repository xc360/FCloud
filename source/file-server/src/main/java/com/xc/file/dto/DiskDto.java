package com.xc.file.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>磁盘信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class DiskDto {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "磁盘名称")
    private String name;

    @ApiModelProperty(value = "磁盘编号")
    private String diskNo;

    @ApiModelProperty(value = "用户主键")
    private String userId;

    @ApiModelProperty(value = "磁盘组主键")
    private String groupId;

    @ApiModelProperty(value = "网盘空间")
    private Long cloudSpace;

    @ApiModelProperty(value = "可用流量")
    private Long freeFlow;

    @ApiModelProperty(value = "已使用空间")
    private Long useSpace;

    @ApiModelProperty(value = "共享code")
    private String shareCode;

    @ApiModelProperty(value = "签名有效时间")
    private Long signValidTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
