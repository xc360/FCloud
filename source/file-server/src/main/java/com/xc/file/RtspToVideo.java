package com.xc.file;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RtspToVideo {
    // 录制时长配置（毫秒）
    private static final long RECORD_DURATION = 5 * 60 * 1000; // 5 分钟
    // 保存多长时间的录像
    private static final long MAX_AGE_MILLIS = 7 * 24 * 60 * 60 * 1000; // 7 天

    public static void main(String[] args) {
        new Thread(() -> {
            init("rtsp://admin:zZ925988564@192.168.2.124:554/stream1", "C:\\home\\stream1");
        }).start();
        new Thread(() -> {
            init("rtsp://admin:zZ925988564@192.168.2.124:554/stream1&channel=2", "C:\\home\\stream2");
        }).start();
    }

    private static void init(String rtspUrl, String outputDir) {
        // 确保输出目录存在
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 读取流
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.start();
            while (true) {
                // 删除之前的文件
                deleteOldFiles(outputDir);
                // 拉取新的数据流
                String outputFilePath = generateOutputFileName(outputDir);
                setFile(grabber, outputFilePath);
            }
        } catch (FrameGrabber.Exception e) {
            System.err.println("✗ 错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteOldFiles(String outputDir) {
        File dir = new File(outputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".mkv"));
        if (files == null || files.length == 0) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        int deletedCount = 0;

        for (File file : files) {
            if (currentTime - file.lastModified() > MAX_AGE_MILLIS) {
                if (file.delete()) {
                    deletedCount++;
                    System.out.println("已删除旧文件：" + file.getName());
                }
            }
        }

        if (deletedCount > 0) {
            System.out.println("清理完成，共删除 " + deletedCount + " 个文件");
        }
    }

    private static void setFile(FFmpegFrameGrabber grabber, String outputFilePath) {
        // 源视频参数
        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();
        double frameRate = grabber.getVideoFrameRate();
        int audioChannels = grabber.getAudioChannels();
        int sampleRate = grabber.getSampleRate();
        System.out.println("源视频参数 - 宽：" + width + ", 高：" + height + ", 帧率：" + frameRate);
        //  打开写入通道
        try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFilePath, width, height)) {
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameRate > 0 ? frameRate : 25.0);
            recorder.setGopSize((int) (frameRate > 0 ? frameRate : 25.0));
            recorder.setVideoBitrate(2500000);

            if (audioChannels > 0) {
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.setAudioChannels(audioChannels);
                recorder.setSampleRate(sampleRate > 0 ? sampleRate : 44100);
                recorder.setAudioBitrate(128000);
            }

            System.out.println("开始录制...");
            recorder.start();

            Frame frame;
            long frameCount = 0;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + RECORD_DURATION;
            while (System.currentTimeMillis() < endTime && (frame = grabber.grab()) != null) {
                if (frame.image != null) {
                    recorder.record(frame);
                    frameCount++;

                    if (frameCount % 100 == 0) {
                        System.out.println("已录制：" + frameCount + "帧，耗时：" + (System.currentTimeMillis() - startTime) + "ms");
                    }
                } else if (frame.samples != null && audioChannels > 0) {
                    recorder.record(frame);
                }
            }
            recorder.close();
            System.out.println("✓ 录制完成！文件：" + outputFilePath + ", 总帧数：" + frameCount);
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            System.err.println("✗ 错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成输出文件名（带时间戳）
     */
    private static String generateOutputFileName(String outputDir) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return outputDir + "\\recording_" + timestamp + ".mp4";
    }
}
