package com.xc.file.web.rest;


import com.xc.core.annotation.Authority;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.FolderBean;
import com.xc.file.bean.QueryFolderBean;
import com.xc.file.bean.UpdateFolderBean;
import com.xc.file.config.Constants;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderSizeDto;
import com.xc.file.entity.FolderEntity;
import com.xc.file.service.DiskService;
import com.xc.file.service.FolderService;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>【用户】文件夹信息</p>
 *
 * @author xc
 * @version v1.0
 */
@Api(tags = "【用户】文件夹信息")
@RestController
public class FolderRest {

    @Autowired
    private FolderService folderService;
    @Autowired
    private Constants constants;
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "创建文件夹", notes = "创建当前磁盘的文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/folder")
    @Authority
    public FolderDto createDiskFolder(TokenModel tokenModel, @PathVariable String diskId, @RequestParam(required = false) String folderId, @RequestBody FolderBean folderBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        FolderEntity folderEntity = folderService.createFolder(diskId, folderId, folderBean);
        return ObjectUtils.convert(new FolderDto(), folderEntity);
    }

    @ApiOperation(value = "修改文件夹", notes = "修改当前磁盘的文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PutMapping("/disk/{diskId}/folder/{folderId}")
    @Authority
    public FolderDto updateDiskFolder(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String folderId, @RequestBody UpdateFolderBean updateFolderBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return folderService.updateFolder(diskId, folderId, updateFolderBean);
    }

    @ApiOperation(value = "删除文件夹", notes = "删除当前磁盘的文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @DeleteMapping("/disk/{diskId}/folder/{folderId}")
    @Authority
    public void deleteDiskFolder(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String folderId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        folderService.deleteFolder(diskId, folderId);
    }

    @ApiOperation(value = "查询文件夹集合", notes = "查询当前磁盘的文件夹集合")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping("/disk/{diskId}/folder_page/{current}")
    @Authority
    public PagingDto<FolderDto> getDiskFolderPage(TokenModel tokenModel, @PathVariable String diskId, @PathVariable Integer current,
                                                  @ModelAttribute PagingBean pagingBean, @ModelAttribute QueryFolderBean queryFolderBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        FolderEntity folderEntity = new FolderEntity();
        if (!queryFolderBean.getOpenQueryAll()) {
            if (queryFolderBean.getFolderId() == null || "".equals(queryFolderBean.getFolderId())) {
                folderEntity.setParentNode(constants.getRoot());
            } else {
                FolderEntity folder = folderService.getTargetFolder(diskId, queryFolderBean.getFolderId());
                folderEntity.setParentNode(folder.getNode());
            }
        }
        folderEntity.setDiskId(diskId);
        folderEntity.setName(queryFolderBean.getName());
        return folderService.getFolderPage(current, pagingBean, folderEntity);
    }

    @ApiOperation(value = "获取文件夹大小", notes = "获取当前磁盘文件夹大小")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "文件夹id", name = "folderId", paramType = "path", required = true),
    })
    @GetMapping("/disk/{diskId}/folder/{folderId}/size")
    @Authority
    public FolderSizeDto getDiskFolderSize(TokenModel tokenModel, @PathVariable String diskId, @PathVariable("folderId") String folderId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return new FolderSizeDto(folderService.getFolderSize(folderId, null));
    }

    @ApiOperation(value = "获取父级文件夹集合", notes = "获取父级文件夹集合")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "文件夹id", name = "folderId", paramType = "query", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping("/disk/{diskId}/parent_folder_list")
    @Authority
    public List<FolderDto> getDiskParentFolderList(TokenModel tokenModel, @PathVariable String diskId, @RequestParam String folderId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        List<FolderEntity> entities = folderService.getParentFolderList(folderId, null);
        return ObjectUtils.convertList(entities, FolderDto::new);
    }
}
