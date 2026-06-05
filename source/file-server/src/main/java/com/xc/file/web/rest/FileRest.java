package com.xc.file.web.rest;

import com.xc.api.file.dto.FileDto;
import com.xc.core.annotation.Authority;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.UpdateFileBean;
import com.xc.file.dto.CdnDto;
import com.xc.file.entity.FileEntity;
import com.xc.file.service.DiskService;
import com.xc.file.service.FileService;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>【用户】文件管理</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【用户】文件管理")
@RestController
public class FileRest {

    @Autowired
    private FileService fileService;
    @Autowired
    private DiskService diskService;


    @ApiOperation(value = "删除文件", notes = "删除当前磁盘的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件id", name = "fileId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @DeleteMapping(value = "/disk/{diskId}/file/{fileId}")
    @Authority
    public FileDto deleteDiskFile(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String fileId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        FileEntity fileEntity = fileService.deleteDiskFile(diskId, fileId);
        return ObjectUtils.convert(new FileDto(), fileEntity);
    }

    @ApiOperation(value = "修改文件", notes = "修改当前磁盘的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件id", name = "fileId", paramType = "path", required = true),
            @ApiImplicitParam(value = "文件名称", name = "name", paramType = "body", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PutMapping(value = "/disk/{diskId}/file/{fileId}")
    @Authority
    public FileDto updateDiskFile(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String fileId, @RequestBody UpdateFileBean updateFileBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return fileService.updateDiskFile(diskId, fileId, updateFileBean);
    }

    @ApiOperation(value = "生成cdn地址", notes = "创建当前磁盘的cdn地址")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping(value = "/disk/{diskId}/file/{fileId}/cdn_url")
    @Authority
    public CdnDto createDiskFileCdnUrl(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String fileId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return fileService.createDiskFileCdnUrl(diskId, fileId);
    }
}
