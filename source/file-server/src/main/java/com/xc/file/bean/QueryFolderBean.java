package com.xc.file.bean;

import com.xc.core.bean.QueryBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <p>文件夹参数类</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryFolderBean extends QueryBean {

    @ApiModelProperty(value = "文件夹名称")
    private String name;

    @ApiModelProperty(value = "文件夹主键")
    private String folderId;

    @ApiModelProperty(value = "磁盘主键")
    private String diskId;

    @ApiModelProperty(value = "打开查询全部")
    private Boolean openQueryAll = false;
}
