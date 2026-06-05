package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.api.file.dto.FileDto;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.PagingBean;
import com.xc.core.bean.SignBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.enums.Whether;
import com.xc.core.utils.RedisUtils;
import com.xc.core.utils.ServiceUtils;
import com.xc.file.bean.*;
import com.xc.file.config.Constants;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.entity.*;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.FileSuffix;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.enums.RedisTime;
import com.xc.file.function.FolderHandle;
import com.xc.file.mapper.FileMapper;
import com.xc.file.mapper.FolderMapper;
import com.xc.file.model.DownloadModel;
import com.xc.file.model.FolderFileModel;
import com.xc.file.model.QueryFolderFileModel;
import com.xc.file.model.ShareIdModel;
import com.xc.file.service.*;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>文件夹服务实现类</p>
 *
 * @author xc
 * @version v1.0
 */
@Service
@Slf4j
public class FolderServiceImpl extends ServiceImpl<FolderMapper, FolderEntity> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Lazy
    @Autowired
    private FileService fileService;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private Constants constants;
    @Autowired
    private RestTemplate restTemplate;
    @Lazy
    @Autowired
    private UploadService uploadService;
    @Lazy
    @Autowired
    private ShareService shareService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private FileHashService fileHashService;
    @Autowired
    private DiskService diskService;
    @Autowired
    private BasicConstants basicConstants;
    @Lazy
    @Autowired
    private DownloadService downloadService;

    @Override
    public synchronized FolderEntity createParentFolders(String parentNode, String diskId, String path) {
        FolderEntity entity = null;
        // 创建文件夹
        String[] folderPaths = FileUtils.getFolderPaths(path != null ? path : "");
        // 验证文件夹是否存在
        for (String folderName : folderPaths) {
            FolderEntity folderEntity = new FolderEntity();
            folderEntity.setParentNode(parentNode);
            folderEntity.setDiskId(diskId);
            folderEntity.setName(folderName);
            FolderEntity folder = folderMapper.selectOne(new QueryWrapper<>(folderEntity));
            if (folder == null) {
                // 创建文件夹
                try {
                    folder = new FolderEntity();
                    folder.setParentNode(parentNode);
                    folder.setDiskId(diskId);
                    folder.setName(folderName);
                    folder.setNode(IdWorker.getIdStr());
                    if (!this.save(folder)) {
                        throw FailCode.FOLDER_CREATE_FAIL.getOperateException();
                    }
                } catch (DuplicateKeyException e) {
                    throw FailCode.FOLDER_EXIST.getOperateException();
                }
            }
            parentNode = folder.getNode();
            entity = folder;
        }
        return entity;
    }

    @Override
    public FolderEntity createFolder(String diskId, String folderId, FolderBean folderBean) {
        if (folderBean.getName() == null || folderBean.getName().equals("")) {
            throw FailCode.FOLDER_NAME_NOT_NULL.getOperateException();
        }
        FolderEntity folder = null;
        if (folderId != null && !"".equals(folderId)) {
            // 查询父级文件夹
            folder = folderMapper.selectById(folderId);
            // 验证父级文件夹是否存在
            if (folder == null) {
                throw FailCode.PARENT_FOLDER_NOT_EXIST.getOperateException();
            }
            // 验证父级文件夹是不是当前磁盘的
            if (!folder.getDiskId().equals(diskId)) {
                throw FailCode.FOLDER_NOT_DISK.getOperateException();
            }
            diskId = folder.getDiskId();
        }
        // 判断当前文件夹是否重复，重复重命名
        FolderEntity folderEntity = new FolderEntity();
        if (folder == null) {
            folderEntity.setParentNode(constants.getRoot());
        } else {
            folderEntity.setParentNode(folder.getNode());
        }
        folderEntity.setName(folderBean.getName());
        folderEntity.setDiskId(diskId);
        FolderEntity entity = folderMapper.selectOne(new QueryWrapper<>(folderEntity));
        if (entity != null) {
            //不等于空，重命名文件
            String name = FileUtils.rename(entity.getName());
            folderEntity.setName(name);
        }
        // 创建文件夹
        try {
            folderEntity.setNode(IdWorker.getIdStr());
            if (!this.save(folderEntity)) {
                throw FailCode.FOLDER_CREATE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.FOLDER_EXIST.getOperateException();
        }
        return folderEntity;
    }

    @Override
    public FolderDto updateFolder(String diskId, String folderId, UpdateFolderBean updateFolderBean) {
        FolderEntity folderEntity = folderMapper.selectById(folderId);
        if (folderEntity == null) {
            throw FailCode.FOLDER_NOT_EXIST.getOperateException();
        }
        // 验证数据权限
        if (!folderEntity.getDiskId().equals(diskId)) {
            throw FailCode.FOLDER_NOT_DISK.getOperateException();
        }
        // 修改文件夹
        try {
            folderEntity.setName(updateFolderBean.getName());
            if (!this.updateById(folderEntity)) {
                throw FailCode.FOLDER_UPDATE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.FOLDER_EXIST.getOperateException();
        }
        return ObjectUtils.convert(new FolderDto(), folderEntity);
    }

    @Override
    @Transactional
    public void deleteFolder(String diskId, String folderId) {
        //获取文件夹信息
        FolderEntity folderEntity = folderMapper.selectById(folderId);
        //验证文件夹信息是否存在
        if (folderEntity == null) {
            throw FailCode.FOLDER_NOT_EXIST.getOperateException();
        }
        // 验证数据权限
        if (!folderEntity.getDiskId().equals(diskId)) {
            throw FailCode.FOLDER_NOT_DISK.getOperateException();
        }
        // 查询所有子集文件夹集合
        List<FolderEntity> folders = getChildrenFolder(folderEntity.getDiskId(), folderEntity.getNode());
        folders.add(folderEntity);
        String folderDiskId = folders.get(0).getDiskId();
        List<String> folderIds = folders.stream().map(FolderEntity::getId).collect(Collectors.toList());
        if (folderIds.size() > 0) {
            // 删除文件
            List<FileEntity> files = fileService.getFilesByFolderIds(folderDiskId, folderIds);
            if (files.size() > 0) {
                List<String> fileIds = files.stream().map(FileEntity::getId).collect(Collectors.toList());
                if (!fileService.removeByIds(fileIds)) {
                    throw FailCode.FILE_DELETE_FAIL.getOperateException();
                }
            }
            // 删除文件夹
            if (!this.removeByIds(folderIds)) {
                throw FailCode.FOLDER_DELETE_FAIL.getOperateException();
            }
        }
    }

    @Override
    @Transactional
    public void batchCopyFolderFile(String diskId, FolderFileBean folderFileBean) {
        // 查询目标文件夹
        FolderEntity targetFolder = getTargetFolder(diskId, folderFileBean.getFolderId());
        // 查询所有文件夹
        FolderFileModel folderFileModel = getFolderFiles(folderFileBean.getFolderIds(), folderFileBean.getFileIds(), null);
        // 验证父级文件夹是否正确，验证目标文件夹是否存在
        verifyRoot(targetFolder, folderFileModel);
        // 复制文件目录
        batchCopyFolderFile(targetFolder, folderFileModel, null);
    }

    @Override
    @Transactional
    public void batchMoveFolderFile(String diskId, FolderFileBean folderFileBean) {
        // 查询目标文件夹
        FolderEntity targetFolder = getTargetFolder(diskId, folderFileBean.getFolderId());
        // 不能向他人文件夹移动
        if (!targetFolder.getDiskId().equals(diskId)) {
            throw FailCode.NOT_TOWARDS_OTHERS_FOLDER_MOVE.getOperateException();
        }
        // 查询所有文件夹
        FolderFileModel folderFileModel = getFolderFiles(folderFileBean.getFolderIds(), folderFileBean.getFileIds(), null);
        // 验证父级文件夹是否正确，验证目标文件夹是否存在
        verifyRoot(targetFolder, folderFileModel);
        // 移动目录
        for (FolderEntity entity : folderFileModel.getRootFolders()) {
            // 验证不能向下级移动
            if (targetFolder.getId() != null && !targetFolder.getId().equals(constants.getRoot())) {
                List<FolderEntity> entities = getParentFolderList(targetFolder.getId(), null);
                for (FolderEntity folderEntity : entities) {
                    if (folderEntity.getId().equals(entity.getId())) {
                        throw FailCode.CANNOT_DOWN_MOVE.getOperateException();
                    }
                }
            }
            // 验证文件夹是否是自己的
            if (!entity.getDiskId().equals(diskId)) {
                throw FailCode.NOT_MOVE_OTHERS_FOLDER.getOperateException();
            }
            entity.setParentNode(targetFolder.getNode());
            // 移动文件夹
            if (!updateById(entity)) {
                throw FailCode.FOLDER_COPY_FAIL.getOperateException();
            }
        }
        // 移动文件
        for (FileEntity entity : folderFileModel.getRootFiles()) {
            // 验证文件是否是自己的
            if (!entity.getDiskId().equals(diskId)) {
                throw FailCode.NOT_MOVE_OTHERS_FILE.getOperateException();
            }
            entity.setFolderId(targetFolder.getId());
            if (!fileService.updateById(entity)) {
                throw FailCode.COPY_FILE_FAIL.getOperateException();
            }
        }
    }

    @Override
    @Transactional
    public void batchSaveShareFolderFile(String diskId, String shareId, FolderFileBean folderFileBean) {
        // 查询目标文件夹
        FolderEntity targetFolder = getTargetFolder(diskId, folderFileBean.getFolderId());
        // 不能向他人文件夹保存
        if (!targetFolder.getDiskId().equals(diskId)) {
            throw FailCode.NOT_TOWARDS_OTHERS_FOLDER_SAVE.getOperateException();
        }
        // 获取共享的文件夹/文件信息
        ShareIdModel shareIdModel = shareService.getShareIdModelByShareId(shareId);
        //查询共享文件信息，修改保存次数
        ShareEntity shareEntity = shareIdModel.getShareEntity();
        shareEntity.setPreserveNum(shareEntity.getPreserveNum() + 1);
        if (!shareService.updateById(shareEntity)) {
            throw FailCode.SHARE_FILE_UPDATE_FAIL.getOperateException();
        }
        // 获取根文件夹下的文件夹
        FolderFileModel folderFileModel = getFolderFiles(folderFileBean.getFolderIds(), folderFileBean.getFileIds(), shareIdModel);
        // 验证父级文件夹是否正确，验证目标文件夹是否存在
        verifyRoot(targetFolder, folderFileModel);
        // 复制目录
        batchCopyFolderFile(targetFolder, folderFileModel, shareIdModel);
    }

    @Override
    public FolderEntity getTargetFolder(String diskId, String folderId) {
        FolderEntity targetFolder;
        if (folderId == null || "".equals(folderId)) {
            targetFolder = new FolderEntity();
            targetFolder.setDiskId(diskId);
            targetFolder.setId(constants.getRoot());
            targetFolder.setNode(constants.getRoot());
        } else {
            targetFolder = folderMapper.selectById(folderId);
            if (targetFolder == null) {
                throw FailCode.TARGET_FOLDER_NOT_NULL.getOperateException();
            }
        }
        return targetFolder;
    }


    /**
     * 批量复制文件夹及文件
     *
     * @param targetFolder    目标文件夹
     * @param folderFileModel 目录文件信息
     * @return 文件及文件夹信息
     */
    private void batchCopyFolderFile(FolderEntity targetFolder, FolderFileModel folderFileModel, ShareIdModel shareIdModel) {
        // 复制文件夹
        for (FolderEntity entity : folderFileModel.getRootFolders()) {
            String oldNode = entity.getNode();
            String newNode = IdWorker.getIdStr();
            // 创建下级
            getChildrenFolder(entity.getDiskId(), oldNode, newNode, (folderList, parentNewNode) -> {
                // 处理文件夹
                if (shareIdModel != null) {
                    // 保存共享文件处理
                    for (FolderEntity folderEntity : folderList) {
                        for (String folderId : shareIdModel.getFolderIds()) {
                            if (folderEntity.getId().equals(folderId)) {
                                String node = IdWorker.getIdStr();
                                createFolderFile(targetFolder.getDiskId(), parentNewNode, node, folderEntity, shareIdModel);
                            }
                        }
                    }
                } else {
                    // 复制文件夹处理
                    for (FolderEntity folderEntity : folderList) {
                        String node = IdWorker.getIdStr();
                        createFolderFile(targetFolder.getDiskId(), parentNewNode, node, folderEntity, null);
                    }
                }
            });
            // 创建文件夹及文件
            createFolderFile(targetFolder.getDiskId(), targetFolder.getNode(), newNode, entity, shareIdModel);
        }
        // 复制文件
        fileService.batchCopyFile(targetFolder.getDiskId(), targetFolder.getId(), folderFileModel.getRootFiles());
        // 验证网盘空间是否足够
        diskService.verifyDiskSpace(targetFolder.getDiskId(), 0);
    }

    /**
     * 创建文件夹及文件
     *
     * @param diskId       磁盘主键
     * @param targetNode   目标父级文件夹节点
     * @param entity       文件夹实体
     * @param shareIdModel 共享文件信息
     */
    private void createFolderFile(String diskId, String targetNode, String newNode, FolderEntity entity, ShareIdModel shareIdModel) {
        // 复制文件夹
        String folderId = entity.getId();
        entity.setId(null);
        entity.setUpdateTime(null);
        entity.setCreateTime(null);
        entity.setVersion(0);
        entity.setDiskId(diskId);
        entity.setNode(newNode);
        entity.setParentNode(targetNode);
        if (!save(entity)) {
            throw FailCode.FOLDER_COPY_FAIL.getOperateException();
        }
        // 查询文件
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFolderId(folderId);
        List<FileEntity> fileList = fileMapper.selectList(new QueryWrapper<>(fileEntity));
        // 处理文件
        List<FileEntity> files = new ArrayList<>();
        if (shareIdModel != null) {
            for (String fileId : shareIdModel.getFileIds()) {
                for (FileEntity file : fileList) {
                    if (file.getId().equals(fileId)) {
                        files.add(file);
                    }
                }
            }
        } else {
            files.addAll(fileList);
        }
        // 复制文件
        fileService.batchCopyFile(diskId, entity.getId(), files);
    }


    /**
     * 验证父级文件夹是否正确
     * 验证目标文件夹是否存在
     *
     * @param targetFolder    目标文件夹
     * @param folderFileModel 文件夹及文件信息
     */
    private void verifyRoot(FolderEntity targetFolder, FolderFileModel folderFileModel) {
        // 验证根文件夹
        if (folderFileModel.getRootFolders().size() > 0) {
            String parentNode = null;
            for (FolderEntity folderEntity : folderFileModel.getRootFolders()) {
                if (parentNode == null) {
                    parentNode = folderEntity.getParentNode();
                }
                // 验证待移动的文件夹父级是否相同
                if (!parentNode.equals(folderEntity.getParentNode())) {
                    throw FailCode.PARENT_FOLDER_ERROR.getOperateException();
                }
                // 验证文件夹是否存在
                FolderEntity folder = new FolderEntity();
                folder.setParentNode(targetFolder.getNode());
                folder.setName(folderEntity.getName());
                folder.setDiskId(targetFolder.getDiskId());
                FolderEntity entity = folderMapper.selectOne(new QueryWrapper<>(folder));
                if (entity != null) {
                    throw FailCode.FOLDER_EXIST.getOperateException();
                }
            }
        }
        // 验证根文件
        if (folderFileModel.getRootFiles().size() > 0) {
            String folderId = null;
            for (FileEntity fileEntity : folderFileModel.getRootFiles()) {
                if (folderId == null) {
                    folderId = fileEntity.getFolderId();
                }
                // 验证待移动的文件夹父级是否相同
                if (!folderId.equals(fileEntity.getFolderId())) {
                    throw FailCode.PARENT_FOLDER_ERROR.getOperateException();
                }

                // 验证文件是否存在
                FileEntity entity = new FileEntity();
                entity.setFolderId(targetFolder.getId());
                entity.setName(fileEntity.getName());
                entity.setDiskId(targetFolder.getDiskId());
                FileEntity file = fileService.getOne(new QueryWrapper<>(entity));
                if (file != null) {
                    throw FailCode.FILE_EXIST.getOperateException();
                }
            }
        }
    }

    @Override
    public List<FolderEntity> getChildrenFolder(String diskId, String parentNode) {
        return getChildrenFolder(diskId, parentNode, null, null);
    }

    @Override
    public List<FolderEntity> getChildrenFolder(String diskId, String parentNode, String parentNewNode, FolderHandle<FolderEntity> action) {
        FolderEntity folderEntity = new FolderEntity();
        folderEntity.setDiskId(diskId);
        folderEntity.setParentNode(parentNode);
        List<FolderEntity> folders = folderMapper.selectList(new QueryWrapper<>(folderEntity));
        List<String> newNodes = new ArrayList<>();
        if (action != null) {
            List<FolderEntity> entities = ObjectUtils.convertList(folders, FolderEntity::new);
            action.handle(entities, parentNewNode);
            newNodes.addAll(entities.stream().map(FolderEntity::getNode).collect(Collectors.toList()));
        }
        int index = 0;
        while (index < folders.size()) {
            FolderEntity entity = folders.get(index);
            String newNode = null;
            if (action != null) {
                newNode = newNodes.get(index);
            }
            FolderEntity folder = new FolderEntity();
            folder.setDiskId(diskId);
            folder.setParentNode(entity.getNode());
            List<FolderEntity> folderList = folderMapper.selectList(new QueryWrapper<>(folder));
            if (folderList.size() > 0) {
                if (action != null) {
                    List<FolderEntity> entities = ObjectUtils.convertList(folderList, FolderEntity::new);
                    action.handle(entities, newNode);
                    newNodes.addAll(entities.stream().map(FolderEntity::getNode).collect(Collectors.toList()));
                }
                folders.addAll(folderList);
            }
            index++;
        }
        return folders;
    }

    @Override
    public FolderEntity getFolderByPath(String diskId, String path) {
        if (path.contains("/")) {
            String[] folderPaths = FileUtils.getFolderPaths(path);
            String parentNode = constants.getRoot();
            FolderEntity folderEntity = null;
            for (String folderName : folderPaths) {
                FolderEntity entity = new FolderEntity();
                entity.setDiskId(diskId);
                entity.setParentNode(parentNode);
                entity.setName(folderName);
                folderEntity = folderMapper.selectOne(new QueryWrapper<>(entity));
                if (folderEntity == null) {
                    return null;
                }
                parentNode = folderEntity.getNode();
            }
            if (folderEntity == null) {
                throw FailCode.FOLDER_PATH_ERROR.getOperateException();
            }
            return folderEntity;
        } else {
            throw FailCode.FOLDER_PATH_ERROR.getOperateException();
        }
    }

    @Override
    public FolderFileModel getFolderFiles(List<String> folderIds, List<String> fileIds, ShareIdModel shareIdModel) {
        List<String> rootFolders = new ArrayList<>();
        List<String> rootFiles = new ArrayList<>();
        if (shareIdModel != null) {
            List<String> shareFolderIds = shareIdModel.getRootFolderIds();
            shareFolderIds.addAll(shareIdModel.getFolderIds());
            for (String folderId : folderIds) {
                for (String shareFolderId : shareFolderIds) {
                    if (folderId.equals(shareFolderId)) {
                        rootFolders.add(shareFolderId);
                    }
                }
            }
            List<String> shareFileIds = shareIdModel.getRootFileIds();
            shareFileIds.addAll(shareIdModel.getFileIds());
            for (String fileId : fileIds) {
                for (String shareFileId : shareFileIds) {
                    if (fileId.equals(shareFileId)) {
                        rootFiles.add(shareFileId);
                    }
                }
            }
        } else {
            rootFolders.addAll(folderIds);
            rootFiles.addAll(fileIds);
        }
        FolderFileModel folderFileModel = new FolderFileModel();
        // 获取根文件夹
        if (rootFolders.size() > 0) {
            folderFileModel.setRootFolders(folderMapper.selectBatchIds(rootFolders));
        } else {
            folderFileModel.setRootFolders(new ArrayList<>());
        }
        // 获取根文件
        if (rootFiles.size() > 0) {
            folderFileModel.setRootFiles(fileMapper.selectBatchIds(rootFiles));
        } else {
            folderFileModel.setRootFiles(new ArrayList<>());
        }
        // 查询子集数据
        return folderFileModel;
    }

    @Override
    public PagingDto<FolderDto> getFolderPage(Integer current, PagingBean pagingBean, FolderEntity folderEntity) {
        QueryWrapper<FolderEntity> queryWrapper = ServiceUtils.queryData(pagingBean, folderEntity);
        IPage<FolderEntity> iPage = this.page(new Page<>(current, pagingBean.getSize()), queryWrapper);
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), FolderDto::new));
    }

    @Override
    public PagingDto<FolderFileDto> getFolderFilePage(String diskId, Integer current, PagingBean pagingBean, QueryFolderBean queryFolderBean) {
        QueryFolderFileModel queryFolderFileModel = ObjectUtils.convert(new QueryFolderFileModel(), queryFolderBean);
        if (!queryFolderBean.getOpenQueryAll()) {
            if (queryFolderBean.getFolderId() == null || "".equals(queryFolderBean.getFolderId())) {
                queryFolderFileModel.setFolderId(constants.getRoot());
                queryFolderFileModel.setParentNode(constants.getRoot());
            } else {
                FolderEntity folder = this.getTargetFolder(diskId, queryFolderBean.getFolderId());
                queryFolderFileModel.setFolderId(folder.getId());
                queryFolderFileModel.setParentNode(folder.getNode());
            }
        }
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("is_file");
        ServiceUtils.querySort(queryWrapper, pagingBean);
        IPage<FolderFileDto> iPage = folderMapper.getFolderFilePage(new Page<>(current, pagingBean.getSize()), queryWrapper, queryFolderFileModel);
        List<FileEntity> files = iPage.getRecords().stream().filter(e -> e.getIsFile().equals("0")).map(obj -> ObjectUtils.convert(new FileEntity(), obj)).collect(Collectors.toList());
        // 设置下载url
        List<FileDto> dtoList = ObjectUtils.convertList(files, FileDto::new);
        dtoList = fileService.setDownloadUrl(dtoList, StringUtils.analysisPath(constants.getDiskDownloadPath(), diskId));
        // 设置为有效允许下载
        DownloadModel downloadModel = new DownloadModel();
        downloadModel.setFiles(dtoList);
        String key = RedisPrefix.USER_DOWNLOAD.getKey() + diskId;
        RedisUtils.set(key, downloadModel, RedisTime.USER_DOWNLOAD_CACHE.getTime());
        // 处理url
        for (FolderFileDto folderFileDto : iPage.getRecords()) {
            if (folderFileDto.getIsFile().equals(Whether.YES.getValue())) {
                for (FileDto fileDto : dtoList) {
                    if (folderFileDto.getId().equals(fileDto.getId())) {
                        folderFileDto.setUrl(fileDto.getUrl());
                    }
                }
            }
        }
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), FolderFileDto::new));
    }

    @Override
    public FileDto createFolderFilePack(DiskEntity diskEntity, PackFileBean packFileBean, String token, String forwardUrl) {
        String diskId = diskEntity.getId();
        if (packFileBean.getName() == null || "".equals(packFileBean.getName())) {
            throw FailCode.PACK_NAME_NOT_NULL.getOperateException();
        }
        // 查询目标文件夹
        FolderEntity targetFolder = getTargetFolder(diskId, packFileBean.getFolderId());
        // 不能向他人文件夹保存
        if (!targetFolder.getDiskId().equals(diskId)) {
            throw FailCode.NOT_TOWARDS_OTHERS_FOLDER_SAVE.getOperateException();
        }
        // 查询所有文件
        List<FolderEntity> folders = new ArrayList<>();
        List<FileEntity> files = new ArrayList<>();
        addFolderFile(folders, files, packFileBean.getFolderIds(), packFileBean.getFileIds(), diskId);
        //计算出目录的大小
        long size = 0;
        for (FileEntity entity : files) {
            size += entity.getSize();
        }
        // 验证文件大小是否存的下
        diskService.verifyDiskSpace(diskEntity, size);
        // 检测本服务器空间是否支持打包，不支持调用其他服务器接口打包
        String path = uploadService.getFilePathBySize(size);
        if (path == null) {
            String serviceIp = fileCacheService.getServiceIp(size);
            forwardUrl = serviceIp + forwardUrl;
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("token", token);
            HttpEntity<PackFileBean> requestEntity = new HttpEntity<>(packFileBean, requestHeaders);
            ResponseEntity<FileDto> responseEntity = restTemplate.exchange(forwardUrl, HttpMethod.POST, requestEntity, FileDto.class);
            return responseEntity.getBody();
        }
        String fileName = StringUtils.generateOnlyId(constants.getMachineId());
        String tempPath = path + File.separator + fileName + FileSuffix.PACK.getSuffix();
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(tempPath));
             CheckedOutputStream cos = new CheckedOutputStream(outputStream, new CRC32());
             ZipOutputStream zipStream = new ZipOutputStream(cos)) {
            // 设置下载url
            List<FileDto> dtoList = ObjectUtils.convertList(files, FileDto::new);
            dtoList = fileService.setDownloadUrl(dtoList, StringUtils.analysisPath(constants.getDiskDownloadPath(), diskId));
            // 设置为有效允许下载
            DownloadModel downloadModel = new DownloadModel();
            downloadModel.setFiles(dtoList);
            String key = RedisPrefix.USER_DOWNLOAD.getKey() + diskId;
            RedisUtils.set(key, downloadModel, RedisTime.USER_DOWNLOAD_CACHE.getTime());
            // 打包文件夹
            for (FolderEntity folderEntity : folders) {
                String folderPath = getPackPath(folders, folderEntity.getId());
                ZipEntry entry = new ZipEntry(folderPath.replace("/", File.separator) + "/");
                zipStream.putNextEntry(entry);
            }
            // 打包文件
            for (FileDto fileDto : dtoList) {
                String folderPath = "";
                if (folders.size() > 0) {
                    folderPath = getPackPath(folders, fileDto.getFolderId());
                }
                String filePath = folderPath.equals("") ?
                        fileDto.getName() :
                        folderPath.replace("/", File.separator) + "/" + fileDto.getName();
                ZipEntry entry = new ZipEntry(filePath);
                zipStream.putNextEntry(entry);
                httpDownloadFile(zipStream, fileDto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建文件
        File tempFile = new File(tempPath);
        String folderId = targetFolder.getId() != null ? targetFolder.getId() : constants.getRoot();
        return fileHashService.createFileHash(diskEntity, tempFile, path, folderId, packFileBean.getName() + ".zip");
    }

    /**
     * 获取集合里面的文件夹
     *
     * @param folders  文件夹信息
     * @param folderId 文件夹id
     * @param node     节点
     * @return 文件夹
     */
    private FolderEntity getListFolder(List<FolderEntity> folders, String folderId, String node) {
        for (FolderEntity folderEntity : folders) {
            if (folderId != null) {
                if (folderId.equals(folderEntity.getId())) {
                    return folderEntity;
                }
            }
            if (node != null) {
                if (node.equals(folderEntity.getNode())) {
                    return folderEntity;
                }
            }
        }
        return null;
    }

    /**
     * 打包文件的路径
     *
     * @param folders  文件夹信息
     * @param folderId 文件夹id
     * @return 文件路径
     */
    private String getPackPath(List<FolderEntity> folders, String folderId) {
        StringBuilder path = new StringBuilder();
        String node = constants.getRoot();
        if (!folderId.equals(constants.getRoot())) {
            FolderEntity folderEntity = getListFolder(folders, folderId, null);
            if (folderEntity == null) {
                throw FailCode.FOLDER_NOT_EXIST.getOperateException();
            }
            node = folderEntity.getParentNode();
            path.append(folderEntity.getName());
        }
        while (true) {
            FolderEntity entity = getListFolder(folders, null, node);
            if (entity == null) {
                break;
            }
            node = entity.getParentNode();
            path.insert(0, entity.getName() + "/");
        }
        return path.toString();
    }

    @Override
    public String getFolderSize(String folderId, List<String> shareFileIds) {
        FolderEntity folderEntity = folderMapper.selectById(folderId);
        List<FolderEntity> folders = getChildrenFolder(folderEntity.getDiskId(), folderEntity.getNode());
        folders.add(folderEntity);
        BigDecimal bigDecimal = new BigDecimal("0.00");
        if (folders.size() > 0) {
            String diskId = folders.get(0).getDiskId();
            List<String> folderIds = folders.stream().map(FolderEntity::getId).collect(Collectors.toList());
            List<FileEntity> files = fileService.getFilesByFolderIds(diskId, folderIds);
            for (FileEntity fileEntity : files) {
                if (shareFileIds == null) {
                    bigDecimal = bigDecimal.add(new BigDecimal(fileEntity.getSize()));
                } else {
                    for (String shareFileId : shareFileIds) {
                        if (shareFileId.equals(fileEntity.getId())) {
                            bigDecimal = bigDecimal.add(new BigDecimal(fileEntity.getSize()));
                        }
                    }
                }
            }
        }
        return bigDecimal.toString();
    }

    @Override
    public String getPathByFolderId(String folderId) {
        if (constants.getRoot().equals(folderId)) {
            return "";
        }
        FolderEntity folderEntity = folderMapper.selectById(folderId);
        return getPathByFolder(folderEntity);
    }

    @Override
    public String getPathByFolder(FolderEntity folderEntity) {
        StringBuilder path = new StringBuilder(folderEntity.getName());
        String node = folderEntity.getParentNode();
        while (true) {
            FolderEntity folder = new FolderEntity();
            folder.setNode(node);
            FolderEntity entity = folderMapper.selectOne(new QueryWrapper<>(folder));
            if (entity == null) {
                break;
            }
            node = entity.getParentNode();
            path.insert(0, entity.getName() + "/");
        }
        return "/" + path;
    }

    @Override
    public List<FolderEntity> getParentFolderList(String folderId, List<String> folderIds) {
        List<FolderEntity> folders = new ArrayList<>();
        // 查文件夹
        FolderEntity folderEntity1 = new FolderEntity();
        folderEntity1.setId(folderId);
        QueryWrapper<FolderEntity> queryWrapper = new QueryWrapper<>(folderEntity1);
        if (folderIds != null) {
            queryWrapper.lambda().in(FolderEntity::getId, folderIds);

        }
        FolderEntity folderEntity = folderMapper.selectOne(queryWrapper);
        if (folderEntity == null) {
            throw FailCode.FOLDER_NOT_EXIST.getOperateException();
        }
        // 处理数据
        folders.add(folderEntity);
        String node = folderEntity.getParentNode();
        while (true) {
            FolderEntity folder = new FolderEntity();
            folder.setNode(node);
            QueryWrapper<FolderEntity> wrapper = new QueryWrapper<>(folder);
            if (folderIds != null) {
                wrapper.lambda().in(FolderEntity::getId, folderIds);
            }
            FolderEntity entity = folderMapper.selectOne(wrapper);
            if (entity == null) {
                break;
            }
            node = entity.getParentNode();
            folders.add(entity);
        }
        Collections.reverse(folders);
        return folders;
    }

    @Override
    public void addFolderFile(List<FolderEntity> folders, List<FileEntity> files, List<String> folderIds, List<String> fileIds, String diskId) {
        // 查询所有文件
        if (folderIds.size() > 0) {
            List<FolderEntity> entities = folderMapper.selectBatchIds(folderIds);
            for (FolderEntity entity : entities) {
                folders.add(entity);
                folders.addAll(this.getChildrenFolder(diskId, entity.getNode()));
            }
        }
        // 查询所有文件
        List<String> ids = folders.stream().map(FolderEntity::getId).collect(Collectors.toList());
        if (ids.size() > 0) {
            files.addAll(fileService.getFilesByFolderIds(diskId, ids));
        }
        if (fileIds.size() > 0) {
            files.addAll(fileMapper.selectBatchIds(fileIds));
        }
    }

    /**
     * 发送http 请求下载文件打包
     *
     * @param zipStream 打包输出流
     * @param fileDto   文件信息
     */
    private void httpDownloadFile(ZipOutputStream zipStream, FileDto fileDto) {
        FileHashEntity fileHashEntity = fileHashService.getById(fileDto.getHashId());
        File file;
        if (!fileHashEntity.getServerUrl().equals(constants.getLocalUrl())) {
            try {
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHashEntity.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileDto.getHashId());
                url = StringUtils.analysisParam(url, signBean);
                file = new File(new URL(url).getFile());
            } catch (IOException e) {
                e.printStackTrace();
                throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
            }
        } else {
            file = downloadService.getDownloadFile(fileHashEntity.getCode());
        }
        try (InputStream stream = Files.newInputStream(file.toPath());
             BufferedInputStream bis = new BufferedInputStream(stream)) {
            byte[] bytes = new byte[constants.getFileCache()];
            int count;
            while ((count = bis.read(bytes)) != -1) {
                zipStream.write(bytes, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
