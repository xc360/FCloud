package com.xc.file.web.rest.open;

import com.xc.api.file.bean.DiskSignBean;
import com.xc.api.file.enums.FileRestCode;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.service.DiskService;
import com.xc.file.service.FolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>【开放】文件夹管理</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【开放】文件夹管理")
@RestController
public class OpenFolderRest {
    @Autowired
    private FolderService folderService;
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "删除文件夹", notes = "开放接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件夹路径", name = "path", paramType = "query", required = true),
    })
    @DeleteMapping("/open/disk/folder")
    public void deleteOpenFolder(@ModelAttribute DiskSignBean diskSignBean, @RequestParam String path) {
        diskSignBean.setAuthorityCode(FileRestCode.deleteOpenFolder.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        FolderEntity folderEntity = folderService.getFolderByPath(diskEntity.getUserId(), path);
        if (folderEntity != null) {
            folderService.deleteFolder(diskEntity.getId(), folderEntity.getId());
        }
    }
}
