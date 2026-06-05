package com.xc.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xc.api.file.bean.*;
import com.xc.api.file.dto.FileDto;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.core.enums.EffectStatus;
import com.xc.file.bean.FileBean;
import com.xc.file.config.Constants;
import com.xc.file.entity.DiskEntity;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.FileSuffix;
import com.xc.file.mapper.FileHashMapper;
import com.xc.file.service.DownloadService;
import com.xc.file.service.FileHashService;
import com.xc.file.service.FileService;
import com.xc.file.service.UploadService;
import com.xc.file.utils.FfmpegUtils;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.Md5Utils;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * <p>文件hash信息Service实现</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Slf4j
@Service
public class FileHashServiceImpl extends ServiceImpl<FileHashMapper, FileHashEntity> implements FileHashService {
    @Autowired
    private Constants constants;
    @Lazy
    @Autowired
    private FileService fileService;
    @Autowired
    private BasicConstants basicConstants;
    @Lazy
    @Autowired
    private UploadService uploadService;
    @Lazy
    @Autowired
    private DownloadService downloadService;

    @Override
    public FileHashEntity getFileHash(String hashCode, String groupCode) {
        FileHashEntity fileHashEntity = new FileHashEntity();
        fileHashEntity.setHashCode(hashCode);
        fileHashEntity.setGroupCode(groupCode);
        return this.getOne(new QueryWrapper<>(fileHashEntity));
    }

    @Override
    public FileDto createFileHash(DiskEntity diskEntity, File tempFile, String path, String folderId, String fileName) {
        return createFileHash(diskEntity, tempFile, path, folderId, fileName, EffectStatus.VALID.getStatus());
    }

    @Override
    @Transactional
    public FileDto createFileHash(DiskEntity diskEntity, File tempFile, String path, String folderId, String fileName, String status) {
        String diskId = diskEntity.getId();
        String diskNo = diskEntity.getDiskNo();
        if (!tempFile.exists()) {
            throw FailCode.FILE_NOT_EXIST.getOperateException();
        }
        String hashCode = Md5Utils.getFileMd5(tempFile);
        FileHashEntity hashEntity = this.getFileHash(hashCode, diskId);
        if (hashEntity == null) {
            String code = StringUtils.generateOnlyId(constants.getMachineId());
            // 创建文件hash信息
            hashEntity = this.createFileHash(hashCode, diskId, tempFile.length(), code);
            // 重命名文件
            String newPath = path + File.separator + hashEntity.getCode() + FileSuffix.SUCCESS.getSuffix();
            File newFile = new File(newPath);
            uploadService.renameFile(tempFile, newFile);
        }
        // 创建文件，返回数据
        FileBean fileBean = new FileBean();
        fileBean.setName(fileName);
        fileBean.setFolderId(folderId);
        fileBean.setHashId(hashEntity.getId());
        fileBean.setSize(hashEntity.getSize());
        fileBean.setStatus(status);
        FileEntity fileEntity = fileService.createFile(diskId, fileBean, false);
        FileDto fileDto = ObjectUtils.convert(new FileDto(), fileEntity);
        // 写入下载地址
        fileDto.setUrl(hashEntity.getServerUrl() + constants.getOpenDiskUrl() + diskNo);
        fileDto.setCode(hashEntity.getCode());
        fileDto.setServerUrl(hashEntity.getServerUrl());
        return fileDto;
    }

    @Override
    public FileHashEntity createFileHash(String hashCode, String groupCode, Long size, String code) {
        FileHashEntity fileHash = new FileHashEntity();
        fileHash.setHashCode(hashCode);
        fileHash.setGroupCode(groupCode);
        fileHash.setSize(size);
        fileHash.setCode(code);
        fileHash.setServerUrl(constants.getLocalUrl());
        if (!this.save(fileHash)) {
            throw FailCode.CREATE_FILE_HASH_FAIL.getOperateException();
        }
        return fileHash;
    }

