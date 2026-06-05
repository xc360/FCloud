package com.xc.file.model;

import com.xc.file.entity.FolderEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>交叉点信息</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
public class CrossPointModel {

    /**
     * 原文件夹信息集合
     */
    private List<FolderEntity> folders;

    /**
     * 新文件夹信息集合
     */
    private List<FolderEntity> newFolders;

    public CrossPointModel() {
    }

    public CrossPointModel(List<FolderEntity> folders, List<FolderEntity> newFolders) {
        this.folders = folders;
        this.newFolders = newFolders;
    }
}
