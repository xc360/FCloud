package com.xc.file.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xc.api.basic.bean.UserSignBean;
import com.xc.api.file.bean.AudioHandleBean;
import com.xc.api.file.bean.DiskSignBean;
import com.xc.api.file.bean.ImageHandleBean;
import com.xc.api.file.bean.VideoHandleBean;
import com.xc.api.file.dto.FileDto;
import com.xc.file.bean.FileBean;
import com.xc.file.bean.UpdateFileBean;
import com.xc.file.dto.CdnDto;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.model.DownloadModel;

import java.util.List;

/**
 * <p>文件管理接口</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface FileService extends IService<FileEntity> {

    /**
     * <p>创建文件</p>
     *
     * @param diskId   磁盘主键
     * @param fileBean 文件信息
     * @param isVerify 是否验证目录信息
     * @return 创建成功的文件信息
     */
    public FileEntity createFile(String diskId, FileBean fileBean, boolean isVerify);

    /**
     * <p>修改磁盘的文件名称</p>
     *
     * @param diskId         磁盘主键
     * @param fileId         文件id
     * @param updateFileBean 文件信息
     * @return 返回结果
     */
    public FileDto updateDiskFile(String diskId, String fileId, UpdateFileBean updateFileBean);

    /**
     * <p>删除磁盘的文件</p>
     *
     * @param diskId 磁盘主键
     * @param fileId 文件id
     */
    public FileEntity deleteDiskFile(String diskId, String fileId);

    /**
     * <p>写入下载文件地址</p>
     *
     * @param files        文件信息集合
     * @param downloadPath 下载地址
     * @return 文件信息集合，带有下载url
     */
    public List<FileDto> setDownloadUrl(List<FileDto> files, String downloadPath);

    /**
     * 验证下载文件
     *
     * @param diskId 磁盘主键
     * @param path   文件路径，带文件名
     * @return 文件信息
     */
    public FileEntity getOpenDownload(String diskId, String path);

    /**
     * 验证文件是否共享下载
     *
     * @param referer  当前请求域
     * @param diskNo   磁盘编号
     * @param filePath 文件路径
     * @return 下载信息
     */
    public DownloadModel verifySafetyChain(String referer, UserSignBean userSignBean, String diskNo, String filePath);

    /**
     * 验证磁盘文件
     *
     * @param downloadCode 下载code
     * @param fid          文件id
     * @return 下载信息
     */
    public DownloadModel getDownloadModel(String downloadCode, String fid, String downloadType);

    /**
     * 下载完成
     *
     * @param downloadModel 参数
     */
    public void downloadFinish(DownloadModel downloadModel);

    /**
     * 创建当前磁盘的cdn地址
     *
     * @param diskId 磁盘主键
     */
    public CdnDto createDiskFileCdnUrl(String diskId, String fileId);

    /**
     * 根据文件夹主键集合获取文件
     *
     * @param diskId    磁盘主键
     * @param folderIds 文件夹主键集合
     * @return 文件信息
     */
    public List<FileEntity> getFilesByFolderIds(String diskId, List<String> folderIds);

    /**
     * 批量复制文件
     *
     * @param diskId 磁盘主键
     * @param files  文件集合
     * @return 文件集合
     */
    public List<FileEntity> batchCopyFile(String diskId, String folderId, List<FileEntity> files);

    /**
     * 获取文件对象
     *
     * @param diskId   磁盘主键
     * @param filePath 文件path
     * @param status   是否有效，0：有效，1：无效
     * @return 文件对象
     */
    public FileEntity getFileByPath(String diskId, String filePath, String status);

    /**
     * 获取文件集合根据路径
     *
     * @param diskEntity 磁盘信息
     * @param filePaths  文件路径
     * @return 文件集合
     */
    public List<FileEntity> getFileListByPath(DiskEntity diskEntity, List<String> filePaths);

    /**
     * 添加path
     *
     * @param queryWrapper 查询条件
     * @param path         path路径
     * @param diskId       磁盘主键
     */
    public boolean addPath(QueryWrapper<FileEntity> queryWrapper, String path, String diskId);

    /**
     * 压缩图片
     *
     * @param diskSignBean    磁盘签名
     * @param diskEntity      磁盘实体
     * @param imageHandleBean 处理配置
     * @param forwardUrl      转发地址
     * @return 压缩的文件对象
     */
    public FileDto imageHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, ImageHandleBean imageHandleBean, String forwardUrl);

    /**
     * 视频处理
     *
     * @param diskSignBean    磁盘签名
     * @param diskEntity      磁盘实体
     * @param videoHandleBean 处理配置
     * @param forwardUrl      转发地址
     * @return 压缩的文件对象
     */
    public FileDto videoHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, VideoHandleBean videoHandleBean, String forwardUrl);

    /**
     * 音频处理
     *
     * @param diskSignBean    磁盘签名
     * @param diskEntity      磁盘实体
     * @param audioHandleBean 处理配置
     * @param forwardUrl      转发地址
     * @return 压缩的文件对象
     */
    public FileDto audioHandle(DiskSignBean diskSignBean, DiskEntity diskEntity, AudioHandleBean audioHandleBean, String forwardUrl);
}
