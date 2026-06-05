package com.xc.file.bean;

import com.xc.core.bean.QueryBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>查询共享文件夹及文件参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class QueryShareFolderBean {

    @ApiModelProperty(value = "共享文件id集合")
    private String visitCode;

    @ApiModelProperty(value = "文件夹主键")
    private String folderId;

    @ApiModelProperty(value = "文件或文件夹名称")
    private String name;

    @ApiModelProperty(value = "打开名称查询")
    private Boolean openQueryAll = false;
}
