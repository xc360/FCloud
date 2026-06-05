package com.xc.file.live;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

/**
 * <p>
 *
 * </p>
 *
 * @author xc
 * @since 2023-06-15
 */
public class RecordVideoThread {
    public static void main(String[] args) {
        String RtspURL = "rtsp://192.168.17.88:554/live/ch00_0";// RTSP流地址（1中获取的）
        String RtmpUrl = "rtmp://127.0.0.1:1935/live/test";  //（RTMP地址）
        push(RtspURL, RtmpUrl);
    }

    public static void push(String RtspURL, String RtmpUrl) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(RtspURL)) {
            //发起请求
            grabber.start();
            // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(RtmpUrl, 1080, 1440, 1)) {
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);// 直播流格式
                recorder.setFormat("flv");// 录制的视频格式
                recorder.setFrameRate(25);// 帧数
                //百度翻译的比特率，默认400000，但是我400000贼模糊，调成800000比较合适
                recorder.setVideoBitrate(800000);
                System.out.println("开始保存视频");
                recorder.start();
                Frame frame = grabber.grabFrame();
                while ((frame != null)) {
                    recorder.record(frame);// 推流
                    frame = grabber.grabFrame();// 获取下一帧
                }
                recorder.record(frame);
                // 停止录制
                recorder.stop();
                recorder.release();
            }
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

}
