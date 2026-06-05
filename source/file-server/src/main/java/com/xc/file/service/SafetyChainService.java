package com.xc.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.core.bean.PagingBean;
import com.xc.core.dto.PagingDto;
import com.xc.file.bean.SafetyChainBean;
import com.xc.file.dto.SafetyChainDto;
import com.xc.file.entity.SafetyChainEntity;

/**
 * <p>安全链接接口</p>
 *
 * @author xc
 * @version v1.0
 */
public interface SafetyChainService extends IService<SafetyChainEntity> {

    /**
     * <p>获取磁盘的安全链接页数据</p>
     *
     * @param diskId          磁盘主键
     * @param pagingBean      分页信息
     * @param safetyChainBean 查询条件
     * @return 查询结果
     */
    public PagingDto<SafetyChainDto> getSafetyChainPage(String diskId, Integer current, PagingBean pagingBean, SafetyChainBean safetyChainBean);

    /**
     * <p>创建安全链接</p>
     *
     * @param diskId          磁盘主键
     * @param safetyChainBean 安全链接数据
     * @return 创建成功的数据
     */
    public SafetyChainDto createSafetyChain(String diskId, SafetyChainBean safetyChainBean);

    /**
     * <p>更新安全链接</p>
     *
     * @param diskId          磁盘主键
     * @param safetyChainId   安全链接id
     * @param safetyChainBean 安全链接数据
     * @return 更新成功的数据
     */
    public SafetyChainDto updateSafetyChain(String diskId, String safetyChainId, SafetyChainBean safetyChainBean);

    /**
     * <p>删除安全链接</p>
     *
     * @param diskId        磁盘主键
     * @param safetyChainId 安全链接id
     */
    public void deleteSafetyChain(String diskId, String safetyChainId);
}
