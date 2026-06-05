package com.xc.file.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xc.file.dto.FolderFileDto;
import com.xc.file.entity.FolderEntity;
import com.xc.file.model.QueryFolderFileModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * <p>文件夹Mapper 接口</p>
 *
 * @author xc
 * @version v1.0
 */
@Repository
public interface FolderMapper extends BaseMapper<FolderEntity> {
    /**
     * 查询文件夹和文件
     *
     * @param page                 分页
     * @param ew                   排序条件
     * @param queryFolderFileModel 参数
     * @return 文件夹和文件集合
     */
    public IPage<FolderFileDto> getFolderFilePage(@Param("page") IPage<FolderEntity> page, @Param(Constants.WRAPPER) QueryWrapper<Object> ew, @Param("obj") QueryFolderFileModel queryFolderFileModel);
}
