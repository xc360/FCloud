package com.xc.file.service;

import com.xc.file.model.DownloadModel;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;

/**
 * <p>下载服务类</p>
 *
 * @author xc
 * @version v1.0.0
 */
public interface DownloadService {
    /**
     * <p>下载文件</p>
     *
     * @param response      Response
     * @param downloadModel 下载信息
     * @param w             宽
     * @param h             高
     * @param s             比例,小数
     */
    public void downloadFile(HttpServletResponse response, DownloadModel downloadModel, Integer w, Integer h, Double s);

    /**
     * <p>下载文件及断点续传</p>
     *
     * @param response      Response
     * @param downloadModel 下载信息
     */
    public void download(HttpServletResponse response, File file, DownloadModel downloadModel);

    /**
     * <p>压缩图片</p>
     *
     * @param file        需要压缩的文件地址
     * @param outFileName 输出文件名
     * @param w           宽
     * @param h           高
     * @param s           比例
     * @return 文件压缩地址
     */
    public String compressImage(File file, String outFileName, Integer w, Integer h, Double s);

    /**
     * <p>下载M3u8格式文件</p>
     *
     * @param response      Response
     * @param downloadModel 下载信息
     */
    public void downloadM3u8(HttpServletResponse response, DownloadModel downloadModel);

    /**
     * <p>下载M3u8格式文件</p>
     *
     * @param response      Response
     * @param downloadModel 下载信息
     * @param inputStream   文件流
     */
    public void downloadM3u8(HttpServletResponse response, DownloadModel downloadModel, InputStream inputStream);

    /**
     * <p>下载M3u8格式文件</p>
     *
     * @param response Response
     * @param fileName 文件名称
     */
    public void downloadM3u8(HttpServletResponse response, String fileName);

    /**
     * 获取下载文件
     *
     * @param code 文件标识
     * @return 下载文件
     */
    public File getDownloadFile(String code);
}
