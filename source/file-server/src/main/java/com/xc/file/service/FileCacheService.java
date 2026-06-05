package com.xc.file.service;

import com.xc.file.model.ServerModel;


/**
 * <p>文件缓存数据</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface FileCacheService {


    /**
     * 更新服务信息
     *
     * @param serverModel 初始化文件信息
     */
    public void updateServerInfo(ServerModel serverModel);

    /**
     * <p>获取有空间可以上传文件的服务器ip</p>
     *
     * @param size 文件大小
     * @return 可以上传文件的url
     */
    public String getServiceIp(Long size);
}
