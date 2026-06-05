package com.xc.file;

import com.xc.api.file.bean.*;
import com.xc.file.entity.DiskEntity;
import com.xc.file.service.DiskService;
import com.xc.file.service.FileService;
import com.xc.file.utils.FfmpegUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class FileServerApplicationTests {

    @Autowired
    private DiskService diskService;
    @Autowired
    private FileService fileService;

    @Test
    public void videoHandle() {
        VideoHandleBean videoHandleBean = new VideoHandleBean();
        videoHandleBean.setFilePath("/BCloud/白玛拉姆-玉麦我的家.mp4");
//        videoHandleBean.setFilePath("/BCloud/001.mp4");
        videoHandleBean.setNewFilePath("/BCloud/m3u8/001.m3u8");
//        videoHandleBean.setNewFilePath("/BCloud/001.mp4");

        VideoM3u8Bean videoM3u8Bean = new VideoM3u8Bean();
        videoM3u8Bean.setPrefixName("001");
        videoHandleBean.setVideoM3u8(videoM3u8Bean);

//        ImageWatermarkBean videoWatermarkBean = new ImageWatermarkBean();
//        videoWatermarkBean.setWatermarkPath("/BCloud/logo.png");
//        videoWatermarkBean.setPosition("topRight");
//        videoWatermarkBean.setPositionX(-20);
//        videoWatermarkBean.setPositionY(20);
//        videoWatermarkBean.setScale(0.1);
//        videoHandleBean.setImageWatermark(videoWatermarkBean);

        DiskEntity diskEntity = diskService.getDiskByDiskNo("DN1086392460");
        fileService.videoHandle(null, diskEntity, videoHandleBean, "/open/disk/DN1086392460/video_handle");
    }

    @Test
    public void imageHandle() {
        ImageHandleBean imageHandleBean = new ImageHandleBean();
//        imageHandleBean.setFilePath("/BCloud/20240625211115.png");
//        imageHandleBean.setNewFilePath("/BCloud/20240625211115-1.png");
        imageHandleBean.setFilePath("/BCloud/20240408151034.jpg");
        imageHandleBean.setNewFilePath("/BCloud/20240408151034-1.jpg");


//        ImageWatermarkBean videoWatermarkBean = new ImageWatermarkBean();
//        videoWatermarkBean.setWatermarkPath("/BCloud/watermark/watermark_icon.png");
//        videoWatermarkBean.setPosition("topRight");
//        videoWatermarkBean.setPositionX(-20);
//        videoWatermarkBean.setPositionY(20);
////        videoWatermarkBean.setWatermarkScale(0.2);
//        videoWatermarkBean.setWatermarkImageScale(null);
//        videoWatermarkBean.setWatermarkWidth(200);
//        videoWatermarkBean.setWatermarkHeight(200);
//        imageHandleBean.setImageWatermark(videoWatermarkBean);
        ImageCompressBean imageCompressBean = new ImageCompressBean();
        imageCompressBean.setMaxWidth(1200);
        imageCompressBean.setMaxHeight(900);
        // 封面大小：520*416px
        // 图片大小：1080*900px
        imageHandleBean.setImageCompress(imageCompressBean);
        DiskEntity diskEntity = diskService.getDiskByDiskNo("DN1308836235");
        fileService.imageHandle(null, diskEntity, imageHandleBean, "/open/disk/DN1086392460/video_handle");
    }

    public static void main(String[] args) {
        String url = "http://1256193465.vod2.myqcloud.com/542c5c74vodtranscq1256193465/4041a1f85285890792596915252/v.f230.m3u8";
        String outputPath = "D:\\temp\\test\\001.mp4";
        FfmpegUtils.m3u8ToMp4(url, outputPath);
    }
}
