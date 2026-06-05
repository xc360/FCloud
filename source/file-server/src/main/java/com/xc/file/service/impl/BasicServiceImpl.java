package com.xc.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xc.api.basic.BasicApi;
import com.xc.api.basic.dto.DeletedUserDto;
import com.xc.api.basic.enums.BasicRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.core.enums.EffectStatus;
import com.xc.core.utils.RedisUtils;
import com.xc.file.config.Constants;
import com.xc.file.config.FolderConfig;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.enums.FileSuffix;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.model.FileModel;
import com.xc.file.model.HashModel;
import com.xc.file.model.ServerModel;
import com.xc.file.model.ServerSpaceModel;
import com.xc.file.service.*;
import com.xc.tool.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 基础ServiceImpl
 * </p>
 *
 * @author xc
 * @since 2026-05-14
 */
@Slf4j
@Service
public class BasicServiceImpl implements BasicService {
    @Autowired
    private Constants constants;
    @Autowired
    private FileHashService fileHashService;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;
    @Autowired
    private DiskService diskService;

    @Override
    public void logoutHandle() {
        // 注销用户主键集合
        SignBean signBean1 = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.getOpenDeletedUserList.getCode());
        List<DeletedUserDto> deletedUserList = basicApi.getOpenDeletedUserList(signBean1);
        for (DeletedUserDto deletedUserDto : deletedUserList) {
            long time = System.currentTimeMillis() - constants.getLogOffGuardTime();
            if (deletedUserDto.getDeleteTime().getTime() < time) {
                diskService.deleteDiskByUserId(deletedUserDto.getUserId());
            }
        }
    }

    @Override
    public void dataHandle() {
        // 处理其他数据
        constants.setFileInfoTable(new Hashtable<>());
        deleteTempFile(); //  清理零时文件
        updateServerInfo(); // 加载服务信息
        deleteInvalidFile(); // 删除失效文件
        initVerifyFileHash(); // 验证文件哈希和文件是否正确
        initUpdateCreateFileHash(); // 检测文件哈希和真实文件是否正确
        log.info("定时清除数据完成！");
    }

    /**
     * <p>初始化删除文件</p>
     */
    private void deleteTempFile() {
        try {
            // 清理临时文件
            for (FolderConfig folderConfig : constants.getFolderPaths()) {
                List<File> fileList = FileUtil.loopFiles(folderConfig.getPath());
                for (File file : fileList) {
                    if (file.getPath().contains(FileSuffix.UPLOAD.getSuffix())) {
                        if (file.exists() && !file.delete()) {
                            log.error("初始化删除上传中的零时文件失败！");
                        }
                    }
                    if (file.getPath().contains(FileSuffix.PACK.getSuffix())) {
                        if (file.exists() && !file.delete()) {
                            log.error("初始化删除打包的零时文件失败！");
                        }
                    }
                }
            }
            // 清理临时目录
            if (!FileUtils.deleteFolder(constants.getTempFolder().getPath())) {
                log.error("清理临时目录失败！");
            }
            log.info("清理临时信息完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>更新FileMode信息</p>
     */
    private void updateServerInfo() {
        //计算可用空间
        List<ServerSpaceModel> serverSpaceModels = new ArrayList<>();
        for (FolderConfig folderConfig : constants.getFolderPaths()) {
            serverSpaceModels.add(getAvailableSpace(folderConfig));
        }
        //发送通知请求
        ServerModel serverModel = new ServerModel();
        serverModel.setLocalUrl(constants.getLocalUrl());
        serverModel.setServerSpaceModels(serverSpaceModels);
        //装载本地文件
        fileCacheService.updateServerInfo(serverModel);
        // 清除下载缓存
        String key = RedisPrefix.OPEN_DOWNLOAD.getKey() + "*";
        Set<String> keys = RedisUtils.getKes(key);
        RedisUtils.delete(keys.toArray(new String[0]));
    }

    /**
     * <p>获取目录可用空间</p>
     *
     * @param folderConfig 目录信息
     * @return 返回当前目录的可用空间
     */
    private ServerSpaceModel getAvailableSpace(FolderConfig folderConfig) {
        long size = 0;
        //计算缓存为上传完成文件大小
        Map<String, FileModel> fileInfoMap = constants.getFileInfoTable();
        for (String key : fileInfoMap.keySet()) {
            if (folderConfig.getPath().equals(fileInfoMap.get(key).getFilePath())) {
                size += fileInfoMap.get(key).getSize();
            }
        }
        File file = new File(folderConfig.getPath());
        //创建目录
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.error("初始化创建目录失败！");
            }
        }
        ServerSpaceModel serverSpaceModel = new ServerSpaceModel();
        serverSpaceModel.setPath(folderConfig.getPath());
        long availableSpace;
        if (size == 0) {
            availableSpace = file.getFreeSpace() - folderConfig.getReserveSpace();
        } else {
            availableSpace = file.getFreeSpace() - folderConfig.getReserveSpace() - size;
        }
        serverSpaceModel.setAvailableSpace(availableSpace);
        return serverSpaceModel;
    }

    /**
     * <p>删除失效文件</p>
     */
    private void deleteInvalidFile() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setStatus(EffectStatus.INVALID.getStatus());
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>(fileEntity);
        queryWrapper.lambda().le(FileEntity::getCreateTime, new Date(new Date().getTime() - constants.getFileCleaningTime()));
        List<FileEntity> fileList = fileService.list(queryWrapper);
        List<String> fileIds = fileList.stream().map(FileEntity::getId).collect(Collectors.toList());
        if (fileIds.size() > 0) {
            fileService.remove(queryWrapper);
        }
        log.info("【提示】已删除失效的文件信息:{}", fileIds);
    }

    /**
     * 获取本地文件的hash信息
     *
     * @return 文件哈希信息
     */
    private List<HashModel> getHashModel() {
        List<HashModel> hashModels = new ArrayList<>();
        for (FolderConfig folderConfig : constants.getFolderPaths()) {
            List<File> fileList = FileUtil.loopFiles(folderConfig.getPath());
            for (File file : fileList) {
                if (file.getName().contains(FileSuffix.SUCCESS.getSuffix())) {
                    String code = file.getName().substring(0, file.getName().indexOf("."));
                    hashModels.add(new HashModel(code, file.length()));
                }
            }
        }
        return hashModels;
    }

    /**
     * <p>检测哈希表里的文件是否在本地存在</p>
     * <p>检测本地文件是否在哈希表里存在</p>
     * <p>检测哈希表的文件信息，与实际信息是否一直不一致修改为一致</p>
     */
    private void initUpdateCreateFileHash() {
        //获取本地文件的hash信息
        List<HashModel> hashModels = getHashModel();
        final List<String> tableCodes = new ArrayList<>();
        final List<String> modelCodes = new ArrayList<>();
        // 所有本机的hash信息
        QueryWrapper<FileHashEntity> queryWrapper = new QueryWrapper<>(new FileHashEntity());
        queryWrapper.lambda().eq(FileHashEntity::getServerUrl, constants.getLocalOldUrl());
        List<FileHashEntity> entities = fileHashService.list(queryWrapper);
        if (entities.size() == 0) {
            QueryWrapper<FileHashEntity> wrapper = new QueryWrapper<>(new FileHashEntity());
            wrapper.lambda().eq(FileHashEntity::getServerUrl, constants.getLocalUrl());
            entities = fileHashService.list(wrapper);
        }
        // 检测hash表的数据
        for (FileHashEntity fileHash : entities) {
            boolean bool = true;
            for (HashModel hashModel : hashModels) {
                if (hashModel.getCode().equals(fileHash.getCode())) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                tableCodes.add(fileHash.getCode());
            }
        }
        // 检测文件源数据
        List<FileHashEntity> fileHashEntities = new ArrayList<>();
        for (HashModel hashModel : hashModels) {
            boolean bool = true;
            for (FileHashEntity fileHash : entities) {
                if (hashModel.getCode().equals(fileHash.getCode())) {
                    bool = false;
                    if (!hashModel.getSize().equals(fileHash.getSize()) || !constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                        fileHash.setServerUrl(constants.getLocalUrl());
                        fileHash.setSize(hashModel.getSize());
                        fileHashEntities.add(fileHash);
                    }
                }
            }
            if (bool) { // 创建fileHash
                modelCodes.add(hashModel.getCode());
            }
        }
        if (fileHashEntities.size() > 0) {
            fileHashService.updateBatchById(fileHashEntities);
        }
        log.info("【提示】失效的哈希表数据：{}", tableCodes);
        log.info("【提示】失效的文件源数据：{}", modelCodes);
    }

    /**
     * <p>初始化验证文件数据信息</p>
     * <p>检测服务器的文件是否有丢失</p>
     * <p>检测文件的信息和hash的信息是否存在错误</p>
     * <p>检测文件是否有冗余</p>
     */
    private void initVerifyFileHash() {
        //查出数据库数据
        List<FileHashEntity> fileHashList = fileHashService.list();
        List<FileEntity> fileList = fileService.list();
        // 检测数据库文件是否存在
        List<String> errorIds = new ArrayList<>();
        for (FileEntity fileEntity : fileList) {
            boolean bool = true;
            for (FileHashEntity fileHashEntity : fileHashList) {
                if (fileHashEntity.getId().equals(fileEntity.getHashId())) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                errorIds.add(fileEntity.getHashId());
            }
        }
        log.info("【严重】服务器文件丢失，异常数据:{}", errorIds);

        // 检测文件源是否被使用
        List<String> errorHashIds = new ArrayList<>();
        List<String> sizeHashIds = new ArrayList<>();
        for (FileHashEntity fileHashEntity : fileHashList) {
            boolean bool = true;
            for (FileEntity fileEntity : fileList) {
                if (fileHashEntity.getId().equals(fileEntity.getHashId())) {
                    bool = false;
                }
                if (fileHashEntity.getId().equals(fileEntity.getHashId()) && !fileHashEntity.getSize().equals(fileEntity.getSize())) {
                    sizeHashIds.add(fileHashEntity.getId());
                }
            }
            if (bool) {
                if (fileHashEntity.getServerUrl().equals(constants.getLocalUrl())) {
                    errorHashIds.add(fileHashEntity.getId());
                    fileHashService.removeById(fileHashEntity.getId());
                    uploadService.deleteFile(fileHashEntity.getCode(), FileSuffix.SUCCESS.getSuffix());
                }
            }
        }
        log.info("【严重】文件的文件hash或size异常，异常数据:{}", sizeHashIds);
        log.info("【提示】已清理的未使用文件，数据:{}", errorHashIds);
    }
}