    @Override
    public File imageHandle(FileEntity fileEntity, ImageHandleBean imageHandleBean) {
        // 获取下载地址
        FileHashEntity fileHash = this.getById(fileEntity.getHashId());
        try {
            // 压缩图片临时路径
            if (constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                File file = downloadService.getDownloadFile(fileHash.getCode());
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    throw FailCode.FILE_FORMAT_ERROR.getOperateException();
                }
                return imageFileHandle(fileEntity, fileHash, ImageIO.read(file), imageHandleBean);
            } else {
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHash.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileHash.getHashCode());
                url = StringUtils.analysisParam(url, signBean);
                BufferedImage image = ImageIO.read(new URL(url));
                if (image == null) {
                    throw FailCode.FILE_FORMAT_ERROR.getOperateException();
                }
                return imageFileHandle(fileEntity, fileHash, image, imageHandleBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
        }
    }

    @Override
    public File videoToM3u8(FileEntity fileEntity, VideoHandleBean videoHandleBean) {
        String diskId = fileEntity.getDiskId();
        // 获取下载地址
        FileHashEntity fileHash = this.getById(fileEntity.getHashId());
        try {
            // 压缩图片临时路径
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFolder = constants.getTempFolder().getPath() + File.separator + uuid;
            String prefixName = videoHandleBean.getVideoM3u8().getPrefixName();
            // 创建该目录
            FileUtils.createFolder(newFolder);
            // 删除目录下所有文件
            FileUtils.deleteFolder(newFolder);
            File oldFile;
            if (constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                oldFile = downloadService.getDownloadFile(fileHash.getCode());
            } else {
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHash.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileHash.getHashCode());
                url = StringUtils.analysisParam(url, signBean);
                oldFile = new File(new URL(url).getFile());
            }
            if (videoHandleBean.getImageWatermark() != null ||
                    videoHandleBean.getImageCompress() != null ||
                    videoHandleBean.getImageCrop() != null) {
                oldFile = videoWatermark(diskId, fileHash.getHashCode(), oldFile, videoHandleBean);
            }
            String newFilePath = FfmpegUtils.videoToM3u8(oldFile, newFolder, prefixName);
            return new File(newFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
        }
    }

