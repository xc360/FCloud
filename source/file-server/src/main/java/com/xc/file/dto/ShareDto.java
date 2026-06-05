package com.xc.file.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>共享文件返回参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ShareDto {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "共享名称")
    private String name;

    @ApiModelProperty(value = "文件提取码")
    private String code;

    @ApiModelProperty(value = "有效期")
    private Long validTime;

    @ApiModelProperty(value = "提取码")
    private String drawCode;

    @ApiModelProperty(value = "磁盘主键")
    private String diskId;

    @ApiModelProperty(value = "保存次数")
    private Integer preserveNum;

    @ApiModelProperty(value = "浏览次数")
    private Integer browseNum;

    @ApiModelProperty(value = "下载次数")
    private Integer downloadNum;


    @ApiModelProperty(value = "共享备注")
    private String remark;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
