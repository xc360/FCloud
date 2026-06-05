package com.xc.file.service.impl;

import com.xc.core.enums.CoreRedisPrefix;
import com.xc.core.utils.RedisUtils;
import com.xc.file.config.Constants;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.model.ServerModel;
import com.xc.file.model.ServerSpaceModel;
import com.xc.file.service.FileCacheService;
import com.xc.tool.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>文件缓存数据</p>
 *
 * @author xc
 * @version v1.0
 */
@Service
@Slf4j
public class FileCacheServiceImpl implements FileCacheService {
    @Autowired
    private Constants constants;

    @Override
    public void updateServerInfo(ServerModel serverModel) {
        //装载本地文件
        String key = constants.getServerInfoKey();
        if (RedisUtils.lock(CoreRedisPrefix.LOCK.getKey() + key)) {
            String value = RedisUtils.get(RedisPrefix.SERVER.getKey() + key);
            List<ServerModel> serverModels = new ArrayList<>();
            if (value != null) {
                serverModels = JSONUtils.getListByString(value, ServerModel.class);
                boolean bool = true;
                for (ServerModel serverModel1 : serverModels) {
                    if (serverModel1.getLocalUrl().equals(serverModel.getLocalUrl())) {
                        if (Collections.replaceAll(serverModels, serverModel1, serverModel)) {
                            log.info("替换成功！");
                            bool = false;
                        }
                    }
                }
                if (bool) {
                    serverModels.add(serverModel);
                }
            } else {
                serverModels.add(serverModel);
            }
            RedisUtils.set(RedisPrefix.SERVER.getKey() + key, JSONUtils.getStringByObject(serverModels));
            RedisUtils.unlock(CoreRedisPrefix.LOCK.getKey() + key);
        } else {
            log.error("初始化转载文件超时！");
        }
    }

    @Override
    public String getServiceIp(Long size) {
        String value = RedisUtils.get(RedisPrefix.SERVER.getKey() + constants.getServerInfoKey());
        List<ServerModel> serverModels = JSONUtils.getListByString(value, ServerModel.class);
        if (serverModels == null || serverModels.size() == 0) {
            throw FailCode.NO_FILE_SERVICE_AVAILABLE.getOperateException();
        }
        for (ServerModel serverModel : serverModels) {
            for (ServerSpaceModel serverSpaceModel : serverModel.getServerSpaceModels()) {
                if (serverSpaceModel.getAvailableSpace() > size) {
                    return serverModel.getLocalUrl();
                }
            }
        }
        throw FailCode.SERVER_SPACE_SHORTAGE.getOperateException();
    }


}