    @Override
    public File videoWatermark(FileEntity fileEntity, VideoHandleBean videoHandleBean) {
        String diskId = fileEntity.getDiskId();
        // 获取下载地址
        FileHashEntity fileHash = this.getById(fileEntity.getHashId());
        try {
            // 获取水印图片
            if (constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                File oldFile = downloadService.getDownloadFile(fileHash.getCode());
                return videoWatermark(diskId, fileHash.getHashCode(), oldFile, videoHandleBean);
            } else {
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHash.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileHash.getHashCode());
                url = StringUtils.analysisParam(url, signBean);
                return videoWatermark(diskId, fileHash.getHashCode(), new File(new URL(url).getFile()), videoHandleBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
        }
    }

    @Override
    public File audioConvert(FileEntity fileEntity, AudioHandleBean audioHandleBean) {
        try {
            // 获取下载地址
            FileHashEntity fileHash = this.getById(fileEntity.getHashId());
            // 获取水印图片
            if (constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                // 旧文件
                File oldFile = downloadService.getDownloadFile(fileHash.getCode());
                //新文件
                String tempPath = constants.getTempFolder().getPath() + File.separator + fileHash.getHashCode() + FileSuffix.AC.getSuffix();
                File newFile = new File(tempPath);
                FileUtils.createFolder(newFile.getParentFile().getPath());
                String toFormat = audioHandleBean.getAudioConvert().getToFormat();
                FfmpegUtils.audioConvert(oldFile, newFile, toFormat);
                return newFile;
            } else {
                // 旧文件
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHash.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileHash.getHashCode());
                url = StringUtils.analysisParam(url, signBean);
                File oldFile = new File(new URL(url).getFile());
                //新文件
                String tempPath = constants.getTempFolder().getPath() + File.separator + fileHash.getHashCode() + FileSuffix.AC.getSuffix();
                File newFile = new File(tempPath);
                FileUtils.createFolder(newFile.getParentFile().getPath());
                String toFormat = audioHandleBean.getAudioConvert().getToFormat();
                FfmpegUtils.audioConvert(oldFile, newFile, toFormat);
                return newFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
        }
    }

    /**
     * 视频水印
     *
     * @param diskId          磁盘主键
     * @param hashCode        文件hash
     * @param oldFile         文件数据
     * @param videoHandleBean 处理参数
     * @return 文件
     */
    private File videoWatermark(String diskId, String hashCode, File oldFile, VideoHandleBean videoHandleBean) {
        String tempPath = constants.getTempFolder().getPath() + File.separator + hashCode + FileSuffix.WATERMARK.getSuffix();
        File newFile = new File(tempPath);
        FileUtils.createFolder(newFile.getParentFile().getPath());
        // 绘制图片水印，方式二
        ImageWatermarkBean imageWatermarkBean = videoHandleBean.getImageWatermark();
        BufferedImage bufferedImage;
        if (imageWatermarkBean.getWatermarkPath() != null) {
            bufferedImage = getWatermarkImage(diskId, imageWatermarkBean.getWatermarkPath());
        } else {
            bufferedImage = null;
        }
        String format = FileUtils.getFileSuffix(videoHandleBean.getFilePath());
        return FfmpegUtils.videoWatermark(oldFile, newFile, format.substring(1), (buffImg) -> {
            try {
                Thumbnails.Builder<BufferedImage> builder = imageFileHandle(buffImg, bufferedImage, videoHandleBean.getImageCompress(), imageWatermarkBean, videoHandleBean.getImageCrop());
                builder.scale(1);
                // 重新合成视频
                return builder.asBufferedImage();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw FailCode.CREATE_WATERMARK_FAIL.getOperateException();
            }
        });
    }

    /**
     * 获取水印图片
     *
     * @param diskId        磁盘主键
     * @param watermarkPath 水印路劲
     * @return 水印图片
     */
    private BufferedImage getWatermarkImage(String diskId, String watermarkPath) {
        BufferedImage bufferedImage;
        try {
            FileEntity fileEntity = fileService.getFileByPath(diskId, watermarkPath, EffectStatus.VALID.getStatus());
            // 获取下载地址
            FileHashEntity fileHash = this.getById(fileEntity.getHashId());
            // 处理水印图片
            if (constants.getLocalUrl().equals(fileHash.getServerUrl())) {
                File file = downloadService.getDownloadFile(fileHash.getCode());
                bufferedImage = ImageIO.read(file);
            } else {
                SignBean signBean = new SignBean(basicConstants.getAppId(), basicConstants.getAppSecret(), FileRestCode.downloadHashFile.getCode());
                String url = fileHash.getServerUrl() + StringUtils.analysisPath(constants.getDownloadHashUrl(), fileHash.getHashCode());
                url = StringUtils.analysisParam(url, signBean);
                bufferedImage = ImageIO.read(new URL(url));
            }
        } catch (IOException e) {
            throw FailCode.FILE_DOWNLOAD_ERROR.getOperateException();
        }
        return bufferedImage;
    }

    /**
     * 处理图片
     *
     * @param fileEntity      文件信息
     * @param image           图片信息
     * @param imageHandleBean 参数
     * @return 处理后的文件
     */
    private File imageFileHandle(FileEntity fileEntity, FileHashEntity fileHash, BufferedImage image, ImageHandleBean imageHandleBean) {
        // 压缩图片临时路径
        String tempPath = constants.getTempFolder().getPath() + File.separator + fileHash.getCode() + FileSuffix.WATERMARK.getSuffix();
        File tempFile = new File(tempPath);
        FileUtils.createFolder(tempFile.getParentFile().getPath());
        // 生成零时文件
        ImageWatermarkBean imageWatermarkBean = imageHandleBean.getImageWatermark();
        BufferedImage bufferedImage = null;
        if (imageHandleBean.getImageWatermark() != null && imageWatermarkBean.getWatermarkPath() != null) {
            bufferedImage = getWatermarkImage(fileEntity.getDiskId(), imageWatermarkBean.getWatermarkPath());
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            Thumbnails.Builder<BufferedImage> builder = imageFileHandle(image, bufferedImage, imageHandleBean.getImageCompress(), imageWatermarkBean, imageHandleBean.getImageCrop());
            String suffix = FileUtils.getFileSuffix(fileEntity.getName());
            builder.outputFormat(suffix.substring(1));
            builder.toOutputStream(fileOutputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw FailCode.CREATE_WATERMARK_FAIL.getOperateException();
        }
        return tempFile;
    }

    /**
     * 图片处理
     *
     * @param image              原始图片
     * @param watermarkImage     水印图片
     * @param imageCompressBean  压缩配置
     * @param imageWatermarkBean 水印配置
     * @param imageCropBean      裁剪配置
     * @return 处理结果
     */
    private Thumbnails.Builder<BufferedImage> imageFileHandle(BufferedImage image, BufferedImage watermarkImage, ImageCompressBean imageCompressBean, ImageWatermarkBean imageWatermarkBean, ImageCropBean imageCropBean) {
        try {
            Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(image);
            // 裁剪图片
            if (imageCropBean != null) {
                if (imageCropBean.getWidth() != null && imageCropBean.getHeight() != null) {
                    int width = imageCropBean.getWidth();
                    int height = imageCropBean.getHeight();
                    if (imageCropBean.getPosition() == null) {
                        imageCropBean.setPosition("");
                    }
                    switch (imageCropBean.getPosition()) {
                        case "topLeft":
                            builder.sourceRegion(Positions.TOP_LEFT, width, height);
                            break;
                        case "topCenter":
                            builder.sourceRegion(Positions.TOP_CENTER, width, height);
                            break;
                        case "topRight":
                            builder.sourceRegion(Positions.TOP_RIGHT, width, height);
                            break;
                        case "centerLeft":
                            builder.sourceRegion(Positions.CENTER_LEFT, width, height);
                            break;
                        case "center":
                            builder.sourceRegion(Positions.CENTER, width, height);
                            break;
                        case "centerRight":
                            builder.sourceRegion(Positions.CENTER_RIGHT, width, height);
                            break;
                        case "bottomLeft":
                            builder.sourceRegion(Positions.BOTTOM_LEFT, width, height);
                            break;
                        case "bottomCenter":
                            builder.sourceRegion(Positions.BOTTOM_CENTER, width, height);
                            break;
                        case "bottomRight":
                            builder.sourceRegion(Positions.BOTTOM_RIGHT, width, height);
                            break;
                        default:
                            Position position = (i, i1, i2, i3, i4, i5, i6, i7) -> {
                                int positionX = 0;
                                if (imageCropBean.getPositionX() != null) {
                                    positionX = imageCropBean.getPositionX();
                                }
                                int positionY = 0;
                                if (imageCropBean.getPositionY() != null) {
                                    positionY = imageCropBean.getPositionY();
                                }
                                return new Point(positionX, positionY);
                            };
                            builder.sourceRegion(position, width, height);
                    }
                }
            }
            // 压缩图片
            if (imageCompressBean != null) {
                // 比例
                if (imageCompressBean.getScale() != null) {
                    builder.scale(imageCompressBean.getScale());
                }
                // 设置最大宽高
                if (imageCompressBean.getMaxHeight() != null) {
                    if (image.getWidth() >= image.getHeight()) {
                        builder.width(imageCompressBean.getMaxWidth());
                    }
                }
                if (imageCompressBean.getMaxWidth() != null) {
                    if (image.getHeight() >= image.getWidth()) {
                        builder.height(imageCompressBean.getMaxHeight());
                    }
                }
                // 宽高
                if (imageCompressBean.getWidth() != null && imageCompressBean.getHeight() != null) {
                    builder.forceSize(imageCompressBean.getWidth(), imageCompressBean.getHeight());
                } else if (imageCompressBean.getWidth() != null) {
                    builder.width(imageCompressBean.getWidth());
                } else if (imageCompressBean.getHeight() != null) {
                    builder.height(imageCompressBean.getHeight());
                }
                //质量
                if (imageCompressBean.getQuality() != null) {
                    builder.outputQuality(imageCompressBean.getQuality());
                }
                // 旋转
                if (imageCompressBean.getRotate() != null) {
                    builder.rotate(imageCompressBean.getRotate());
                }
            } else {
                builder.scale(1);
            }
            // 添加水印
            if (imageWatermarkBean != null) {
                if (imageWatermarkBean.getWatermarkPath() != null) {
                    Thumbnails.Builder<BufferedImage> watermarkBuilder = Thumbnails.of(watermarkImage).outputFormat("png");
                    // 相对于图片的比例
                    if (imageWatermarkBean.getWatermarkImageScale() != null) {
                        double scale = imageWatermarkBean.getWatermarkImageScale();
                        if (scale == 1) {
                            watermarkBuilder.size(watermarkImage.getWidth(), watermarkImage.getHeight());
                        } else {
                            int waterWidth = (int) (image.getWidth() * scale);
                            int waterHeight = waterWidth * watermarkImage.getHeight() / watermarkImage.getWidth();
                            watermarkBuilder.forceSize(waterWidth, waterHeight);
                        }
                    }
                    // 宽高
                    if (imageWatermarkBean.getWatermarkWidth() != null && imageWatermarkBean.getWatermarkHeight() != null) {
                        watermarkBuilder.forceSize(imageWatermarkBean.getWatermarkWidth(), imageWatermarkBean.getWatermarkHeight());
                    } else if (imageWatermarkBean.getWatermarkWidth() != null) {
                        watermarkBuilder.width(imageWatermarkBean.getWatermarkWidth());
                    } else if (imageWatermarkBean.getWatermarkHeight() != null) {
                        watermarkBuilder.height(imageWatermarkBean.getWatermarkHeight());
                    }
                    // 比例
                    if (imageWatermarkBean.getWatermarkScale() != null) {
                        watermarkBuilder.scale(imageWatermarkBean.getWatermarkScale());
                    }
                    // 旋转
                    if (imageWatermarkBean.getWatermarkRotate() != null) {
                        watermarkBuilder.rotate(imageWatermarkBean.getWatermarkRotate());
                    }
                    // 图片质量
                    if (imageWatermarkBean.getWatermarkQuality() != null) {
                        watermarkBuilder.outputQuality(imageWatermarkBean.getWatermarkQuality());
                    }
                    watermarkImage = watermarkBuilder.asBufferedImage();
                    // 处理透明度
                    float opacity = 1f;
                    if (imageWatermarkBean.getOpacity() != null) {
                        opacity = imageWatermarkBean.getOpacity();
                    }
                    // 处理位置
                    if (imageWatermarkBean.getPosition() == null) {
                        imageWatermarkBean.setPosition("");
                    }
                    Position position = (i, i1, i2, i3, i4, i5, i6, i7) -> {
                        Positions positions = null;
                        switch (imageWatermarkBean.getPosition()) {
                            case "topLeft":
                                positions = Positions.TOP_LEFT;
                                break;
                            case "topCenter":
                                positions = Positions.TOP_CENTER;
                                break;
                            case "topRight":
                                positions = Positions.TOP_RIGHT;
                                break;
                            case "centerLeft":
                                positions = Positions.CENTER_LEFT;
                                break;
                            case "center":
                                positions = Positions.CENTER;
                                break;
                            case "centerRight":
                                positions = Positions.CENTER_RIGHT;
                                break;
                            case "bottomLeft":
                                positions = Positions.BOTTOM_LEFT;
                                break;
                            case "bottomCenter":
                                positions = Positions.BOTTOM_CENTER;
                                break;
                            case "bottomRight":
                                positions = Positions.BOTTOM_RIGHT;
                                break;
                        }
                        Point point;
                        if (positions != null) {
                            point = positions.calculate(i, i1, i2, i3, i4, i5, i6, i7);
                            if (imageWatermarkBean.getPositionX() != null) {
                                point.setLocation(point.getX() + imageWatermarkBean.getPositionX(), point.getY());
                            }
                            if (imageWatermarkBean.getPositionY() != null) {
                                point.setLocation(point.getX(), point.getY() + imageWatermarkBean.getPositionY());
                            }
                        } else {
                            int positionX = 0;
                            if (imageWatermarkBean.getPositionX() != null) {
                                positionX = imageWatermarkBean.getPositionX();
                            }
                            int positionY = 0;
                            if (imageWatermarkBean.getPositionY() != null) {
                                positionY = imageWatermarkBean.getPositionY();
                            }
                            point = new Point(positionX, positionY);
                        }
                        return point;
                    };
                    builder.watermark(position, watermarkImage, opacity);
                }
            }
            return builder;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw FailCode.CREATE_WATERMARK_FAIL.getOperateException();
        }
    }

}
