package com.xc.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.api.file.bean.AudioHandleBean;
import com.xc.api.file.bean.ImageHandleBean;
import com.xc.api.file.bean.VideoHandleBean;
import com.xc.api.file.dto.FileDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FileHashEntity;
import io.swagger.annotations.ApiModelProperty;

import java.io.File;

/**
 * <p>文件hash信息Service</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface FileHashService extends IService<FileHashEntity> {

    /**
     * <p>验证文件hash是否存在</p>
     *
     * @param hashCode  文件的hash值
     * @param groupCode 组标识
     * @return true：存在，false：不存在
     */
    public FileHashEntity getFileHash(String hashCode, String groupCode);

    /**
     * <p>创建文件hash信息</p>
     *
     * @param diskEntity 磁盘信息
     * @param file       文件对象
     * @param path       文件路径
     * @param folderId   文件夹主键
     * @param fileName   文件名称
     * @return 创建成功的文件信息
     */
    public FileDto createFileHash(DiskEntity diskEntity, File file, String path, String folderId, String fileName);

    /**
     * <p>创建文件hash信息</p>
     *
     * @param diskEntity 磁盘信息
     * @param file       文件对象
     * @param path       文件路径
     * @param folderId   文件夹主键
     * @param fileName   文件名称
     * @param status     文件状态
     * @return 创建成功的文件信息
     */
    public FileDto createFileHash(DiskEntity diskEntity, File file, String path, String folderId, String fileName, String status);

    /**
     * 创建文件hash信息
     *
     * @param hashCode  文件的hash值
     * @param groupCode 组标识
     * @param size      文件大小
     * @param code      标识
     * @return 文件hash信息
     */
    public FileHashEntity createFileHash(String hashCode, String groupCode, Long size, String code);

    /**
     * 压缩图片
     *
     * @param fileEntity      文件信息
     * @param imageHandleBean 视频处理参数
     * @return 处理后的文件
     */
    public File imageHandle(FileEntity fileEntity, ImageHandleBean imageHandleBean);

    /**
     * 视频转M3u8
     *
     * @param fileEntity      文件信息
     * @param videoHandleBean 视频处理参数
     * @return 处理后的文件
     */
    public File videoToM3u8(FileEntity fileEntity, VideoHandleBean videoHandleBean);

    /**
     * 视频加水印
     *
     * @param fileEntity      文件信息
     * @param videoHandleBean 视频处理参数
     * @return 处理后的文件
     */
    public File videoWatermark(FileEntity fileEntity, VideoHandleBean videoHandleBean);

    /**
     * 音频转换
     *
     * @param audioHandleBean 音频处理参数
     * @return 处理后的文件
     */
    public File audioConvert(FileEntity fileEntity, AudioHandleBean audioHandleBean);
}
