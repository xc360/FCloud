package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * <p>文件夹参数类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
public class FolderBean {

    @ApiModelProperty(value = "文件夹名称")
    private String name;
}
