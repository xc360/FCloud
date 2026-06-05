package com.xc.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xc.file.entity.StatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>统计Mapper</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Repository
public interface StatisticsMapper extends BaseMapper<StatisticsEntity> {

    public void copyTable(@Param("newTableName") String newTableName, @Param("oldTableName") String oldTableName);

    public Long queryTableCount(@Param("tableName") String tableName);

    public void deleteTable(@Param("tableName") String tableName);

    public void insertBatchStatistics(@Param("tableName") String tableName, @Param("entity") StatisticsEntity entity);
}
