package com.xc.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.api.file.dto.FileDto;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.file.bean.*;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.function.FolderHandle;
import com.xc.file.model.FolderFileModel;
import com.xc.file.model.ShareIdModel;

import java.util.List;


/**
 * <p>文件夹服务类接口</p>
 *
 * @author xc
 * @version v1.0
 */
public interface FolderService extends IService<FolderEntity> {
    /**
     * <p>创建父级文件夹</p>
     *
     * @param parentNode 父节点
     * @param diskId     磁盘主键
     * @param path       文件夹路径
     * @return 最后一个子集文件夹的信息
     */
    public FolderEntity createParentFolders(String parentNode, String diskId, String path);

    /**
     * <p>创建文件夹</p>
     *
     * @param diskId     磁盘主键
     * @param folderBean 目录信息
     * @return 添加成功的数据
     */
    public FolderEntity createFolder(String diskId, String folderId, FolderBean folderBean);

    /**
     * <p>修改文件夹</p>
     *
     * @param diskId           磁盘主键
     * @param folderId         文件夹id
     * @param updateFolderBean 文件夹修改参数
     * @return 修改后的文件文件夹信息
     */
    public FolderDto updateFolder(String diskId, String folderId, UpdateFolderBean updateFolderBean);

    /**
     * <p>删除文件夹及以下文件</p>
     *
     * @param diskId   磁盘主键
     * @param folderId 文件夹id
     */
    public void deleteFolder(String diskId, String folderId);

    /**
     * <p>批量复制文件夹及文件<p/>
     *
     * @param folderFileBean 参数
     * @return 复制后的文件夹信息
     */
    public void batchCopyFolderFile(String diskId, FolderFileBean folderFileBean);

    /**
     * <p>批量移动文件夹及文件<p/>
     *
     * @param folderFileBean 参数
     * @return 复制后的文件夹信息
     */
    public void batchMoveFolderFile(String diskId, FolderFileBean folderFileBean);

    /**
     * <p>批量保存共享文件及文件夹<p/>
     *
     * @param diskId         磁盘主键
     * @param shareId        共享id
     * @param folderFileBean 文件夹和文件的id集合
     * @return 文件和目录信息
     */
    public void batchSaveShareFolderFile(String diskId, String shareId, FolderFileBean folderFileBean);

    /**
     * 获取目标目录
     *
     * @param diskId   磁盘主键
     * @param folderId 目标目录id
     * @return 目标目录信息
     */
    public FolderEntity getTargetFolder(String diskId, String folderId);

    /**
     * 查询所有子集文件夹集合
     *
     * @param diskId     磁盘主键
     * @param parentNode 父节点
     * @return 文件夹集合
     */
    public List<FolderEntity> getChildrenFolder(String diskId, String parentNode);

    /**
     * 查询所有子集文件夹集合
     *
     * @param diskId     磁盘id
     * @param parentNode 父节点
     * @param action     回调
     * @return 文件夹集合
     */
    public List<FolderEntity> getChildrenFolder(String diskId, String parentNode, String parentNewNode, FolderHandle<FolderEntity> action);

    /**
     * <p>获取父级文件夹</p>
     *
     * @param diskId 磁盘主键
     * @param path   文件夹路径
     * @return 文件夹
     */
    public FolderEntity getFolderByPath(String diskId, String path);


    /**
     * 根据文件夹主键集合和文件主键集合获取目录下的所有文件信息
     *
     * @param folderIds    文件夹主键集合
     * @param fileIds      文件主键集合
     * @param shareIdModel 共享文件model，可为空
     * @return 所有文件信息
     */
    public FolderFileModel getFolderFiles(List<String> folderIds, List<String> fileIds, ShareIdModel shareIdModel);

    /**
     * <p>查询磁盘的文件夹集合</p>
     *
     * @param pagingBean   分页查询条件
     * @param folderEntity 查询条件
     * @return 磁盘文件夹集合
     */
    public PagingDto<FolderDto> getFolderPage(Integer current, PagingBean pagingBean, FolderEntity folderEntity);

    /**
     * 查询文件夹和文件
     *
     * @param diskId          磁盘主键
     * @param current         当前页
     * @param pagingBean      分页参数
     * @param queryFolderBean 参数
     * @return 文件夹和文件集合
     */
    public PagingDto<FolderFileDto> getFolderFilePage(String diskId, Integer current, PagingBean pagingBean, QueryFolderBean queryFolderBean);

    /**
     * <p>压缩文件和目录</p>
     *
     * @param diskEntity   磁盘信息
     * @param packFileBean 打包参数
     * @param token        用户token
     * @param forwardUrl   转发地址
     * @return 文件信息
     */
    public FileDto createFolderFilePack(DiskEntity diskEntity, PackFileBean packFileBean, String token, String forwardUrl);

    /**
     * 获取文件夹代销
     *
     * @param folderId 目录id
     * @param fileIds  共享的文件id集合，可为null
     * @return 文件夹大小
     */
    public String getFolderSize(String folderId, List<String> fileIds);

    /**
     * 根据文件夹主键获取路径
     *
     * @param folderId 文件及主键
     * @return 路径
     */
    public String getPathByFolderId(String folderId);

    /**
     * 根据文件夹获取路径
     *
     * @param folderEntity 文件夹
     * @return 路径
     */
    public String getPathByFolder(FolderEntity folderEntity);

    /**
     * 根据文件夹主键获取路径集合
     *
     * @param folderId  文件夹主键
     * @param folderIds 文件夹主键集合
     * @return 路径集合
     */
    public List<FolderEntity> getParentFolderList(String folderId, List<String> folderIds);

    /**
     * 添加文件夹及文件
     *
     * @param folders   文件夹集合
     * @param files     文件集合
     * @param folderIds 文件夹id集合
     * @param fileIds   文件id集合
     * @param diskId    磁盘主键
     */
    public void addFolderFile(List<FolderEntity> folders, List<FileEntity> files, List<String> folderIds, List<String> fileIds, String diskId);

}
