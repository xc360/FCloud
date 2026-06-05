package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>修改目录参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class UpdateFolderBean {

    @ApiModelProperty(value = "文件名称")
    private String name;
}
