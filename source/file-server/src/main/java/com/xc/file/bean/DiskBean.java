package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>磁盘bean</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class DiskBean {

    @ApiModelProperty(value = "磁盘名称")
    private String name;

    @ApiModelProperty(value = "磁盘组主键")
    private String groupId;

    @ApiModelProperty(value = "签名有效时间")
    private Long signValidTime;
}
