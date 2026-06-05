package com.xc.file.config;

import com.xc.file.model.FileModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * <p>文件服务配置类</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "xc.file")
public class Constants {
    /**
     * 服务唯一机器码，防止uuid重复
     */
    private String machineId = "";
    /**
     * 磁盘编号前缀
     */
    private String diskNoPrefix = "DN";
    /**
     * 初始可用流量，默认10G
     */
    private long initFreeFlow = 10737418240L;
    /**
     * 初始网盘空间，默认10G
     */
    private long initCloudSpace = 10737418240L;
    /**
     * 目录信息
     */
    private List<FolderConfig> folderPaths;
    /**
     * 本机地址
     */
    private String localUrl;
    /**
     * 本地旧地址
     */
    private String localOldUrl;
    /**
     * 每次上传文件大小，默认10Mb
     */
    private Long renewalSize = 10485760L;
    /**
     * <p>续传文件信息</p>
     * <p>非配置文件</p>
     */
    private Map<String, FileModel> fileInfoTable = new Hashtable<>();
    /**
     * 临时目录
     */
    private FolderConfig tempFolder;
    /**
     * 文件服务信息key
     */
    private String serverInfoKey = "serverInfo";
    /**
     * 开放磁盘下载
     */
    private String openDiskUrl = "/open/disk/";
    /**
     * 开放的m3u8下载url
     */
    private String m3u8DiskUrl = "/m3u8/disk/";
    /**
     * cdn下载url
     */
    private String cdnDiskUrl = "/cdn/disk/";
    /**
     * 下载hash的Url
     */
    private String downloadHashUrl = "/hash_file/{hashId}";
    /**
     * 磁盘下载路劲
     */
    private String diskDownloadPath = "/disk/{diskId}/file/";
    /**
     * 共享下载路劲
     */
    private String shareDownloadPath = "/share/{visitCode}/file/";
    /**
     * 上传文件路径
     */
    public String uploadPath = "/disk/{diskId}/upload_file";
    /**
     * 根
     */
    private String root = "root";
    /**
     * 缓存大小，默认1Mb
     */
    private Integer fileCache = 1048576;
    /**
     * 禁用ip地址刷新时间，默认1小时
     */
    private Long disableIpRefreshTime = 3600000L;
    /**
     * 注销保留时间，默认60天
     */
    private Long logOffGuardTime = 5184000000L;
    /**
     * 签名有效期，默认30分钟
     */
    private Long signValidTime = 1800000L;
    /**
     * 失效文件清理时间，默认1天
     */
    private Long fileCleaningTime = 86400000L;
    /**
     * 开启日志同步
     */
    private Boolean openLogSync = false;
    /**
     * 日志服务名称
     */
    private String logServeName;
}
