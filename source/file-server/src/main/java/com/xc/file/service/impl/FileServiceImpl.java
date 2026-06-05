package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.api.basic.BasicApi;
import com.xc.api.basic.bean.UserSignBean;
import com.xc.api.file.bean.AudioHandleBean;
import com.xc.api.file.bean.DiskSignBean;
import com.xc.api.file.bean.ImageHandleBean;
import com.xc.api.file.bean.VideoHandleBean;
import com.xc.api.file.dto.FileDto;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.enums.EffectStatus;
import com.xc.core.enums.Whether;
import com.xc.core.utils.RedisUtils;
import com.xc.file.bean.FileBean;
import com.xc.file.bean.UpdateFileBean;
import com.xc.file.config.Constants;
import com.xc.file.dto.CdnDto;
import com.xc.file.entity.*;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.enums.RedisTime;
import com.xc.file.mapper.FileMapper;
import com.xc.file.mapper.SafetyChainMapper;
import com.xc.file.model.DownloadModel;
import com.xc.file.service.*;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * <p>文件管理实现</p>
 *
 * @version v1.0
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private Constants constants;
    @Autowired
    private DiskService diskService;
    @Autowired
    private SafetyChainMapper safetyChainMapper;
    @Lazy
    @Autowired
    private ShareService shareService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FileHashService fileHashService;
    @Lazy
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BasicApi basicApi;
    @Autowired
    private BasicConstants basicConstants;
    /**
     * 线程池
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    @Transactional
    public FileEntity createFile(String diskId, FileBean fileBean, boolean isVerify) {
        if (isVerify) {
            //计算当前磁盘使用空间
            diskService.verifyDiskSpace(diskId, fileBean.getSize());
        }
        //查询文件信息,检测文件是否重复
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(fileBean.getName());
        fileEntity.setDiskId(diskId);
        fileEntity.setFolderId(fileBean.getFolderId());
        FileEntity entity = fileMapper.selectOne(new QueryWrapper<>(fileEntity));
        //判断文件是否创建，已创建则替换
        if (entity != null) {
            entity.setStatus(fileBean.getStatus());
            entity.setHashId(fileBean.getHashId());
            entity.setSize(fileBean.getSize());
            if (!this.updateById(entity)) {
                throw FailCode.REPLACE_FILE_FAIL.getOperateException();
            }
            return entity;
        }
        // 添加剩余参数保存
        fileEntity.setStatus(fileBean.getStatus());
        fileEntity.setHashId(fileBean.getHashId());
        fileEntity.setSize(fileBean.getSize());
        try {
            if (!this.save(fileEntity)) {
                throw FailCode.FILE_CREATE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.FILE_EXIST.getOperateException();
        }
        //返回结果
        return fileEntity;
    }

    @Override
    public FileDto updateDiskFile(String diskId, String fileId, UpdateFileBean updateFileBean) {
        FileEntity fileEntity = getById(fileId);
        // 验证是否是磁盘文件
        if (!fileEntity.getDiskId().equals(diskId)) {
            throw FailCode.FILE_NOT_DISK.getOperateException();
        }
        fileEntity.setName(updateFileBean.getName());
        try {
            if (!this.updateById(fileEntity)) {
                throw FailCode.UPDATE_FILE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.FILE_EXIST.getOperateException();
        }
        return ObjectUtils.convert(new FileDto(), fileEntity);
    }

    @Override
    @Transactional
    public FileEntity deleteDiskFile(String diskId, String fileId) {
        //获取文件信息
        FileEntity fileEntity = fileMapper.selectById(fileId);
        //验证文件是否存在
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        // 验证是否是磁盘文件
        if (!fileEntity.getDiskId().equals(diskId)) {
            throw FailCode.FILE_NOT_DISK.getOperateException();
        }
        //删除文件信息
        if (!this.removeById(fileId)) {
            throw FailCode.FILE_DELETE_FAIL.getOperateException();
        }
        return fileEntity;
    }

    @Override
    public FileEntity getOpenDownload(String diskId, String filePath) {
        // 去除文件后缀
        String folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String folderId = constants.getRoot();
        if (folderPath.contains("/")) {
            FolderEntity folderEntity = folderService.getFolderByPath(diskId, folderPath);
            if (folderEntity == null) {
                throw FailCode.FOLDER_NOT_EXIST.getOperateException();
            }
            folderId = folderEntity.getId();
        }
        FileEntity entity = new FileEntity();
        entity.setName(fileName);
        entity.setFolderId(folderId);
        entity.setDiskId(diskId);
        FileEntity fileEntity = fileMapper.selectOne(new QueryWrapper<>(entity));
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        return fileEntity;
    }

    @Override
    public DownloadModel verifySafetyChain(String referer, UserSignBean userSignBean, String diskNo, String filePath) {
        String key = RedisPrefix.OPEN_DOWNLOAD.getKey() + diskNo + filePath;
        DownloadModel downloadModel = RedisUtils.get(key);
        if (downloadModel != null) {
            return downloadModel;
        }
        // 获取磁盘信息
        DiskEntity diskEntity = diskService.getDiskByDiskNo(diskNo);
        if (diskEntity == null) {
            throw FailCode.DISK_NO_ERROR.getOperateException();
        }
        // 验证文件
        FileEntity fileEntity = this.getOpenDownload(diskEntity.getId(), filePath);
        // 查文件hash信息
        FileHashEntity fileHash = fileHashService.getById(fileEntity.getHashId());
        // 验证访问权限
        SafetyChainEntity safetyChainEntity = new SafetyChainEntity();
        safetyChainEntity.setDiskId(fileEntity.getDiskId());
        safetyChainEntity.setStatus(EffectStatus.VALID.getStatus());
        List<SafetyChainEntity> safetyChains = safetyChainMapper.selectList(new QueryWrapper<>(safetyChainEntity));
        String folderPath = folderService.getPathByFolderId(fileEntity.getFolderId());
        boolean bool2 = false;
        boolean bool3 = false;
        for (SafetyChainEntity safetyChain : safetyChains) {
            // 验证链接
            if (safetyChain.getUrl() != null && !"".equals(safetyChain.getUrl())) {
                boolean bool = StringUtils.compareUrl(folderPath, safetyChain.getPath() + "/**");
                boolean bool1 = StringUtils.compareUrl(referer, safetyChain.getUrl());
                boolean bool4 = true;
                if (safetyChain.getAllowSuffix() != null && !"".equals(safetyChain.getAllowSuffix())) {
                    String suffix = FileUtils.getFileSuffix(fileEntity.getName());
                    List<String> suffixList = Arrays.stream(safetyChain.getAllowSuffix().split(",")).map(String::toLowerCase).collect(Collectors.toList());
                    bool4 = suffixList.contains(suffix.toLowerCase());
                }
                if (bool && bool1 && bool4) {
                    bool2 = true;
                }
            }
            // 验证签名
            if (safetyChain.getVerifySign() != null && !"".equals(safetyChain.getVerifySign())) {
                boolean bool = StringUtils.compareUrl(folderPath, safetyChain.getPath() + "/**");
                if (bool && Whether.YES.getValue().equals(safetyChain.getVerifySign())) {
                    bool3 = true;
                }
            }
        }
        if (bool2) {
            if (bool3) {
                try {
                    userSignBean.setMyAppId(basicConstants.getAppId());
                    userSignBean.setAuthorityCode(FileRestCode.downloadFile.getCode());
                    basicApi.verifyUserSign(userSignBean);
                    downloadModel = ObjectUtils.convert(new DownloadModel(), fileEntity);
                    downloadModel.setIsCompute(true);
                    downloadModel.setFreeFlow(diskEntity.getFreeFlow());
                    downloadModel.setCode(fileHash.getCode());
                    downloadModel.setServerUrl(fileHash.getServerUrl());
                    return downloadModel;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                downloadModel = ObjectUtils.convert(new DownloadModel(), fileEntity);
                downloadModel.setIsCompute(true);
                downloadModel.setFreeFlow(diskEntity.getFreeFlow());
                downloadModel.setCode(fileHash.getCode());
                downloadModel.setServerUrl(fileHash.getServerUrl());
                // 设置为有效允许下载
                RedisUtils.set(key, downloadModel, RedisTime.OPEN_DOWNLOAD_CACHE.getTime());
                return downloadModel;
            }
        }
        throw FailCode.NOT_VISIT_AUTHORITY.getOperateException();
    }

    @Override
    public DownloadModel getDownloadModel(String downloadCode, String fid, String downloadType) {
        DownloadModel downloadModel = RedisUtils.get(downloadType + downloadCode);
        if (downloadModel == null || downloadModel.getFiles() == null || downloadModel.getFiles().size() <= 0) {
            throw FailCode.DOWNLOAD_CODE_ERROR.getOperateException();
        }
        // 查看共享文件是否存在
        for (FileDto fileDto : downloadModel.getFiles()) {
            if (fileDto.getId().equals(fid)) {
                return ObjectUtils.convert(downloadModel, fileDto);
            }
        }
        throw FailCode.DOWNLOAD_CODE_ERROR.getOperateException();
    }

    @Override
    public void downloadFinish(DownloadModel downloadModel) {
        if (downloadModel.getIsCompute()) {
            // 验证流量是否充足
            if (downloadModel.getFreeFlow() < downloadModel.getSize()) {
                throw FailCode.FREE_FLOW_SHORTAGE.getOperateException();
            }
            executorService.execute(() -> {
                diskService.computeFreeFlow(downloadModel.getDiskId(), downloadModel.getSize(), false);
            });
        }
        if (downloadModel.getShareId() != null && downloadModel.getOpen() == 1) {
            executorService.execute(() -> {
                ShareEntity shareEntity = shareService.getById(downloadModel.getShareId());
                shareEntity.setDownloadNum(shareEntity.getDownloadNum() + 1);
                if (!shareService.updateById(shareEntity)) {
                    throw FailCode.SHARE_FILE_UPDATE_FAIL.getOperateException();
                }
            });
        }
    }

    @Override
    public List<FileDto> setDownloadUrl(List<FileDto> files, String downloadPath) {
        if (files.size() == 0) {
            return new ArrayList<>();
        }
        List<String> hashIds = new ArrayList<>();
        for (FileDto fileDto : files) {
            hashIds.add(fileDto.getHashId());
        }
        //  获取下载地址
        List<FileHashEntity> fileHashList = new ArrayList<>(fileHashService.listByIds(hashIds));
        List<FileDto> fileDtoList = new ArrayList<>();
        for (FileDto fileDto : files) {
            boolean bool = true;
            for (FileHashEntity fileHashEntity : fileHashList) {
                if (fileDto.getHashId().equals(fileHashEntity.getId())) {
                    bool = false;
                    //获取后缀
                    String fileName = fileDto.getName();
                    String suffix = "";
                    if (fileName.contains(".")) {
                        int j = fileName.lastIndexOf(".");
                        suffix = fileName.substring(j);
                    }
                    fileDto.setUrl(fileHashEntity.getServerUrl() + downloadPath + fileDto.getId() + suffix);
                    fileDto.setCode(fileHashEntity.getCode());
                    fileDto.setServerUrl(fileHashEntity.getServerUrl());
                    fileDtoList.add(fileDto);
                }
            }
            if (bool) {
                fileDtoList.add(fileDto);
            }
        }
        return fileDtoList;
    }

    @Override
    public CdnDto createDiskFileCdnUrl(String diskId, String fileId) {
        FileEntity fileEntity = fileMapper.selectById(fileId);
        // 验证是否是磁盘文件
        if (!fileEntity.getDiskId().equals(diskId)) {
            throw FailCode.FILE_NOT_DISK.getOperateException();
        }
        // 查询文件hash信息
        FileHashEntity hashEntity = fileHashService.getById(fileEntity.getHashId());
        // 生成下载地址
        String folderPath = folderService.getPathByFolderId(fileEntity.getFolderId());
        String filePath = folderPath + "/" + fileEntity.getName();
        // 获取磁盘信息
        DiskEntity diskEntity = diskService.getById(diskId);
        if (diskEntity == null) {
            throw FailCode.DISK_NO_ERROR.getOperateException();
        }
        String url = hashEntity.getServerUrl() + constants.getCdnDiskUrl() + diskEntity.getDiskNo() + filePath;
        CdnDto cdnDto = new CdnDto();
        cdnDto.setCdnUrl(url);
        return cdnDto;
    }

    @Override
    public List<FileEntity> getFilesByFolderIds(String diskId, List<String> folderIds) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setDiskId(diskId);
        fileEntity.setStatus(EffectStatus.VALID.getStatus());
        QueryWrapper<FileEntity> fileWrapper = new QueryWrapper<>(fileEntity);
        fileWrapper.lambda().in(FileEntity::getFolderId, folderIds);
        return fileMapper.selectList(fileWrapper);
    }

    @Override
    public List<FileEntity> batchCopyFile(String diskId, String folderId, List<FileEntity> files) {
        for (FileEntity entity : files) {
            entity.setId(null);
            entity.setUpdateTime(null);
            entity.setCreateTime(null);
            entity.setVersion(0);
            entity.setDiskId(diskId);
            entity.setFolderId(folderId);
            if (!this.save(entity)) {
                throw FailCode.COPY_FILE_FAIL.getOperateException();
            }
        }
        return files;
    }

    @Override
    public FileEntity getFileByPath(String diskId, String filePath, String status) {
        if (filePath == null) {
            return null;
        }
        String folderPath = "";
        String fileName = filePath;
        if (filePath.contains("/")) {
            folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        FolderEntity folderEntity = folderService.getFolderByPath(diskId, folderPath);
        if (folderEntity != null) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(fileName);
            fileEntity.setFolderId(folderEntity.getId());
            fileEntity.setDiskId(diskId);
            fileEntity.setStatus(status);
            return this.getOne(new QueryWrapper<>(fileEntity));
        }
        return null;
    }

    @Override
    public List<FileEntity> getFileListByPath(DiskEntity diskEntity, List<String> filePaths) {
        String diskId = diskEntity.getId();
        // 获取文件信息
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        boolean bool = true;
        for (String filePath : filePaths) {
            if (filePath != null && !"".equals(filePath)) {
                if (addPath(queryWrapper, filePath, diskId)) {
                    bool = false;
                }
            }
        }
        if (!bool) {
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean addPath(QueryWrapper<FileEntity> queryWrapper, String path, String diskId) {
        String folderPath = "";
        String fileName = path;
        if (path.contains("/")) {
            folderPath = path.substring(0, path.lastIndexOf("/"));
            fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        FolderEntity folderEntity = folderService.getFolderByPath(diskId, folderPath);
        if (folderEntity != null) {
            String finalFileName = fileName;
            queryWrapper.lambda().nested(wrapper -> wrapper
                    .eq(FileEntity::getDiskId, diskId)
                    .eq(FileEntity::getFolderId, folderEntity.getId())
                    .eq(FileEntity::getName, finalFileName));
            queryWrapper.or();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public FileDto imageHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, ImageHandleBean imageHandleBean, String forwardUrl) {
        String diskId = diskEntity.getId();
        FileEntity fileEntity = this.getFileByPath(diskId, imageHandleBean.getFilePath(), null);
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        // 处理图片
        File file = fileHashService.imageHandle(fileEntity, imageHandleBean);
        //计算当前磁盘使用空间
        diskService.verifyDiskSpace(diskEntity, file.length());
        // 检测本服务器空间是否支持上传，不支持调用其他服务器接口长传
        String path = uploadService.getFilePathBySize(file.length());
        if (path == null) {
            // 空间不足
            String serviceIp = fileCacheService.getServiceIp(fileEntity.getSize());
            forwardUrl = serviceIp + forwardUrl;
            forwardUrl = StringUtils.analysisParam(forwardUrl, diskSignBean);
            HttpEntity<ImageHandleBean> requestEntity = new HttpEntity<>(imageHandleBean);
            ResponseEntity<FileDto> responseEntity = restTemplate.exchange(forwardUrl, HttpMethod.POST, requestEntity, FileDto.class);
            return responseEntity.getBody();
        }
        // 创建新文件夹
        String newPath = imageHandleBean.getNewFilePath();
        FolderEntity newFolderEntity = folderService.createParentFolders(constants.getRoot(), diskId, FileUtils.getFilePath(newPath));
        FileDto fileDto = fileHashService.createFileHash(diskEntity, file, path, newFolderEntity.getId(), FileUtils.getFileName(newPath), fileEntity.getStatus());
        fileDto.setUrl(fileDto.getUrl() + newPath);
        return fileDto;
    }

    @Override
    public FileDto videoHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, VideoHandleBean videoHandleBean, String forwardUrl) {
        String diskId = diskEntity.getId();
        FileEntity fileEntity = this.getFileByPath(diskId, videoHandleBean.getFilePath(), null);
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        File tempFile;
        long total;
        // 处理视频
        if (videoHandleBean.getVideoM3u8() != null) {
            tempFile = fileHashService.videoToM3u8(fileEntity, videoHandleBean);
            total = Arrays.stream(Objects.requireNonNull(tempFile.getParentFile().listFiles())).mapToLong(File::length).sum();
        } else if (videoHandleBean.getImageWatermark() != null || videoHandleBean.getImageCompress() != null || videoHandleBean.getImageCrop() != null) {
            tempFile = fileHashService.videoWatermark(fileEntity, videoHandleBean);
            total = tempFile.length();
        } else {
            throw FailCode.PARAM_ERROR.getOperateException();
        }
        //计算当前磁盘使用空间
        diskService.verifyDiskSpace(diskEntity, total);
        // 检测本服务器空间是否支持上传，不支持调用其他服务器接口长传
        String path = uploadService.getFilePathBySize(total);
        if (path == null) {
            // 空间不足
            String serviceIp = fileCacheService.getServiceIp(fileEntity.getSize());
            forwardUrl = serviceIp + forwardUrl;
            forwardUrl = StringUtils.analysisParam(forwardUrl, diskSignBean);
            HttpEntity<VideoHandleBean> requestEntity = new HttpEntity<>(videoHandleBean);
            ResponseEntity<FileDto> responseEntity = restTemplate.exchange(forwardUrl, HttpMethod.POST, requestEntity, FileDto.class);
            return responseEntity.getBody();
        }
        // 创建新文件夹
        String newPath = videoHandleBean.getNewFilePath();
        FolderEntity newFolderEntity = folderService.createParentFolders(constants.getRoot(), diskId, FileUtils.getFilePath(newPath));
        // 创建新文件
        if (videoHandleBean.getVideoM3u8() != null) {
            FileDto fileDto = fileHashService.createFileHash(diskEntity, tempFile, path, newFolderEntity.getId(), FileUtils.getFileName(newPath));
            for (File file : Objects.requireNonNull(tempFile.getParentFile().listFiles())) {
                fileHashService.createFileHash(diskEntity, file, path, newFolderEntity.getId(), FileUtils.getFileName(file.getPath().replace(File.separator, "/")));
            }
            fileDto.setUrl(fileDto.getUrl() + newPath);
            return fileDto;
        } else if (videoHandleBean.getImageWatermark() != null || videoHandleBean.getImageCompress() != null || videoHandleBean.getImageCrop() != null) {
            FileDto fileDto = fileHashService.createFileHash(diskEntity, tempFile, path, newFolderEntity.getId(), FileUtils.getFileName(newPath), fileEntity.getStatus());
            fileDto.setUrl(fileDto.getUrl() + newPath);
            return fileDto;
        } else {
            throw FailCode.PARAM_ERROR.getOperateException();
        }
    }

    @Override
    public FileDto audioHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, AudioHandleBean audioHandleBean, String forwardUrl) {
        String diskId = diskEntity.getId();
        FileEntity fileEntity = this.getFileByPath(diskId, audioHandleBean.getFilePath(), null);
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        File tempFile;
        long total;
        // 处理视频
        if (audioHandleBean.getAudioConvert() != null) {
            tempFile = fileHashService.audioConvert(fileEntity, audioHandleBean);
            total = tempFile.length();
        } else {
            throw FailCode.PARAM_ERROR.getOperateException();
        }
        //计算当前磁盘使用空间
        diskService.verifyDiskSpace(diskEntity, total);
        // 检测本服务器空间是否支持上传，不支持调用其他服务器接口长传
        String path = uploadService.getFilePathBySize(total);
        if (path == null) {
            // 空间不足
            String serviceIp = fileCacheService.getServiceIp(fileEntity.getSize());
            forwardUrl = serviceIp + forwardUrl;
            forwardUrl = StringUtils.analysisParam(forwardUrl, diskSignBean);
            HttpEntity<AudioHandleBean> requestEntity = new HttpEntity<>(audioHandleBean);
            ResponseEntity<FileDto> responseEntity = restTemplate.exchange(forwardUrl, HttpMethod.POST, requestEntity, FileDto.class);
            return responseEntity.getBody();
        }
        // 创建新文件夹
        String newPath = audioHandleBean.getNewFilePath();
        FolderEntity newFolderEntity = folderService.createParentFolders(constants.getRoot(), diskId, FileUtils.getFilePath(newPath));
        // 创建新文件
        if (audioHandleBean.getAudioConvert() != null) {
            FileDto fileDto = fileHashService.createFileHash(diskEntity, tempFile, path, newFolderEntity.getId(), FileUtils.getFileName(newPath), fileEntity.getStatus());
            fileDto.setUrl(fileDto.getUrl() + newPath);
            return fileDto;
        } else {
            throw FailCode.PARAM_ERROR.getOperateException();
        }
    }
}
