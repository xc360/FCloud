package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>共享code信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ShareCodeDto {

    @ApiModelProperty(value = "共享ID")
    private String shareId;

    @ApiModelProperty(value = "磁盘主键")
    private String diskId;

    @ApiModelProperty(value = "共享访问code")
    private String visitCode;

    @ApiModelProperty(value = "需要提取码")
    private Boolean needDrawCode;
}
