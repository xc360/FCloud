package com.xc.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>文件夹大小Dto</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class FolderSizeDto {

    @ApiModelProperty(value = "文件夹大小")
    private String size;

    public FolderSizeDto() {
    }

    public FolderSizeDto(String size) {
        this.size = size;
    }
}
