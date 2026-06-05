package com.xc.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>统计实体</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
@TableName("xc_statistics")
public class StatisticsEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 类型，0：开始，1：异常，2：结束
     */
    private String type;
    /**
     * 统计时间
     */
    private Date statisticsTime;
    /**
     * 客户端ip地址
     */
    private String clientIp;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 用户访问url
     */
    private String url;
    /**
     * 用户主键
     */
    private String userId;
    /**
     * 请求参数
     */
    private String paramJson;
    /**
     * 响应时长，单位秒
     */
    private BigDecimal responseTime;
    /**
     * 返回参数
     */
    private String resultJson;
    /**
     * 异常类型，0：操作异常，1：系统异常
     */
    private String errorType;
    /**
     * 异常消息
     */
    private String errorMessage;
}
