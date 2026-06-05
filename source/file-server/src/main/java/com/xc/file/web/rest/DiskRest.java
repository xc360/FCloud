package com.xc.file.web.rest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xc.core.annotation.Authority;
import com.xc.core.aspect.AuthorityHandle;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.BasicInfoBean;
import com.xc.file.bean.DiskBean;
import com.xc.file.dto.BasicInfoDto;
import com.xc.file.dto.DiskDto;
import com.xc.file.dto.DiskSecretDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.service.DiskService;
import com.xc.file.service.FileService;
import com.xc.file.service.FolderService;
import com.xc.tool.utils.Md5Utils;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>【用户】用户磁盘信息</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = {"【用户】用户磁盘信息"})
@RestController
public class DiskRest {

    @Autowired
    private DiskService diskService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FileService fileService;

    @ApiOperation(value = "获取磁盘信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @GetMapping(value = "/disk/{diskId}")
    @Authority
    public DiskDto getDisk(TokenModel tokenModel, @PathVariable String diskId) {
        DiskEntity diskEntity = diskService.verifyUserDisk(tokenModel, diskId);
        DiskDto diskDto = ObjectUtils.convert(new DiskDto(), diskEntity);
        diskDto.setUseSpace(diskService.getUseSpace(diskId));
        return diskDto;
    }

    @ApiOperation(value = "获取磁盘分页", notes = "获取当前用户的磁盘页")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @GetMapping("/disk_pages/{current}")
    @Authority
    public PagingDto<DiskDto> getDiskPage(TokenModel tokenModel, @PathVariable Integer current,
                                          @ModelAttribute PagingBean pagingBean, @ModelAttribute DiskBean diskBean) {
        return diskService.getDiskPage(current, tokenModel, pagingBean, diskBean);
    }

    @ApiOperation(value = "获取磁盘集合", notes = "获取当前用户的磁盘集合")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @GetMapping("/disk_list")
    @Authority
    public List<DiskDto> getDiskList(TokenModel tokenModel, @ModelAttribute PagingBean pagingBean, @ModelAttribute DiskBean diskBean) {
        return diskService.getDiskList(tokenModel, pagingBean, diskBean);
    }

    @ApiOperation(value = "创建磁盘", notes = "创建当前用户的磁盘")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @PostMapping("/disk")
    @Authority
    public DiskDto createDisk(TokenModel tokenModel, @RequestBody DiskBean diskBean) {
        DiskEntity diskEntity = ObjectUtils.convert(new DiskEntity(), diskBean);
        diskEntity.setUserId(tokenModel.getUserId());
        return diskService.createDisk(diskEntity);
    }

    @ApiOperation(value = "修改磁盘", notes = "修改当前用户的磁盘")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @PutMapping("/disk/{diskId}")
    @Authority
    public DiskDto updateDisk(TokenModel tokenModel, @PathVariable String diskId, @RequestBody DiskBean diskBean) {
        DiskEntity diskEntity = diskService.verifyUserHaveDisk(diskId, tokenModel, "updateDisk");
        ObjectUtils.convert(diskEntity, diskBean);
        if (!diskService.updateById(diskEntity)) {
            throw FailCode.DISK_UPDATE_FAIL.getOperateException();
        }
        return ObjectUtils.convert(new DiskDto(), diskEntity);
    }

    @ApiOperation(value = "删除磁盘", notes = "删除当前用户的磁盘")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true),
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
    })
    @DeleteMapping("/disk/{diskId}")
    @Authority
    public void deleteDisk(TokenModel tokenModel, @PathVariable String diskId) {
        DiskEntity diskEntity = diskService.verifyUserHaveDisk(diskId, tokenModel, "deleteDisk");
        diskService.deleteDisk(diskEntity);
    }

    @ApiOperation(value = "获取磁盘秘钥", notes = "获取当前用户的磁盘秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "应用主键", name = "appId", paramType = "path", required = true),
    })
    @GetMapping("/disk/{diskId}/secret")
    @Authority
    public DiskSecretDto getDiskSecret(TokenModel tokenModel, @PathVariable String diskId) {
        DiskEntity diskEntity = diskService.verifyUserHaveDisk(diskId, tokenModel, "getDiskSecret");
        DiskSecretDto diskSecretDto = new DiskSecretDto();
        diskSecretDto.setDiskSecret(diskEntity.getDiskSecret());
        return diskSecretDto;
    }

    @ApiOperation(value = "更新磁盘秘钥", notes = "更新当前用户的磁盘秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "应用主键", name = "appId", paramType = "path", required = true),
    })
    @PutMapping("/disk/{diskId}/secret")
    @Authority
    public DiskSecretDto updateDiskSecret(TokenModel tokenModel, @PathVariable String diskId) {
        DiskEntity diskEntity = diskService.verifyUserHaveDisk(diskId, tokenModel, "updateDiskSecret");
        diskEntity.setDiskSecret(Md5Utils.getSaltMd5(StringUtils.random(10)));
        if (!diskService.updateById(diskEntity)) {
            throw FailCode.DISK_UPDATE_FAIL.getOperateException();
        }
        DiskSecretDto diskSecretDto = new DiskSecretDto();
        diskSecretDto.setDiskSecret(diskEntity.getDiskSecret());
        return diskSecretDto;
    }

    @ApiOperation(value = "获取基础信息", notes = "获取基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true),
            @ApiImplicitParam(value = "文件夹路径", name = "path", paramType = "query", required = true)
    })
    @GetMapping("/basic_info")
    @Authority
    public BasicInfoDto getBasicInfo(TokenModel tokenModel, @ModelAttribute BasicInfoBean basicInfoBean) {
        BasicInfoDto basicInfoDto = new BasicInfoDto();
        DiskEntity diskEntity = diskService.getDiskByDiskNo(basicInfoBean.getDiskNo());
        if (diskEntity == null) {
            throw FailCode.DISK_ID_ERROR.getOperateException();
        }
        if (!AuthorityHandle.verifyUser(tokenModel, diskEntity.getUserId())) {
            throw FailCode.DISK_NOT_USER.getOperateException();
        }
        basicInfoDto.setDiskId(diskEntity.getId());
        FolderEntity folderEntity = folderService.getFolderByPath(diskEntity.getId(), basicInfoBean.getFolderPath());
        if (folderEntity != null) {
            basicInfoDto.setFolderId(folderEntity.getId());
            FileEntity fileEntity = new FileEntity();
            fileEntity.setDiskId(diskEntity.getId());
            fileEntity.setFolderId(folderEntity.getId());
            if (basicInfoBean.getFileName() != null) {
                fileEntity.setName(basicInfoBean.getFileName());
                FileEntity entity = fileService.getOne(new QueryWrapper<>(fileEntity));
                if (entity != null) {
                    basicInfoDto.setFileId(entity.getId());
                }
            }
        }
        return basicInfoDto;
    }
}
