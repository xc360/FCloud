package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.xc.api.file.dto.FileDto;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.enums.Whether;
import com.xc.core.utils.RedisUtils;
import com.xc.core.utils.ServiceUtils;
import com.xc.file.bean.QueryShareFolderBean;
import com.xc.file.bean.ShareBean;
import com.xc.file.config.Constants;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.dto.ShareCodeDto;
import com.xc.file.dto.ShareDto;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.entity.ShareEntity;
import com.xc.file.entity.ShareFolderFileEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.enums.RedisTime;
import com.xc.file.mapper.FileMapper;
import com.xc.file.mapper.FolderMapper;
import com.xc.file.mapper.ShareFolderFileMapper;
import com.xc.file.mapper.ShareMapper;
import com.xc.file.model.DownloadModel;
import com.xc.file.model.QueryFolderFileModel;
import com.xc.file.model.ShareIdModel;
import com.xc.file.service.FileService;
import com.xc.file.service.FolderService;
import com.xc.file.service.ShareService;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>共享文件实现</p>
 *
 * @author xc
 * @version v1.0
 */
@Service
@Slf4j
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareEntity> implements ShareService {

    @Autowired
    private ShareFolderFileMapper shareFolderFileMapper;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private Constants constants;

    @Override
    @Transactional
    public ShareDto createShare(String diskId, ShareBean shareBean) {
        //创建共享文件
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setValidTime(shareBean.getValidTime());
        if (shareBean.getNeedCode() == 0) {
            String drawCode = StringUtils.random(6);
            shareEntity.setDrawCode(drawCode);
        }
        shareEntity.setRemark(shareBean.getRemark());
        shareEntity.setDiskId(diskId);
        shareEntity.setCode(StringUtils.generateOnlyId(constants.getMachineId()));
        if (!this.save(shareEntity)) {
            throw FailCode.SHARE_FILE_CREATE_FAIL.getOperateException();
        }
        // 查询所有文件夹
        List<FolderEntity> folders = new ArrayList<>();
        List<FileEntity> files = new ArrayList<>();
        folderService.addFolderFile(folders, files, shareBean.getFolderIds(), shareBean.getFileIds(), diskId);
        // 共享文件夹
        String folderName = null;
        for (FolderEntity folderEntity : folders) {
            for (String folderId : shareBean.getFolderIds()) {
                if (folderId.equals(folderEntity.getId()) && folderName == null) {
                    folderName = folderEntity.getName();
                }
            }
            // 验证是否可以共享
            if (!folderEntity.getDiskId().equals(diskId)) {
                throw FailCode.NOT_SHARE_OTHERS_FOLDER.getOperateException();
            }
            ShareFolderFileEntity share = new ShareFolderFileEntity();
            share.setShareId(shareEntity.getId());
            share.setFolderId(folderEntity.getId());
            share.setIsRoot(1);
            for (String id : shareBean.getFolderIds()) {
                if (id.equals(folderEntity.getId())) {
                    share.setIsRoot(0);
                }
            }
            if (!SqlHelper.retBool(shareFolderFileMapper.insert(share))) {
                throw FailCode.SHARE_FILE_RELATION_CREATE_FAIL.getOperateException();
            }
        }
        // 共享文件
        String fileName = null;
        for (FileEntity fileEntity : files) {
            for (String fileId : shareBean.getFileIds()) {
                if (fileId.equals(fileEntity.getId()) && fileName == null) {
                    fileName = fileEntity.getName();
                }
            }
            // 验证是否可以共享
            if (!fileEntity.getDiskId().equals(diskId)) {
                throw FailCode.NOT_SHARE_OTHERS_FILE.getOperateException();
            }
            ShareFolderFileEntity share = new ShareFolderFileEntity();
            share.setShareId(shareEntity.getId());
            share.setFileId(fileEntity.getId());
            share.setIsRoot(1);
            for (String id : shareBean.getFileIds()) {
                if (id.equals(fileEntity.getId())) {
                    share.setIsRoot(0);
                }
            }
            if (!SqlHelper.retBool(shareFolderFileMapper.insert(share))) {
                throw FailCode.SHARE_FILE_RELATION_CREATE_FAIL.getOperateException();
            }
        }
        // 货物文件名称
        String name = "";
        if (folders.size() > 0 && folderName != null) {
            name = folderName;
        }
        if (folders.size() > 0 && files.size() > 0 && fileName != null) {
            name += "，";
        }
        if (files.size() > 0 && fileName != null) {
            name += fileName;
        }
        // 更新共享名称
        if (shareBean.getFileIds().size() > 1 || shareBean.getFolderIds().size() > 1) {
            name = name + "，等";
        }
        shareEntity.setName(name);
        if (!this.updateById(shareEntity)) {
            throw FailCode.SHARE_FILE_CREATE_FAIL.getOperateException();
        }
        return ObjectUtils.convert(new ShareDto(), shareEntity);
    }

    @Override
    public PagingDto<ShareDto> getSharePage(String diskId, Integer current, PagingBean pagingBean, ShareBean shareBean) {
        ShareEntity shareEntity = ObjectUtils.convert(new ShareEntity(), shareBean);
        shareEntity.setDiskId(diskId);
        QueryWrapper<ShareEntity> queryWrapper = ServiceUtils.queryData(pagingBean, shareEntity);
        IPage<ShareEntity> iPage = this.page(new Page<>(current, pagingBean.getSize()), queryWrapper);
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), ShareDto::new));
    }

    @Override
    @Transactional
    public void deleteShare(String diskId, String shareId) {
        ShareEntity shareEntity = shareMapper.selectById(shareId);
        if (shareEntity == null) {
            throw FailCode.SHARE_INFO_NOT_EXIST.getOperateException();
        }
        // 验证数据权限
        if (!shareEntity.getDiskId().equals(diskId)) {
            throw FailCode.NOT_DATA_AUTHORITY.getOperateException();
        }
        QueryWrapper<ShareFolderFileEntity> queryWrapper = new QueryWrapper<>(new ShareFolderFileEntity(shareId));
        if (shareFolderFileMapper.selectList(queryWrapper).size() != 0) {
            if (!SqlHelper.retBool(shareFolderFileMapper.delete(queryWrapper))) {
                throw FailCode.SHARE_FILE_RELATION_DELETE_FAIL.getOperateException();
            }
        }
        if (!this.removeById(shareId)) {
            throw FailCode.SHARE_FILE_RELATION_DELETE_FAIL.getOperateException();
        }
    }

    @Override
    public PagingDto<FolderFileDto> getShareFolderFile(Integer current, PagingBean pagingBean, QueryShareFolderBean queryShareFolderBean) {
        String key = RedisPrefix.SHARE_DOWNLOAD.getKey() + queryShareFolderBean.getVisitCode();
        DownloadModel downloadModel = RedisUtils.get(key);
        if (downloadModel == null) {
            throw FailCode.SHARE_VISIT_CODE_ERROR.getOperateException();
        }
        QueryFolderFileModel queryFolderFileModel = ObjectUtils.convert(new QueryFolderFileModel(), pagingBean);
        queryFolderFileModel.setName(queryShareFolderBean.getName());
        queryFolderFileModel.setShareId(downloadModel.getShareId());
        if (!queryShareFolderBean.getOpenQueryAll()) {
            if (queryShareFolderBean.getFolderId() == null || "".equals(queryShareFolderBean.getFolderId())) {
                queryFolderFileModel.setIsRoot(Whether.YES.getValue());
            } else {
                FolderEntity targetFolder = folderMapper.selectById(queryShareFolderBean.getFolderId());
                queryFolderFileModel.setFolderId(targetFolder.getId());
                queryFolderFileModel.setParentNode(targetFolder.getNode());
            }
        }
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("is_file");
        ServiceUtils.querySort(queryWrapper, pagingBean);
        IPage<FolderFileDto> iPage = folderMapper.getFolderFilePage(new Page<>(current, pagingBean.getSize()), queryWrapper, queryFolderFileModel);
        List<FileEntity> files = iPage.getRecords().stream().filter(e -> e.getIsFile().equals("0")).map(obj -> ObjectUtils.convert(new FileEntity(), obj)).collect(Collectors.toList());
        // 设置下载url
        String visitCode = queryShareFolderBean.getVisitCode();
        List<FileDto> dtoList = ObjectUtils.convertList(files, FileDto::new);
        dtoList = fileService.setDownloadUrl(dtoList, StringUtils.analysisPath(constants.getShareDownloadPath(), visitCode));
        downloadModel.setFiles(dtoList);
        for (FolderFileDto folderFileDto : iPage.getRecords()) {
            if (folderFileDto.getIsFile().equals("0")) {
                for (FileDto fileDto : dtoList) {
                    if (folderFileDto.getId().equals(fileDto.getId())) {
                        folderFileDto.setUrl(fileDto.getUrl());
                    }
                }
            }
        }
        // 设置为有效允许下载
        RedisUtils.set(key, downloadModel, RedisTime.SHARE_DOWNLOAD_CACHE.getTime());
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), FolderFileDto::new));
    }

    @Override
    public ShareCodeDto verifyShareCode(String code, String drawCode) {
        ShareCodeDto shareCodeDto = new ShareCodeDto();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setCode(code);
        shareEntity = shareMapper.selectOne(new QueryWrapper<>(shareEntity));
        if (shareEntity == null) {
            throw FailCode.SHARE_INFO_NOT_EXIST.getOperateException();
        }
        if (shareEntity.getDrawCode() != null && !shareEntity.getDrawCode().equals("")) {
            if (drawCode != null && !drawCode.equals("")) {
                if (!shareEntity.getDrawCode().equals(drawCode)) {
                    throw FailCode.DRAW_CODE_ERROR.getOperateException();
                }
            } else {
                shareCodeDto.setNeedDrawCode(true);
                return shareCodeDto;
            }
        }
        if (shareEntity.getValidTime() != 0) {
            if ((shareEntity.getCreateTime().getTime() + shareEntity.getValidTime()) < System.currentTimeMillis()) {
                throw FailCode.SHARE_FILE_INVALID.getOperateException();
            }
        }
        shareEntity.setBrowseNum(shareEntity.getBrowseNum() + 1);
        if (!this.updateById(shareEntity)) {
            throw FailCode.SHARE_FILE_UPDATE_FAIL.getOperateException();
        }
        // 设置共享下载
        DownloadModel downloadModel = new DownloadModel();
        downloadModel.setShareId(shareEntity.getId());
        String shareVisitCode = StringUtils.generateOnlyId(constants.getMachineId());
        String key = RedisPrefix.SHARE_DOWNLOAD.getKey() + shareVisitCode;
        RedisUtils.set(key, downloadModel, RedisTime.SHARE_DOWNLOAD_CACHE.getTime());
        // 设置返回参数
        shareCodeDto.setDiskId(shareEntity.getDiskId());
        shareCodeDto.setShareId(shareEntity.getId());
        shareCodeDto.setVisitCode(shareVisitCode);
        shareCodeDto.setNeedDrawCode(false);
        return shareCodeDto;
    }


    @Override
    public String getShareFolderSize(String shareId, String folderId) {
        // 获取下级文件夹及文件
        ShareEntity shareEntity = verifyShare(shareId);
        List<ShareFolderFileEntity> shareFiles = getShareFolderFile(shareEntity);
        // 查询共享的文件遍历
        List<String> shareFileIds = new ArrayList<>();
        for (ShareFolderFileEntity shareFolderFileEntity : shareFiles) {
            if (shareFolderFileEntity.getFileId() != null) {
                shareFileIds.add(shareFolderFileEntity.getFileId());
            }
        }
        // 计算大小
        return folderService.getFolderSize(folderId, shareFileIds);
    }


    @Override
    public ShareEntity verifyShare(String shareId) {
        ShareEntity shareEntity = shareMapper.selectById(shareId);
        if (shareEntity == null) {
            throw FailCode.SHARE_FILE_NOT_EXIST.getOperateException();
        }
        if (shareEntity.getValidTime() != 0) {
            if ((shareEntity.getCreateTime().getTime() + shareEntity.getValidTime()) < System.currentTimeMillis()) {
                throw FailCode.SHARE_FILE_INVALID.getOperateException();
            }
        }
        return shareEntity;
    }

    /**
     * 根据共享id获取共享关联信息
     *
     * @param shareEntity 共享信息
     * @return 关联集合
     */
    private List<ShareFolderFileEntity> getShareFolderFile(ShareEntity shareEntity) {
        //根据共享id查询共享关联
        List<ShareFolderFileEntity> shareFiles = shareFolderFileMapper.selectList(new QueryWrapper<>(new ShareFolderFileEntity(shareEntity.getId())));
        if (shareFiles.size() == 0) {
            throw FailCode.SHARE_FILE_NOT_EXIST.getOperateException();
        }
        return shareFiles;
    }

    @Override
    public ShareIdModel getShareIdModelByShareId(String shareId) {
        // 查询共享信息
        ShareEntity shareEntity = verifyShare(shareId);
        // 查询共享文件信息
        List<ShareFolderFileEntity> shareFiles = getShareFolderFile(shareEntity);
        ShareIdModel shareIdModel = new ShareIdModel();
        shareIdModel.setShareEntity(shareEntity);
        shareIdModel.setFileIds(new ArrayList<>());
        shareIdModel.setFolderIds(new ArrayList<>());
        shareIdModel.setRootFileIds(new ArrayList<>());
        shareIdModel.setRootFolderIds(new ArrayList<>());
        for (ShareFolderFileEntity shareFolderFileEntity : shareFiles) {
            if (shareFolderFileEntity.getFileId() != null) {
                String id = shareFolderFileEntity.getFileId();
                if (shareFolderFileEntity.getIsRoot() == 0) {
                    shareIdModel.getRootFileIds().add(id);
                } else {
                    shareIdModel.getFileIds().add(id);
                }
            } else {
                String id = shareFolderFileEntity.getFolderId();
                if (shareFolderFileEntity.getIsRoot() == 0) {
                    shareIdModel.getRootFolderIds().add(id);
                } else {
                    shareIdModel.getFolderIds().add(id);
                }
            }
        }
        return shareIdModel;
    }

    @Override
    public List<FolderDto> getShareParentFolderList(String shareId, String folderId) {
        ShareIdModel shareIdModel = getShareIdModelByShareId(shareId);
        List<String> folderIds = new ArrayList<>();
        folderIds.addAll(shareIdModel.getRootFolderIds());
        folderIds.addAll(shareIdModel.getFolderIds());
        List<FolderEntity> entities = folderService.getParentFolderList(folderId, folderIds);
        return ObjectUtils.convertList(entities, FolderDto::new);
    }
}
