package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>共享文件关联实体</p>
 *
 * @author xc
 * @version v1.0
 */
@Data
@TableName("xc_share_folder_file")
public class ShareFolderFileEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 共享文件关联表
     */
    private String shareId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件夹id
     */
    private String folderId;

    /**
     * 是否是根，0：是，1：不是
     */
    private Integer isRoot;

    public ShareFolderFileEntity() {
    }

    public ShareFolderFileEntity(String shareId) {
        this.shareId = shareId;
    }
}
