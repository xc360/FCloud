package com.xc.file.service.impl;

import cn.hutool.core.io.FileUtil;
import com.xc.file.config.Constants;
import com.xc.file.config.FolderConfig;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.FileSuffix;
import com.xc.file.model.DownloadModel;
import com.xc.file.service.DownloadService;
import com.xc.file.service.FileService;
import com.xc.file.utils.FfmpegUtils;
import com.xc.tool.http.FileType;
import com.xc.tool.utils.FileUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>下载服务实现</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Service
public class DownloadServiceImpl implements DownloadService {
    @Autowired
    private Constants constants;
    @Autowired
    private FileService fileService;

    @Override
    public void downloadFile(HttpServletResponse response, DownloadModel downloadModel, Integer w, Integer h, Double s) {
        File file = getDownloadFile(downloadModel.getCode());
        //是否压缩文件
        if (w != null || h != null || s != null) {
            String path = compressImage(file, downloadModel.getHashId(), w, h, s);
            download(response, new File(path), downloadModel);
        } else {
            download(response, file, downloadModel);
        }
    }

    @Override
    public void download(HttpServletResponse response, File file, DownloadModel downloadModel) {
        String range = downloadModel.getRange();
        String fileName = downloadModel.getName();
        int open = downloadModel.getOpen();
        // 清空response
        response.reset();
        String rangBytes;
        long startIndex = 0; //下载开始的字节数
        long endIndex = 0; //下载结束的字节数
        long fileLength; //文件的字节数
        // 判断是不是分段上传
        if (range != null && range.trim().length() > 0) {
            rangBytes = range.replaceAll("bytes=", "");
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            // 判断最后是不是 -
            if (rangBytes.endsWith("-")) {
                //结尾是-,最后一次下载
                String startIndexStr = rangBytes.substring(0, rangBytes.indexOf("-"));
                if (!"".equals(startIndexStr)) {
                    startIndex = Long.parseLong(startIndexStr);
                }
                fileLength = file.length() - startIndex;
                String contentRange = "bytes " + startIndex + "-" + (file.length() - 1) + "/" + file.length();
                response.setHeader("Content-Range", contentRange);
                fileService.downloadFinish(downloadModel);
            } else { // 结尾不是-
                String startIndexStr = rangBytes.substring(0, rangBytes.indexOf("-"));
                if (!"".equals(startIndexStr)) {
                    startIndex = Long.parseLong(startIndexStr);
                }
                String endIndexStr = rangBytes.substring(rangBytes.indexOf("-") + 1);
                if (!"".equals(endIndexStr)) {
                    endIndex = Long.parseLong(endIndexStr);
                }
                fileLength = (endIndex + 1) - startIndex;
                String contentRange = range.replace("=", " ") + "/" + file.length();
                response.setHeader("Content-Range", contentRange);
                if ((file.length() - 1) == endIndex) {
                    fileService.downloadFinish(downloadModel);
                }
            }
        } else {
            //不是分段上传
            fileLength = file.length();
            String contentRange = "bytes " + "0-" + 0 + "/" + 0;
            response.setHeader("Content-Range", contentRange);
            // 下载成功,可计算下载结果
            fileService.downloadFinish(downloadModel);
        }
        // 获取文件类型
        String suffix = "";
        if (fileName.lastIndexOf(".") != -1) {
            suffix = fileName.substring(fileName.lastIndexOf("."));
        }
        response.setHeader("Content-Length", Long.toString(fileLength));
        //配置header
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String contentType = FileType.getFileType(suffix);
        //设置文件类型
        if (contentType != null) {
            response.setContentType(contentType);
        } else {
            response.setContentType("application/octet-stream");
        }
        // 文件输出
        try (RandomAccessFile loOutput = new RandomAccessFile(file, "r");
             OutputStream out = response.getOutputStream()) {
            //文件是否可以直接打开
            if (open == 0) {
                response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, "utf-8"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            }
            //设置开始下载的位置
            loOutput.seek(startIndex);
            //创建缓存大小
            byte[] buffer = new byte[constants.getFileCache()]; // 1KB
            int n;
            long readLength = 0;
            //判断是否指定结束位置
            if (endIndex != 0) {
                while (readLength <= fileLength - buffer.length) {
                    n = loOutput.read(buffer);
                    readLength += n;
                    out.write(buffer, 0, n);
                }
                if (readLength <= fileLength) {
                    n = loOutput.read(buffer, 0, (int) (fileLength - readLength));
                    out.write(buffer, 0, n);
                }
            } else {
                while ((n = loOutput.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String compressImage(File file, String fileName, Integer w, Integer h, Double s) {
        String path;
        if (s != null) {
            path = constants.getTempFolder().getPath() + File.separator + s + "-" + fileName + FileSuffix.COMPRESS.getSuffix();
        } else {
            path = constants.getTempFolder().getPath() + File.separator + w + "-" + h + "-" + fileName + FileSuffix.COMPRESS.getSuffix();
        }
        File temFile = new File(path);
        // 判断文件夹是否存在
        if (!temFile.getParentFile().exists() && !temFile.getParentFile().isDirectory()) {
            if (!temFile.getParentFile().mkdirs()) {
                throw FailCode.INIT_CREATE_TEMP_FOLDER_FAIL.getOperateException();
            }
        }
        //存在返回路径
        if (temFile.exists()) {
            return path;
        }
        // 检测本地硬盘是否有足够空间
        if ((temFile.getParentFile().getFreeSpace() - constants.getTempFolder().getReserveSpace()) < temFile.length()) {
            throw FailCode.COMPRESS_FAIL_SERVER_SPACE_SHORTAGE.getOperateException();
        }
        // 生成零时文件
        try (FileOutputStream os = new FileOutputStream(temFile)) {
            Thumbnails.Builder<File> builder = Thumbnails.of(file);
            if (s != null) {
                builder.scale(s);
            }
            if (w != null && h != null) {
                builder.size(w, h);
            } else if (w != null) {
                builder.width(w);
            } else if (h != null) {
                builder.height(h);
            }
            builder.toOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    public void downloadM3u8(HttpServletResponse response, DownloadModel downloadModel) {
        File file = getDownloadFile(downloadModel.getCode());
        // 下载文件
        String fileName = FileUtils.getNotSuffixFileName(downloadModel.getName());
        String path = FfmpegUtils.videoToM3u8(file, constants.getTempFolder().getPath() + File.separator + fileName, fileName);
        download(response, new File(path), downloadModel);
    }

    @Override
    public void downloadM3u8(HttpServletResponse response, DownloadModel downloadModel, InputStream inputStream) {
        String fileName = FileUtils.getNotSuffixFileName(downloadModel.getName());
        String path = FfmpegUtils.videoToM3u8(inputStream, constants.getTempFolder().getPath() + File.separator + fileName, fileName);
        download(response, new File(path), downloadModel);
    }

    @Override
    public void downloadM3u8(HttpServletResponse response, String fileName) {
        String name = FileUtils.getNotSuffixFileName(fileName);
        String filePath = constants.getTempFolder().getPath() + File.separator;
        if (name.contains("-")) {
            filePath += name.substring(0, name.indexOf("-"));
        } else {
            filePath += name;
        }
        File file = new File(filePath + File.separator + fileName);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        //配置header
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Access-Control-Allow-Origin", "*");
        //设置文件类型
        response.setContentType("application/octet-stream");
        // 文件输出
        try (RandomAccessFile loOutput = new RandomAccessFile(file, "r")) {
            //文件是否可以直接打开
            response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(file.getName(), "utf-8"));
            //返回的输出流
            OutputStream out = response.getOutputStream();
            //设置开始下载的位置
            loOutput.seek(0);
            //创建缓存大小
            byte[] buffer = new byte[constants.getFileCache()]; // 1KB
            int n;
            //判断是否指定结束位置
            while ((n = loOutput.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getDownloadFile(String code) {
        List<FolderConfig> folderPath = constants.getFolderPaths();
        for (FolderConfig folderConfig : folderPath) {
            List<File> fileList = FileUtil.loopFiles(folderConfig.getPath());
            for (File file : fileList) {
                if (file.getName().equals(code + FileSuffix.SUCCESS.getSuffix())) {
                    return file;
                }
            }
        }
        throw FailCode.DOWNLOAD_FILE_NOT_EXIST.getOperateException();
    }
}
