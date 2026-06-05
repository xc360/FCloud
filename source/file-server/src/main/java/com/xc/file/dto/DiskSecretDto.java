package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 磁盘秘钥
 * </p>
 *
 * @author xc
 * @since 2023-11-09
 */
@Data
public class DiskSecretDto {

    @ApiModelProperty(value = "磁盘秘钥")
    private String diskSecret;

}
