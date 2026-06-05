package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.api.file.bean.DiskSignBean;
import com.xc.core.aspect.AuthorityHandle;
import com.xc.core.bean.PagingBean;
import com.xc.core.bean.QueryBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.GroupModel;
import com.xc.core.model.TokenModel;
import com.xc.core.utils.ServiceUtils;
import com.xc.file.bean.DiskBean;
import com.xc.file.config.Constants;
import com.xc.file.dto.DiskDto;
import com.xc.file.entity.*;
import com.xc.file.enums.FailCode;
import com.xc.file.mapper.DiskMapper;
import com.xc.file.mapper.ShareFolderFileMapper;
import com.xc.file.service.*;
import com.xc.tool.utils.Md5Utils;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>空间流浪服务实现类</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Slf4j
@Service
public class DiskServiceImpl extends ServiceImpl<DiskMapper, DiskEntity> implements DiskService {

    @Autowired
    private DiskMapper diskMapper;
    @Autowired
    private ShareFolderFileMapper shareFolderFileMapper;
    @Autowired
    private Constants constants;
    @Lazy
    @Autowired
    private FileService fileService;
    @Lazy
    @Autowired
    private FolderService folderService;
    @Lazy
    @Autowired
    private SafetyChainService safetyChainService;
    @Lazy
    @Autowired
    private ShareService shareService;


