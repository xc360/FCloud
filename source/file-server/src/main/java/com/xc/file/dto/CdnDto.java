package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>cdn返回参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class CdnDto {

    @ApiModelProperty(value = "cdn地址")
    private String cdnUrl;
}
