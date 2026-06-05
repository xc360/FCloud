package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>修改文件参数类</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class UpdateFileBean {

    @ApiModelProperty(value = "文件名称")
    private String name;
}
