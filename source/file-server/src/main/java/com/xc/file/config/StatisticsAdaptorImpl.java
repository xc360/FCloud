package com.xc.file.config;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.xc.api.basic.BasicApi;
import com.xc.api.basic.bean.SystemErrorMessageBean;
import com.xc.api.basic.enums.BasicRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.aspect.StatisticsAdaptor;
import com.xc.core.bean.SignBean;
import com.xc.core.enums.ErrorType;
import com.xc.core.enums.StatisticsType;
import com.xc.core.exception.OperateException;
import com.xc.core.model.StatisticsModel;
import com.xc.file.entity.StatisticsEntity;
import com.xc.file.mapper.StatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>统计适配器</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Slf4j
@Component
public class StatisticsAdaptorImpl implements StatisticsAdaptor {
    @Autowired
    private StatisticsMapper statisticsMapper;
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;

    @Override
    public String requestError(StatisticsModel statisticsModel) {
        String requestId = IdWorker.getIdStr();
        Thread thread = new Thread(() -> {
            try {
                StatisticsEntity statisticsEntity = new StatisticsEntity();
                statisticsEntity.setId(requestId);
                statisticsEntity.setType(StatisticsType.ERROR.getType());
                statisticsEntity.setStatisticsTime(new Date());
                statisticsEntity.setClientIp(statisticsModel.getClientIp());
                statisticsEntity.setMethod(statisticsModel.getMethod());
                statisticsEntity.setUrl(statisticsModel.getUrl());
                if (statisticsModel.getTokenModel() != null) {
                    statisticsEntity.setUserId(statisticsModel.getTokenModel().getUserId());
                }
                statisticsEntity.setParamJson(statisticsModel.getParamJson());
                statisticsEntity.setResponseTime(statisticsModel.getResponseTime());
                Throwable throwable = statisticsModel.getThrowable();
                statisticsEntity.setErrorMessage(throwable.getMessage());
                if (throwable instanceof OperateException) {
                    statisticsEntity.setErrorType(ErrorType.OPERATE.getType());
                } else {
                    statisticsEntity.setErrorType(ErrorType.SYSTEM.getType());
                }
                this.createStatistics(statisticsEntity);
                // 系统告警
                if (!(throwable instanceof OperateException)) {
                    SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), BasicRestCode.createOpenSystemErrorNoticeMessage.getCode());
                    SystemErrorMessageBean systemErrorMessageBean = new SystemErrorMessageBean();
                    systemErrorMessageBean.setMessage(throwable.getMessage() + "");
                    basicApi.createOpenSystemErrorNoticeMessage(signBean, systemErrorMessageBean);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        thread.start();
        return requestId;
    }

    @Override
    public String requestSuccess(StatisticsModel statisticsModel) {
        String requestId = IdWorker.getIdStr();
        Thread thread = new Thread(() -> {
            try {
                StatisticsEntity statisticsEntity = new StatisticsEntity();
                statisticsEntity.setId(requestId);
                statisticsEntity.setType(StatisticsType.SUCCESS.getType());
                statisticsEntity.setStatisticsTime(new Date());
                statisticsEntity.setClientIp(statisticsModel.getClientIp());
                statisticsEntity.setMethod(statisticsModel.getMethod());
                statisticsEntity.setUrl(statisticsModel.getUrl());
                if (statisticsModel.getTokenModel() != null) {
                    statisticsEntity.setUserId(statisticsModel.getTokenModel().getUserId());
                }
                statisticsEntity.setParamJson(statisticsModel.getParamJson());
                statisticsEntity.setResponseTime(statisticsModel.getResponseTime());
                statisticsEntity.setResultJson(statisticsModel.getResultJson());
                this.createStatistics(statisticsEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        thread.start();
        return requestId;
    }

    /**
     * 创建统计
     */
    @Transactional
    public void createStatistics(StatisticsEntity statisticsEntity) {
        String oldTableName = "xc_statistics";
        String newTableName = "xc_statistics_" + DateUtil.format(statisticsEntity.getStatisticsTime(), "yyyy_MM");
        Long total = statisticsMapper.queryTableCount(newTableName);
        if (total == 0) {
            statisticsMapper.copyTable(newTableName, oldTableName);
        }
        statisticsMapper.insertBatchStatistics(newTableName, statisticsEntity);
    }

    /**
     * 自动删除7个月以前的数据
     */
    public void init() {
        Date startTime = DateUtil.offsetMonth(new Date(), -7);
        String newTableName = "xc_statistics_" + DateUtil.format(startTime, "yyyy_MM");
        statisticsMapper.deleteTable(newTableName);
    }
}
