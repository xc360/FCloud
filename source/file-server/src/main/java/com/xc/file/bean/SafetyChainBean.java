package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>安全链接参数类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
public class SafetyChainBean {

    @ApiModelProperty(value = "允许访问地址")
    private String url;

    @ApiModelProperty(value = "链接名称")
    private String name;

    @ApiModelProperty(value = "文件夹路径")
    private String path;

    @ApiModelProperty(value = "是否验证签名，0：是，1，不是")
    private String verifySign;

    @ApiModelProperty(value = "允许访问的后缀")
    private String allowSuffix;

    @ApiModelProperty(value = "状态，对应字典表的effectStatus，0：有效，1：无效")
    private String status;
}
