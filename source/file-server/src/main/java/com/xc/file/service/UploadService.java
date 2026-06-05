package com.xc.file.service;

import com.xc.api.file.dto.UploadDto;
import com.xc.file.model.UploadModel;

import java.io.File;

/**
 * <p>文件操作接口</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface UploadService {

    /**
     * <p>上传文件</p>
     *
     * @param uploadModel 上传信息
     * @return 上传信息
     */
    public UploadDto uploadFile(UploadModel uploadModel);

    /**
     * <p>删除文件<p/>
     *
     * @param hashCode 文件hashCode
     * @param suffix   文件后缀
     */
    public void deleteFile(String hashCode, String suffix);

    /**
     * <p>重命名文件</p>
     *
     * @param tempFile 临时文件对象
     * @param newFile  新文件对象
     * @param hashCode 文件hash值
     */
    public void rename(File tempFile, File newFile, String hashCode);

    /**
     * <p>重命名文件</p>
     *
     * @param tempFile 临时文件对象
     * @param newFile  新文件对象
     */
    public void renameFile(File tempFile, File newFile);

    /**
     * <p>更具磁盘大小获取文件存放地址</p>
     * <p>检索合适的存放位置</p>
     *
     * @return 文件存放地址
     */
    public String getFilePathBySize(Long size);
}
