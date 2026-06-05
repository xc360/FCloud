package com.xc.file.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>文件夹及文件参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class FolderFileBean {

    @ApiModelProperty(value = "共享文件id集合")
    public List<String> fileIds;

    @ApiModelProperty(value = "共享目录id集合")
    public List<String> folderIds;

    @ApiModelProperty(value = "目标文件夹主键")
    private String folderId;
}
