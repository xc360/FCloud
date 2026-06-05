package com.xc.file.bean;

import com.xc.api.basic.bean.UserSignBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 下载参数类
 * </p>
 *
 * @author xc
 * @since 2023-11-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DownloadFileBean extends UserSignBean {

    @ApiModelProperty(value = "open是否直接打开,0:可以直接打开，1:不能直接打开，默认下载")
    private Integer open;

    @ApiModelProperty(value = "视频文件后缀，下载m3u8格式视频时使用")
    private String suffix;

    @ApiModelProperty("压缩图片高，下载图片时使用")
    private Integer w;

    @ApiModelProperty("压缩图片宽，下载图片时使用")
    private Integer h;

    @ApiModelProperty("压缩比例,小数，下载图片时使用")
    private Double s;
}
