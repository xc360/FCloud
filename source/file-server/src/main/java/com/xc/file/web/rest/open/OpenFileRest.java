package com.xc.file.web.rest.open;

import com.xc.api.file.bean.*;
import com.xc.api.file.dto.FileDto;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.enums.EffectStatus;
import com.xc.file.bean.FileBean;
import com.xc.file.config.Constants;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.service.DiskService;
import com.xc.file.service.FileHashService;
import com.xc.file.service.FileService;
import com.xc.file.service.FolderService;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>【开放】文件管理</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【开放】文件管理")
@RestController
public class OpenFileRest {
    @Autowired
    private FileService fileService;
    @Autowired
    private Constants constants;
    @Autowired
    private FolderService folderService;
    @Autowired
    private DiskService diskService;
    @Autowired
    private FileHashService fileHashService;

    @ApiOperation(value = "删除文件", notes = "开放接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件路径", name = "path", paramType = "query", required = true),
    })
    @DeleteMapping(value = "/open/disk/file")
    public void deleteOpenDiskFile(@ModelAttribute DiskSignBean diskSignBean, @RequestParam String filePath) {
        diskSignBean.setAuthorityCode(FileRestCode.deleteOpenDiskFile.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        FileEntity fileEntity = fileService.getFileByPath(diskEntity.getId(), filePath, null);
        if (fileEntity != null) {
            if (!fileService.removeById(fileEntity.getId())) {
                throw FailCode.FILE_DELETE_FAIL.getOperateException();
            }
        }
    }

    @ApiOperation(value = "获取文件", notes = "开放接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件路径", name = "path", paramType = "query", required = true),
    })
    @GetMapping(value = "/open/disk/file")
    public FileDto getOpenDiskFile(@ModelAttribute DiskSignBean diskSignBean, @RequestParam String filePath, @RequestParam String status) {
        diskSignBean.setAuthorityCode(FileRestCode.getOpenDiskFile.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        FileEntity fileEntity = fileService.getFileByPath(diskEntity.getId(), filePath, status);
        if (fileEntity == null) {
            return null;
        }
        FileDto fileDto = ObjectUtils.convert(new FileDto(), fileEntity);
        FileHashEntity fileHashEntity = fileHashService.getById(fileDto.getHashId());
        if (fileHashEntity != null) {
            fileDto.setCode(fileHashEntity.getCode());
            String folderPath = folderService.getPathByFolderId(fileDto.getFolderId());
            fileDto.setUrl(constants.getLocalUrl() + constants.getOpenDiskUrl() + diskEntity.getDiskNo() + folderPath + "/" + fileDto.getName());
            fileDto.setServerUrl(fileHashEntity.getServerUrl());
        }
        return fileDto;
    }

    @ApiOperation(value = "修改文件状态为有效", notes = "开放接口")
    @PutMapping("/open/disk/file_status")
    public List<FileDto> updateOpenDiskFileStatus(@ModelAttribute DiskSignBean diskSignBean, @RequestBody OpenFileBean openFileBean) {
        diskSignBean.setAuthorityCode(FileRestCode.updateOpenDiskFileStatus.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        List<FileEntity> fileEntities = fileService.getFileListByPath(diskEntity, openFileBean.getFilePaths());
        String status = openFileBean.getStatus();
        for (FileEntity fileEntity : fileEntities) {
            if (EffectStatus.VALID.getStatus().equals(status)) {
                fileEntity.setStatus(EffectStatus.VALID.getStatus());
            } else {
                fileEntity.setStatus(EffectStatus.INVALID.getStatus());
            }
            if (!fileService.updateById(fileEntity)) {
                throw FailCode.UPDATE_FILE_FAIL.getOperateException();
            }
        }
        // 查询hashCode
        List<FileDto> fileList = ObjectUtils.convertList(fileEntities, FileDto::new);
        List<String> hashIds = fileEntities.stream().map(FileEntity::getHashId).collect(Collectors.toList());
        if (fileEntities.size() > 0) {
            List<FileHashEntity> fileHashList = new ArrayList<>(fileHashService.listByIds(hashIds));
            for (FileDto fileDto : fileList) {
                for (FileHashEntity fileHashEntity : fileHashList) {
                    if (fileDto.getHashId().equals(fileHashEntity.getId())) {
                        fileDto.setCode(fileHashEntity.getCode());
                        String folderPath = folderService.getPathByFolderId(fileDto.getFolderId());
                        fileDto.setUrl(constants.getLocalUrl() + constants.getOpenDiskUrl() + diskEntity.getDiskNo() + folderPath + "/" + fileDto.getName());
                        fileDto.setServerUrl(fileHashEntity.getServerUrl());
                    }
                }
            }
        }
        return fileList;
    }

    @ApiOperation(value = "复制文件", notes = "开放接口")
    @PostMapping("/open/disk/file_copy")
    public FileDto createOpenDiskFileCopy(@ModelAttribute DiskSignBean diskSignBean, @RequestBody CopyFileBean copyFileBean) {
        diskSignBean.setAuthorityCode(FileRestCode.createOpenDiskFileCopy.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        String diskId = diskEntity.getId();
        FileEntity fileEntity = fileService.getFileByPath(diskEntity.getId(), copyFileBean.getFilePath(), EffectStatus.VALID.getStatus());
        if (fileEntity == null) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        // 创建新文件
        String newPath = copyFileBean.getNewFilePath();
        String newFolderPath = "";
        String newFileName = newPath;
        if (newPath.contains("/")) {
            newFolderPath = newPath.substring(0, newPath.lastIndexOf("/"));
            newFileName = newPath.substring(newPath.lastIndexOf("/") + 1);
        }
        FolderEntity newFolderEntity = folderService.createParentFolders(constants.getRoot(), diskId, newFolderPath);
        FileBean fileBean = ObjectUtils.convert(new FileBean(), fileEntity);
        if (EffectStatus.INVALID.getStatus().equals(copyFileBean.getStatus())) {
            fileBean.setStatus(EffectStatus.INVALID.getStatus());
        } else {
            fileBean.setStatus(EffectStatus.VALID.getStatus());
        }
        fileBean.setName(newFileName);
        fileBean.setFolderId(newFolderEntity.getId());
        // 创建文件，返回数据
        FileEntity newFileEntity = fileService.createFile(diskId, fileBean, true);
        FileDto fileDto = ObjectUtils.convert(new FileDto(), newFileEntity);
        // 获取下载地址
        FileHashEntity fileHash = fileHashService.getById(fileDto.getHashId());
        String url = fileHash.getServerUrl() + constants.getOpenDiskUrl() + diskEntity.getDiskNo() + newPath;
        fileDto.setUrl(url);
        fileDto.setCode(fileHash.getCode());
        fileDto.setServerUrl(fileHash.getServerUrl());
        return fileDto;
    }

    @ApiOperation(value = "图片处理", notes = "开放接口")
    @PostMapping("/open/disk/image_handle")
    public FileDto createOpenDiskImageHandle(@ModelAttribute DiskSignBean diskSignBean, @RequestBody ImageHandleBean imageHandleBean) {
        diskSignBean.setAuthorityCode(FileRestCode.createOpenDiskImageHandle.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        return fileService.imageHandle(diskSignBean, diskEntity, imageHandleBean, "/open/disk/image_handle");
    }

    @ApiOperation(value = "视频处理", notes = "开放接口")
    @PostMapping("/open/disk/video_handle")
    public FileDto createOpenDiskVideoHandle(@ModelAttribute DiskSignBean diskSignBean, @RequestBody VideoHandleBean videoHandleBean) {
        diskSignBean.setAuthorityCode(FileRestCode.createOpenDiskVideoHandle.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        return fileService.videoHandle(diskSignBean, diskEntity, videoHandleBean, "/open/disk/video_handle");
    }

    @ApiOperation(value = "音频处理", notes = "开放接口")
    @PostMapping("/open/disk/audio_handle")
    public FileDto createOpenDiskAudioHandle(@ModelAttribute DiskSignBean diskSignBean, @RequestBody AudioHandleBean audioHandleBean) {
        diskSignBean.setAuthorityCode(FileRestCode.createOpenDiskAudioHandle.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        return fileService.audioHandle(diskSignBean, diskEntity, audioHandleBean, "/open/disk/audio_handle");
    }
}
