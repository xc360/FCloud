package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>共享文件返回参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class UrlDto {

    @ApiModelProperty(value = "上传地址")
    private String url;

    public UrlDto() {
    }

    public UrlDto(String url) {
        this.url = url;
    }
}
