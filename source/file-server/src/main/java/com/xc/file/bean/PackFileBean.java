package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>打包文件参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class PackFileBean {

    @ApiModelProperty(value = "共享文件id集合")
    public List<String> fileIds;

    @ApiModelProperty(value = "共享目录id集合")
    public List<String> folderIds;

    @ApiModelProperty(value = "目标文件夹主键")
    private String folderId;

    @ApiModelProperty(value = "包名称")
    public String name;

    @ApiModelProperty(value = "压缩格式")
    public String format;
}
