package com.xc.file.model;

import com.xc.core.bean.PagingBean;
import com.xc.core.bean.QueryBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 查询文件和文件夹的model
 * </p>
 *
 * @author xc
 * @since 2024-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryFolderFileModel extends QueryBean {
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 文件夹主键
     */
    private String folderId;
    /**
     * 磁盘主键
     */
    private String diskId;
    /**
     * 父节点
     */
    private String parentNode;
    /**
     * 共享主键
     */
    private String shareId;
    /**
     * 是Root，0：是，1：不是
     */
    private String isRoot;
}
