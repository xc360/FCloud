package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.utils.ServiceUtils;
import com.xc.file.bean.SafetyChainBean;
import com.xc.file.dto.SafetyChainDto;
import com.xc.file.entity.SafetyChainEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.mapper.SafetyChainMapper;
import com.xc.file.service.SafetyChainService;
import com.xc.tool.utils.ObjectUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * <p>安全链接实现类</p>
 *
 * @author xc
 * @version v1.0
 */
@Service
public class SafetyChainServiceImpl extends ServiceImpl<SafetyChainMapper, SafetyChainEntity> implements SafetyChainService {


    @Override
    public PagingDto<SafetyChainDto> getSafetyChainPage(String diskId, Integer current, PagingBean pagingBean, SafetyChainBean safetyChainBean) {
        SafetyChainEntity safetyChainEntity = ObjectUtils.convert(new SafetyChainEntity(), safetyChainBean);
        safetyChainEntity.setDiskId(diskId);
        QueryWrapper<SafetyChainEntity> queryWrapper = ServiceUtils.queryData(pagingBean, safetyChainEntity);
        IPage<SafetyChainEntity> iPage = this.page(new Page<>(current, pagingBean.getSize()), queryWrapper);
        return new PagingDto<>(iPage.getTotal(), ObjectUtils.convertList(iPage.getRecords(), SafetyChainDto::new));
    }

    @Override
    public SafetyChainDto createSafetyChain(String diskId, SafetyChainBean safetyChainBean) {
        SafetyChainEntity safetyChainEntity = new SafetyChainEntity();
        safetyChainEntity.setDiskId(diskId);
        try {
            ObjectUtils.convert(safetyChainEntity, safetyChainBean);
            if (!this.save(safetyChainEntity)) {
                throw FailCode.SAFETY_CHAIN_CREATE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.SAFETY_CHAIN_REPEAT.getOperateException();
        }
        return ObjectUtils.convert(new SafetyChainDto(), safetyChainEntity);
    }

    @Override
    public SafetyChainDto updateSafetyChain(String diskId, String safetyChainId, SafetyChainBean safetyChainBean) {
        //判断安全链接是否存在
        SafetyChainEntity safetyChainEntity = this.getById(safetyChainId);
        if (safetyChainEntity == null) {
            throw FailCode.SAFETY_CHAIN_NOT_EXIST.getOperateException();
        }
        // 验证数据权限
        if (!safetyChainEntity.getDiskId().equals(diskId)) {
            throw FailCode.NOT_DATA_AUTHORITY.getOperateException();
        }
        try {
            //修改
            ObjectUtils.convert(safetyChainEntity, safetyChainBean);
            if (!this.updateById(safetyChainEntity)) {
                throw FailCode.SAFETY_CHAIN_UPDATE_FAIL.getOperateException();
            }
        } catch (DuplicateKeyException e) {
            throw FailCode.SAFETY_CHAIN_REPEAT.getOperateException();
        }
        return ObjectUtils.convert(new SafetyChainDto(), safetyChainEntity);
    }

    @Override
    public void deleteSafetyChain(String diskId, String safetyChainId) {
        SafetyChainEntity safetyChainEntity = this.getById(safetyChainId);
        if (safetyChainEntity == null) {
            throw FailCode.SAFETY_CHAIN_NOT_EXIST.getOperateException();
        }
        // 验证数据权限
        if (!safetyChainEntity.getDiskId().equals(diskId)) {
            throw FailCode.NOT_DATA_AUTHORITY.getOperateException();
        }
        if (!this.removeById(safetyChainId)) {
            throw FailCode.SAFETY_CHAIN_DELETE_FAIL.getOperateException();
        }
    }
}
