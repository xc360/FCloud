package com.xc.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.file.bean.QueryShareFolderBean;
import com.xc.file.bean.ShareBean;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.dto.ShareCodeDto;
import com.xc.file.dto.ShareDto;
import com.xc.file.entity.ShareEntity;
import com.xc.file.model.ShareIdModel;

import java.util.List;

/**
 * <p>共享文件接口</p>
 *
 * @author xc
 * @version v1.0
 */
public interface ShareService extends IService<ShareEntity> {

    /**
     * <p>创建磁盘的共享</p>
     *
     * @param diskId    磁盘主键
     * @param shareBean 共享信息
     * @return 创建成功的信息
     */
    public ShareDto createShare(String diskId, ShareBean shareBean);


    /**
     * <p>获取磁盘的共享页数据</p>
     *
     * @param diskId     磁盘主键
     * @param current    当前页
     * @param pagingBean 分页参数
     * @param shareBean  共享参数
     * @return 前磁盘的共享文件集合
     */
    public PagingDto<ShareDto> getSharePage(String diskId, Integer current, PagingBean pagingBean, ShareBean shareBean);

    /**
     * <p>删除磁盘的共享</p>
     *
     * @param diskId  磁盘主键
     * @param shareId 共享id
     */
    public void deleteShare(String diskId, String shareId);

    /**
     * <p>查询共享文件夹及文件</p>
     *
     * @param current              当前页
     * @param pagingBean           分页参数
     * @param queryShareFolderBean 查询条件
     * @return 共享文件信息
     */
    public PagingDto<FolderFileDto> getShareFolderFile(Integer current, PagingBean pagingBean, QueryShareFolderBean queryShareFolderBean);


    /**
     * <p>验证共享文件是否需要提取码</p>
     *
     * @param code 共享文件代码
     * @return 不需要提取码返回共享id
     */
    public ShareCodeDto verifyShareCode(String code, String drawCode);


    /**
     * 获取共享文件夹大小
     *
     * @param shareId  共享id
     * @param folderId 文件夹id
     * @return 文件夹大小
     */
    public String getShareFolderSize(String shareId, String folderId);


    /**
     * 验证共享信息
     *
     * @param shareId 共享id
     * @return 验证成功的数据
     */
    public ShareEntity verifyShare(String shareId);

    /**
     * 获取共享文件/文件夹的id信息的
     *
     * @param shareId 共享id
     * @return id信息
     */
    public ShareIdModel getShareIdModelByShareId(String shareId);

    /**
     * 获取共享的父级文件夹集合
     *
     * @param shareId  共享主键
     * @param folderId 目录主键
     * @return 共享父级文件夹集合
     */
    public List<FolderDto> getShareParentFolderList(String shareId, String folderId);
}
