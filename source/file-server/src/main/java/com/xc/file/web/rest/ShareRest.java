package com.xc.file.web.rest;

import com.xc.core.annotation.Authority;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.ShareBean;
import com.xc.file.dto.FolderDto;
import com.xc.file.dto.FolderSizeDto;
import com.xc.file.dto.ShareCodeDto;
import com.xc.file.dto.ShareDto;
import com.xc.file.service.DiskService;
import com.xc.file.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>【用户】共享文件</p>
 *
 * @author xc
 * @version v1.0
 */
@Api(tags = "【用户】共享文件")
@RestController
public class ShareRest {

    @Autowired
    private ShareService shareService;
    @Autowired
    private DiskService diskService;


    @ApiOperation(value = "创建共享", notes = "创建当前磁盘的共享")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/share")
    @Authority
    public ShareDto createDiskShare(TokenModel tokenModel, @PathVariable String diskId, @RequestBody ShareBean shareBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return shareService.createShare(diskId, shareBean);
    }

    @ApiOperation(value = "共享分页", notes = "获取当前磁盘的共享页")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping("/disk/{diskId}/share_pages/{current}")
    @Authority
    public PagingDto<ShareDto> getDiskSharePage(TokenModel tokenModel, @PathVariable String diskId, @PathVariable Integer current,
                                                @ModelAttribute PagingBean pagingBean, @ModelAttribute ShareBean shareBean) {
        diskService.verifyUserDisk(tokenModel, diskId);
        return shareService.getSharePage(diskId, current, pagingBean, shareBean);
    }


    @ApiOperation(value = "删除共享", notes = "删除当前磁盘的共享")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "共享id", name = "shareId", paramType = "path", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @DeleteMapping("/disk/{diskId}/share/{shareId}")
    @Authority
    public void deleteDiskShare(TokenModel tokenModel, @PathVariable String diskId, @PathVariable String shareId) {
        diskService.verifyUserDisk(tokenModel, diskId);
        shareService.deleteShare(diskId, shareId);
    }

    @ApiOperation(value = "验证共享code，如需要drawCode再次传入drawCode")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "共享code", name = "code", paramType = "path", required = true),
            @ApiImplicitParam(value = "提取码", name = "drawCode", paramType = "query"),
    })
    @GetMapping("/verify_share/{code}")
    public ShareCodeDto verifyShareCode(@PathVariable String code, @RequestParam(required = false) String drawCode) {
        return shareService.verifyShareCode(code, drawCode);
    }

    @ApiOperation(value = "获取共享文件夹的大小")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "共享id", name = "shareId", paramType = "path", required = true),
            @ApiImplicitParam(value = "文件夹id", name = "folderId", paramType = "path", required = true),
    })
    @GetMapping("/share/{shareId}/folder/{folderId}/size")
    public FolderSizeDto getShareFolderSize(@PathVariable("shareId") String shareId, @PathVariable("folderId") String folderId) {
        return new FolderSizeDto(shareService.getShareFolderSize(shareId, folderId));
    }

    @ApiOperation(value = "获取共享父级文件夹集合", notes = "获取共享父级文件夹集合")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "文件夹id", name = "folderId", paramType = "query", required = true),
    })
    @GetMapping("/share/{shareId}/parent_folder_list")
    public List<FolderDto> getShareParentFolderList(@PathVariable("shareId") String shareId, @RequestParam String folderId) {
        return shareService.getShareParentFolderList(shareId, folderId);
    }
}
