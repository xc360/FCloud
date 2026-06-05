package com.xc.file.web.rest;

import com.xc.api.file.dto.FileDto;
import com.xc.core.annotation.Authority;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.FolderFileBean;
import com.xc.file.bean.PackFileBean;
import com.xc.file.bean.QueryFolderBean;
import com.xc.file.bean.QueryShareFolderBean;
import com.xc.file.config.Constants;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.service.DiskService;
import com.xc.file.service.FileService;
import com.xc.file.service.FolderService;
import com.xc.file.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>【用户】文件夹及文件rest</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【用户】文件夹及文件rest")
@RestController
public class FolderFileRest {
    @Autowired
    private FolderService folderService;
    @Autowired
    private ShareService shareService;
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "zip打包文件夹及文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "目录id", name = "folderId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/folder_file/pack")
    @Authority
    public FileDto createDiskFolderFilePack(TokenModel tokenModel, @PathVariable String diskId, @RequestBody PackFileBean packFileBean) {
        DiskEntity diskEntity = diskService.verifyUserDisk(tokenModel, diskId);
        return folderService.createFolderFilePack(diskEntity, packFileBean, tokenModel.getAccessToken(), "/disk/" + diskId + "/folder_file/pack");
    }

    @ApiOperation(value = "获取共享的文件夹及文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "共享id", name = "shareId", paramType = "path", required = true),
            @ApiImplicitParam(value = "文件夹路径", name = "path", paramType = "query"),
            @ApiImplicitParam(value = "文件夹或文件名称", name = "name", paramType = "query"),
    })
    @GetMapping("/share_folder_file_page/{current}")
    public PagingDto<FolderFileDto> getShareFolderFilePage(@PathVariable Integer current, @ModelAttribute PagingBean pagingBean,
                                                           @ModelAttribute QueryShareFolderBean queryShareFolderBean) {
        return shareService.getShareFolderFile(current, pagingBean, queryShareFolderBean);
    }


    @ApiOperation(value = "获取当前磁盘的文件夹及文件", notes = "获取当前磁盘的文件夹及文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件夹id", name = "folderId", paramType = "query"),
            @ApiImplicitParam(value = "名称", name = "name", paramType = "query"),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping("/disk/{diskId}/folder_file_page/{current}")
    @Authority
    public PagingDto<FolderFileDto> getDiskFolderFilePage(TokenModel tokenModel, @PathVariable String diskId, @PathVariable Integer current,
                                                     @ModelAttribute PagingBean pagingBean, @ModelAttribute QueryFolderBean queryFolderBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return folderService.getFolderFilePage(diskId, current, pagingBean, queryFolderBean);
    }


    @ApiOperation(value = "复制文件夹及文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "新目录id", name = "folderId", paramType = "query", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/folder_file")
    @Authority
    public void createDiskFolderFile(TokenModel tokenModel, @PathVariable String diskId, @RequestBody FolderFileBean folderFileBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        folderService.batchCopyFolderFile(diskId, folderFileBean);
    }

    @ApiOperation(value = "保存共享文件夹及文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "新目录id", name = "folderId", paramType = "query", required = true),
            @ApiImplicitParam(value = "共享id", name = "shareId", paramType = "query"),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/share_folder_file")
    @Authority
    public void createDiskShareFolderFile(TokenModel tokenModel, @PathVariable String diskId, @RequestParam String shareId, @RequestBody FolderFileBean folderFileBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        folderService.batchSaveShareFolderFile(diskId, shareId, folderFileBean);
    }


    @ApiOperation(value = "移动文件夹及文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "目录id", name = "folderId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PutMapping("/disk/{diskId}/folder_file")
    @Authority
    public void updateDiskFolderFile(TokenModel tokenModel, @PathVariable String diskId, @RequestBody FolderFileBean folderFileBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        folderService.batchMoveFolderFile(diskId, folderFileBean);
    }
}
