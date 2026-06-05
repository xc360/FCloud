package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>文件信息参数类</p>
 *
 * @version v1.0
 */
@Data
public class FileBean {

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "文件夹路劲")
    private String folderId;

    @ApiModelProperty(value = "哈希主键")
    private String hashId;

    @ApiModelProperty(value = "状态：0：有效，1：无效")
    private String status;
}
