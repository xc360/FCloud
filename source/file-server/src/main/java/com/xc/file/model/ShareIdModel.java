package com.xc.file.model;

import com.xc.file.entity.ShareEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>共享id信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ShareIdModel {
    /**
     * 共享实体
     */
    private ShareEntity shareEntity;
    /**
     * 文件夹id集合
     */
    private List<String> folderIds;
    /**
     * 文件id集合
     */
    private List<String> fileIds;
    /**
     * 根文件夹id集合
     */
    private List<String> rootFolderIds;
    /**
     * 根目录文件id集合
     */
    private List<String> rootFileIds;
}
