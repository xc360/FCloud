package com.xc.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.api.file.bean.DiskSignBean;
import com.xc.core.bean.PagingBean;
import com.xc.core.bean.QueryBean;
import com.xc.core.dto.PagingDto;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.DiskBean;
import com.xc.file.dto.DiskDto;
import com.xc.file.entity.DiskEntity;

import java.util.List;

/**
 * <p>空间流量服务类</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface DiskService extends IService<DiskEntity> {
    /**
     * <p>获取用户的磁盘分页数据</p>
     *
     * @param pagingBean 分页信息
     * @param diskBean   查询条件
     * @return 查询结果
     */
    public PagingDto<DiskDto> getDiskPage(Integer current, TokenModel tokenModel, PagingBean pagingBean, DiskBean diskBean);

    /**
     * <p>获取用户的磁盘集合</p>
     *
     * @param queryBean 查询基础条件
     * @param diskBean  查询条件
     * @return 查询结果
     */
    public List<DiskDto> getDiskList(TokenModel tokenModel, QueryBean queryBean, DiskBean diskBean);

    /**
     * 创建磁盘
     *
     * @param diskEntity 用户信息
     */
    public DiskDto createDisk(DiskEntity diskEntity);

    /**
     * <p>删除磁盘</p>
     *
     * @param diskEntity 磁盘实体
     */
    public void deleteDisk(DiskEntity diskEntity);

    /**
     * <p>根据用户主键删除磁盘</p>
     *
     * @param userId 用户主键
     */
    public void deleteDiskByUserId(String userId);

    /**
     * 验证用户是否拥有该磁盘
     *
     * @param diskId        磁盘
     * @param tokenModel    token信息
     * @param authorityCode 权限code
     * @return 磁盘实体
     */
    public DiskEntity verifyUserHaveDisk(String diskId, TokenModel tokenModel, String authorityCode);

    /**
     * <p>计算可用流量</p>
     *
     * @param size   使用大小
     * @param diskId 磁盘主键
     */
    public void computeFreeFlow(String diskId, long size, boolean isAdd);

    /**
     * <p>根据用户id获取磁盘</p>
     *
     * @param userId 用户主键
     * @return 用户信息
     */
    public List<DiskEntity> getDiskByUserId(String userId);


    /**
     * 计算当前用户使用空间
     *
     * @param diskId 磁盘主键
     * @return 使用的空间
     */
    public Long getUseSpace(String diskId);

    /**
     * 根据账号获取磁盘信息
     *
     * @param diskNo 磁盘编号
     * @return 用户信息
     */
    public DiskEntity getDiskByDiskNo(String diskNo);

    /**
     * 验证用户空间是否足够
     *
     * @param diskId 磁盘主键
     * @param size   文件大小
     */
    public void verifyDiskSpace(String diskId, long size);

    /**
     * 验证用户空间是否足够
     *
     * @param diskEntity 磁盘信息
     * @param size       文件大小
     */
    public void verifyDiskSpace(DiskEntity diskEntity, long size);

    /**
     * 验证当前用户是否拥有磁盘
     *
     * @param tokenModel 用户token
     * @param diskId     磁盘主键
     */
    public DiskEntity verifyUserDisk(TokenModel tokenModel, String diskId);

    /**
     * @param diskSignBean 磁盘签名
     * @return 磁盘信息
     */
    public DiskEntity verifyDiskSign(DiskSignBean diskSignBean);
}