    @Override
    public PagingDto<DiskDto> getDiskPage(Integer current, TokenModel tokenModel, PagingBean pagingBean, DiskBean diskBean) {
        DiskEntity diskEntity = ObjectUtils.convert(new DiskEntity(), diskBean);
        QueryWrapper<DiskEntity> queryWrapper = ServiceUtils.queryData(pagingBean, diskEntity);
        List<String> groupIds = tokenModel.getGroups().stream().map(GroupModel::getGroupId).collect(Collectors.toList());
        if (groupIds.size() > 0) {
            queryWrapper.lambda().nested(i -> i.eq(DiskEntity::getUserId, tokenModel.getUserId()).or().in(DiskEntity::getGroupId, groupIds));
        } else {
            queryWrapper.lambda().eq(DiskEntity::getUserId, tokenModel.getUserId());
        }
        IPage<DiskEntity> iPage = this.page(new Page<>(current, pagingBean.getSize()), queryWrapper);
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), DiskDto::new));
    }

    @Override
    public List<DiskDto> getDiskList(TokenModel tokenModel, QueryBean queryBean, DiskBean diskBean) {
        DiskEntity diskEntity = ObjectUtils.convert(new DiskEntity(), diskBean);
        QueryWrapper<DiskEntity> queryWrapper = ServiceUtils.queryData(queryBean, diskEntity);
        List<String> groupIds = tokenModel.getGroups().stream().map(GroupModel::getGroupId).collect(Collectors.toList());
        if (groupIds.size() > 0) {
            queryWrapper.lambda().nested(i -> i.eq(DiskEntity::getUserId, tokenModel.getUserId()).or().in(DiskEntity::getGroupId, groupIds));
        } else {
            queryWrapper.lambda().eq(DiskEntity::getUserId, tokenModel.getUserId());
        }
        List<DiskEntity> entities = list(queryWrapper);
        return ObjectUtils.convertList(entities, DiskDto::new);
    }

    @Override
    public DiskDto createDisk(DiskEntity diskEntity) {
        String diskNo = StringUtils.generateOnlyNum(constants.getDiskNoPrefix());
        diskEntity.setDiskNo(diskNo);
        diskEntity.setDiskSecret(Md5Utils.getSaltMd5(StringUtils.random(10)));
        diskEntity.setShareCode(Md5Utils.getSaltMd5(UUID.randomUUID().toString()));
        diskEntity.setFreeFlow(constants.getInitFreeFlow());
        diskEntity.setCloudSpace(constants.getInitCloudSpace());
        if (!this.save(diskEntity)) {
            throw FailCode.DISK_CREATE_FAIL.getOperateException();
        }
        return ObjectUtils.convert(new DiskDto(), diskEntity);
    }


    @Override
    @Transactional
    public void deleteDisk(DiskEntity diskEntity) {
        // 删除文件
        FileEntity fileEntity = new FileEntity();
        fileEntity.setDiskId(diskEntity.getId());
        fileService.remove(new QueryWrapper<>(fileEntity));
        // 删除文件夹
        FolderEntity folderEntity = new FolderEntity();
        folderEntity.setDiskId(diskEntity.getId());
        folderService.remove(new QueryWrapper<>(folderEntity));
        // 删除安全连接
        SafetyChainEntity safetyChainEntity = new SafetyChainEntity();
        safetyChainEntity.setDiskId(diskEntity.getId());
        safetyChainService.remove(new QueryWrapper<>(safetyChainEntity));
        // 删除共享文件
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setDiskId(diskEntity.getId());
        QueryWrapper<ShareEntity> queryWrapper = new QueryWrapper<>(shareEntity);
        List<ShareEntity> entities = shareService.list(queryWrapper);
        for (ShareEntity entity : entities) {
            // 删除共享信息
            shareService.removeById(entity.getId());
            // 删除文件共享关联
            ShareFolderFileEntity shareFolderFileEntity = new ShareFolderFileEntity();
            shareFolderFileEntity.setShareId(entity.getId());
            shareFolderFileMapper.delete(new QueryWrapper<>(shareFolderFileEntity));
        }
        // 删除磁盘
        removeById(diskEntity.getId());
    }

    @Override
    public void deleteDiskByUserId(String userId) {
        DiskEntity entity = new DiskEntity();
        entity.setUserId(userId);
        List<DiskEntity> diskEntities = this.list(new QueryWrapper<>(entity));
        for (DiskEntity diskEntity : diskEntities) {
            log.info("删除磁盘:{}", diskEntity.getDiskNo());
            deleteDisk(diskEntity);
        }
    }

    @Override
    public DiskEntity verifyUserHaveDisk(String diskId, TokenModel tokenModel, String authorityCode) {
        DiskEntity diskEntity = getById(diskId);
        if (diskEntity == null) {
            throw FailCode.DISK_ID_ERROR.getOperateException();
        }
        if (!diskEntity.getUserId().equals(tokenModel.getUserId())) {
            if (!AuthorityHandle.verifyGroupAuthority(tokenModel, diskEntity.getGroupId(), authorityCode)) {
                throw FailCode.NOT_INTERFACE_DATA_AUTHORITY.getOperateException();
            }
        }
        return diskEntity;
    }

    @Override
    public void computeFreeFlow(String diskId, long size, boolean isAdd) {
        synchronized (DiskServiceImpl.class) {
            //验证可用空间是否充足
            DiskEntity diskEntity = diskMapper.selectById(diskId);
            //计算可用空间
            if (isAdd) {
                long freeFlow = diskEntity.getFreeFlow() + size;
                diskEntity.setFreeFlow(freeFlow);
            } else {
                if (diskEntity.getFreeFlow() < size) {
                    throw FailCode.FREE_FLOW_SHORTAGE.getOperateException();
                }
                long freeFlow = diskEntity.getFreeFlow() - size;
                diskEntity.setFreeFlow(freeFlow);
            }
            if (!this.updateById(diskEntity)) {
                throw FailCode.UPDATE_FREE_FLOW_FAIL.getOperateException();
            }
        }
    }

    @Override
    public List<DiskEntity> getDiskByUserId(String userId) {
        DiskEntity diskEntity = new DiskEntity();
        diskEntity.setUserId(userId);
        return diskMapper.selectList(new QueryWrapper<>(diskEntity));
    }

    @Override
    public Long getUseSpace(String diskId) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setDiskId(diskId);
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>(fileEntity);
        queryWrapper.select("sum(size) as total");
        Map<String, Object> map = fileService.getMap(queryWrapper);
        long total = 0L;
        if (map != null) {
            total = ((BigDecimal) map.get("total")).longValue();
        }
        return total;
    }

    @Override
    public DiskEntity getDiskByDiskNo(String diskNo) {
        DiskEntity diskEntity = new DiskEntity();
        diskEntity.setDiskNo(diskNo);
        return diskMapper.selectOne(new QueryWrapper<>(diskEntity));
    }


    @Override
    public void verifyDiskSpace(String diskId, long size) {
        DiskEntity diskEntity = diskMapper.selectById(diskId);
        verifyDiskSpace(diskEntity, size);
    }

    @Override
    public void verifyDiskSpace(DiskEntity diskEntity, long size) {
        long total = getUseSpace(diskEntity.getId());
        // 验证网盘是否有空间可用
        if ((diskEntity.getCloudSpace() - total) < size) {
            throw FailCode.DISK_SPACE_SHORTAGE.getOperateException();
        }
    }

    @Override
    public DiskEntity verifyUserDisk(TokenModel tokenModel, String diskId) {
        DiskEntity diskEntity = diskMapper.selectById(diskId);
        if (diskEntity == null) {
            throw FailCode.DISK_ID_ERROR.getOperateException();
        }
        if (!AuthorityHandle.verifyUser(tokenModel, diskEntity.getUserId())) {
            throw FailCode.DISK_NOT_USER.getOperateException();
        }
        return diskEntity;
    }

    @Override
    public DiskEntity verifyDiskSign(DiskSignBean diskSignBean) {
        if (diskSignBean.getDiskNo() == null) {
            throw FailCode.SIGN_DISK_NO_NOT_NULL.getOperateException();
        }
        // 查询接口提供者应用
        DiskEntity diskEntity = this.getDiskByDiskNo(diskSignBean.getDiskNo());
        if (diskEntity == null) {
            throw FailCode.SIGN_DISK_NO_ERROR.getOperateException();
        }
        // 签名验证
        diskSignBean.getSignDataObj(diskEntity.getDiskSecret(), diskEntity.getSignValidTime(), diskSignBean.getAuthorityCode());
        // 返回磁盘信息
        return diskEntity;
    }
}
