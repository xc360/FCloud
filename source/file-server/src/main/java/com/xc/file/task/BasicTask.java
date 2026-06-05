package com.xc.file.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xc.core.enums.AuditStatus;
import com.xc.file.config.StatisticsAdaptorImpl;
import com.xc.file.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 系统任务
 * </p>
 *
 * @author xc
 * @since 2026-05-13
 */
@Slf4j
@Component
public class BasicTask {

    @Autowired
    private StatisticsAdaptorImpl statisticsAdaptorImpl;
    @Autowired
    private BasicService basicService;

    /**
     * 每1个小时执行一次
     */
//    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void oneHourHandle() {
        log.info("【每1小时】开始执行！");
        basicService.logoutHandle();
        // 日志同步
        statisticsAdaptorImpl.init();
        log.info("【每1小时】开始完毕！");
    }

    /**
     * 每天凌晨3点执行
     */
//    @Scheduled(cron = "0 0 3 * * ? ")
    public void everyDayHandle() {
        log.info("【凌晨3点】开始执行！");
        basicService.dataHandle();
        log.info("【凌晨3点】执行完毕！");
    }
}