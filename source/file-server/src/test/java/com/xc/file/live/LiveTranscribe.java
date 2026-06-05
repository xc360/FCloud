package com.xc.file.live;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.File;

/**
 * <p>
 * 直播推送
 * </p>
 *
 * @author xc
 * @since 2023-06-15
 */
@Slf4j
public class LiveTranscribe {
    /**
     * 输入对象
     */
    private FrameGrabber grabber;
    /**
     * 本地记录对象
     */
    private FFmpegFrameRecorder fileRecorder;
    /**
     * 直播推流对象
     */
    private FFmpegFrameRecorder recorder;
    /**
     * 用于显示推送视频
     */
    private ImageView imageVideo;
    /**
     * 推送的帧率
     */
    private int frameRate = 30;
    /**
     * 是否停止
     */
    private boolean isStop = true;
    /**
     * 直播推流地址
     */
    private String plugFlowPath;
    /**
     * 输入地址
     */
    private String inputPath;
    /**
     * 本地存储地址
     */
    private String localPath;

    public void setPlugFlowPath(String plugFlowPath) {
        this.plugFlowPath = plugFlowPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    /**
     * 初始化配置
     */
    public void setConfig(ImageView imageVideo) {
        try {
            this.imageVideo = imageVideo;
            plugFlowPath = "rtmp://127.0.0.1:1935/live/stream";
            inputPath = "C:\\Users\\xc\\Videos\\003.mp4";
            localPath = "D://output.mp4";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始推送
     */
    public void startTranscribe() {
        if (!isStop) {
            return;
        }
        isStop = false;
        try {
            /*****************设置视频配置*****************/
            grabber = new FFmpegFrameGrabber(inputPath);
            // 开始
            grabber.start();

            /*****************设置本地存储配置*****************/
            if (localPath != null) {
                File localFile = new File(localPath);
                if (localFile.exists()) {
                    localFile.delete();
                }
                fileRecorder = FFmpegFrameRecorder.createDefault(localFile, grabber.getImageWidth(), grabber.getImageHeight());
                fileRecorder.setInterleaved(true);
                // 该参数用于降低延迟
                fileRecorder.setVideoOption("tune", "zerolatency");
                // 设置参数
                setOutParam(fileRecorder);
                // 视频格式
                fileRecorder.setFormat("mp4");
                // 开始
                fileRecorder.start();
            }

            /*****************设置直播推流配置*****************/
            if (plugFlowPath != null) {
                recorder = new FFmpegFrameRecorder(plugFlowPath, grabber.getImageWidth(), grabber.getImageHeight(), 2);
                recorder.setInterleaved(true);
                // 该参数用于降低延迟
                recorder.setVideoOption("tune", "zerolatency");
                // 设置参数
                setOutParam(recorder);
                // 视频格式
                recorder.setFormat("flv");
                // 开始
                recorder.start();
            }
            videoTranscribe();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始推送
     */
    public void stopTranscribe() {
        try {
            isStop = true;
            Thread.sleep(500);
            if (fileRecorder != null) {
                fileRecorder.stop();
                fileRecorder.release(); // 释放内存，我们都知道c/c++需要手动释放资源
            }
            if (recorder != null) {
                recorder.stop();
                recorder.release(); // 释放内存，我们都知道c/c++需要手动释放资源
            }
            if (grabber != null) {
                grabber.stop();
                grabber.release();  // 释放内存，我们都知道c/c++需要手动释放资源
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置推送参数
     */
    private void setOutParam(FFmpegFrameRecorder recorder) {
        // 设置推送的帧率
        recorder.setFrameRate(frameRate);
        // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(frameRate * 2);
        // 像素格式
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

        /************添加视频推送************/
        // 降低延迟
        recorder.setVideoOption("tune", "zerolatency");
        // 权衡quality(视频质量)和encode speed(编码速度) values(值)：
        // ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快),
        // medium(中等), slow(慢), slower(很慢), veryslow(非常慢)
        // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
        recorder.setVideoOption("preset", "ultrafast");
        // 编码，使用编码能让视频占用内存更小，根据实际自行选择
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setVideoOption("crf", "28");
        // 2500 kb/s
        recorder.setVideoBitrate(2500000);

        /************添加音频推送************/

        // 不可变音频
        recorder.setAudioOption("crf", "0");
        // 最高音质
        recorder.setAudioQuality(0);
        // 音频比特率
        recorder.setAudioBitrate(192000);
        // 音频采样率
        recorder.setSampleRate(44100);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        // 音频编/解码器
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
    }

    /**
     * 视频推送
     */
    private void videoTranscribe() {
        new Thread(() -> {
            try {
                // 获取屏幕捕捉的一帧
                Frame frame;
                // 屏幕推送，由于已经对音频进行了记录，需要对记录时间进行调整即可
                // 即上面调用了 recorder.recordSamples 需要重新分配时间，否则视频输出时长等于实际 的2倍
                while ((frame = grabber.grab()) != null) {
                    if (isStop) {
                        return;
                    }
                    // 将这帧放到推送
                    if (recorder != null) {
                        recorder.record(frame);
                    }
                    if (fileRecorder != null) {
                        fileRecorder.record(frame);
                    }
                    if (imageVideo != null) {
                        if (frame.image != null) {
                            Image convert = new JavaFXFrameConverter().convert(frame);
                            imageVideo.setImage(convert);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }).start();
    }
}
