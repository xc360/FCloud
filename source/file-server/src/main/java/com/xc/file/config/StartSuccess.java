package com.xc.file.config;

import com.xc.file.service.BasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * <p>启动成功执行</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Component
@Slf4j
public class StartSuccess implements CommandLineRunner {
    @Autowired
    private BasicService basicService;
    @Autowired
    private XcConfig xcConfig;

    @Override
    public void run(String... strings) {
        xcConfig.init();
        // 处理已注销用户
        basicService.logoutHandle();
        // 数据处理
        basicService.dataHandle();
    }
}
