package com.xc.file.bean;

import com.xc.api.file.bean.UploadBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>token上传文件</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenUploadBean extends UploadBean {

    @ApiModelProperty(value = "文件夹主键id")
    private String folderId;
}
