package com.xc.file.model;

import lombok.Data;

/**
 * <p>文件index信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class IndexModel {

    /**
     * 开始的index
     */
    private Long startIndex;

    /**
     * 结束的index
     */
    private Long stopIndex;

    /**
     * 状态：true:上传完成，false:未上传
     */
    private Boolean state = false;
}
