package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>共享参数类</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ShareBean {

    @ApiModelProperty(value = "共享名称")
    private String name;

    @ApiModelProperty(value = "有效时间")
    private Long validTime;

    @ApiModelProperty(value = "需要code,0:需要，1：不需要")
    private int needCode;

    @ApiModelProperty(value = "共享文件id集合")
    public List<String> fileIds;

    @ApiModelProperty(value = "共享目录id集合")
    public List<String> folderIds;

    @ApiModelProperty(value = "共享备注")
    private String remark;
}
