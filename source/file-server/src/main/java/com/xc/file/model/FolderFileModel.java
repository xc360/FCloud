package com.xc.file.model;

import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FolderEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>文件夹文件模型</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
public class FolderFileModel {

    /**
     * 跟文件集合
     */
    private List<FileEntity> rootFiles;
    /**
     * 跟文件夹集合
     */
    private List<FolderEntity> rootFolders;

}
