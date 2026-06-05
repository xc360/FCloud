package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>hash信息dto</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class FileHashDto {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "标识，对应真实文件名称")
    private String code;

    @ApiModelProperty(value = "hash值")
    private String hashCode;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "存放的服务器地址")
    private String serverUrl;

    @ApiModelProperty(value = "组标识")
    private String groupCode;
}
