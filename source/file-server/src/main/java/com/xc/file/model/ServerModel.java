package com.xc.file.model;

import lombok.Data;

import java.util.List;

/**
 * <p>通知参数</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
public class ServerModel {
    /**
     * 本机地址
     */
    private String localUrl;

    /**
     * 目录及可用空间
     */
    private List<ServerSpaceModel> serverSpaceModels;
}
