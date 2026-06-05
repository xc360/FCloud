package com.xc.file.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.MemoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * <p>
 * javacv ffmpeg 工具类
 * </p>
 *
 * @author xc
 * @since 2023-05-12
 */
@Slf4j
public class FfmpegUtils {
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
    // 格式参数Map：key=格式，value=[编码器名称, 采样率, 声道数, 比特率, 输出格式名]
    private static final Map<String, Object[]> FORMAT_PARAMS = new HashMap<>();

    static {
        // 数组顺序：[编码器名称, 采样率, 声道数, 比特率, 输出格式名]
        FORMAT_PARAMS.put("mp3", new Object[]{"libmp3lame", 44100, 2, 128000, "mp3"});
        FORMAT_PARAMS.put("aac", new Object[]{"aac", 44100, 2, 128000, "aac"});
        FORMAT_PARAMS.put("amr", new Object[]{"amr_nb", 8000, 1, 12200, "amr"});
        FORMAT_PARAMS.put("flac", new Object[]{"flac", 44100, 2, 1024000, "flac"});
        FORMAT_PARAMS.put("wav", new Object[]{"pcm_s16le", 44100, 2, 1411200, "wav"});
        FORMAT_PARAMS.put("voskwav", new Object[]{"pcm_s16le", 16000, 1, 256000, "wav"});
    }

    /**
     * 视频转m3u8
     *
     * @param file       需要转换文件
     * @param toFilePath 转换输出路径
     * @param fileName   文件名称
     */
    public static String videoToM3u8(File file, String toFilePath, String fileName) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return videoToM3u8(inputStream, toFilePath, fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 视频转m3u8
     *
     * @param inputStream 需要转换文件流
     * @param toFilePath  转换输出路径
     * @param fileName    文件名称
     */
    public static String videoToM3u8(InputStream inputStream, String toFilePath, String fileName) {
        File tempFile3 = new File(toFilePath, fileName + ".m3u8");
        FFmpegLogCallback.set();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
            grabber.start();
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile3, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                // 加载文件
                String prefixName = toFilePath + File.separator + fileName;
                // 生成加密key
                String secureFileName = prefixName + ".key";
                byte[] secureRandom = getSecureRandom();
                FileUtil.writeBytes(secureRandom, secureFileName);
                String toHex = Convert.toHex(secureRandom);
                String keyInfoPath = toFilePath + File.separator + fileName + ".keyinfo";
                // 写入加密文件
                writeKeyInfo(keyInfoPath, fileName + ".key", secureFileName, toHex);
                // 格式方式
                recorder.setFormat("hls");
                // 关于hls_wrap的说明，hls_wrap表示重复覆盖之前ts切片，这是一个过时配置，ffmpeg官方推荐使用hls_list_size 和hls_flags delete_segments代替hls_wrap
                // 设置单个ts切片的时间长度（以秒为单位）。默认值为2秒
                recorder.setOption("hls_time", "10");
                // HLS类型
                recorder.setOption("hls_playlist_type", "vod");
                // 不根据gop间隔进行切片,强制使用hls_time时间进行切割ts分片
                recorder.setOption("hls_flags", "split_by_time");
                // 设置播放列表条目的最大数量。如果设置为0，则列表文件将包含所有片段，默认值为5
                // 当切片的时间不受控制时，切片数量太小，就会有卡顿的现象
                recorder.setOption("hls_list_size", "0");
                // 指定ts切片生成名称规则，按数字序号生成切片,例如'file%03d.ts'，就会生成file000.ts，file001.ts，file002.ts等切片文件
                recorder.setOption("hls_segment_filename", toFilePath + File.separator + fileName + "-%5d.ts");

                // 自动删除切片，如果切片数量大于hls_list_size的数量，则会开始自动删除之前的ts切片，只保留hls_list_size个数量的切片，hls_list_size不能为0
                recorder.setOption("hls_flags", "delete_segments");
                // ts切片自动删除阈值，默认值为1，表示早于hls_list_size+1的切片将被删除
                recorder.setOption("hls_delete_threshold", "1");
                /**
                 * hls的切片类型：
                 * 'mpegts'：以MPEG-2传输流格式输出ts切片文件，可以与所有HLS版本兼容。
                 * 'fmp4':以Fragmented MP4(简称：fmp4)格式输出切片文件，类似于MPEG-DASH，fmp4文件可用于HLS version 7和更高版本。
                 */
                recorder.setOption("hls_segment_type", "mpegts");
                //加密
                recorder.setOption("hls_key_info_file", keyInfoPath);
                // 设置零延迟
                recorder.setVideoOption("tune", "fastdecode");
                // 快速
                recorder.setVideoOption("preset", "ultrafast");
                recorder.setVideoOption("threads", "12");
                recorder.setVideoOption("vsync", "2");
                recorder.setFrameRate(grabber.getFrameRate());// 设置帧率
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                recorder.start(grabber.getFormatContext());
                AVPacket packet;
                while ((packet = grabber.grabPacket()) != null) {
                    recorder.recordPacket(packet);
                }
                recorder.setTimestamp(grabber.getTimestamp());
                recorder.stop();
                recorder.release();
            }
            grabber.stop();
            grabber.release();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return tempFile3.getAbsolutePath();
    }

    /**
     * 安全安全随机
     *
     * @return {@link byte[]}
     */
    public static byte[] getSecureRandom() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * 写入关键文件数据
     *
     * @param keyInfoPath 路径
     * @param decrypt     解密
     * @param encrypt     加密
     */
    public static void writeKeyInfo(String keyInfoPath, String decrypt, String encrypt, String IV) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(keyInfoPath));) {
            writer.write(decrypt);
            writer.newLine();
            writer.write(encrypt);
            writer.newLine();
            if (StringUtils.isNotBlank(IV)) {
                writer.write(IV);
            }
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 视频添加水印
     *
     * @param tempFile 零时文件
     * @param newFile  新文件
     * @param function 视频帧图片处理
     * @return 完成的视频文件，为空表示失败
     */
    public static File videoWatermark(File tempFile, File newFile, String format, Function<BufferedImage, BufferedImage> function) {
        // 抓取视频资源
        FFmpegLogCallback.set();
        // 抓取视频资源
        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(tempFile)) {
            frameGrabber.start();
            // 创建存储水印视频的文件
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(newFile, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels())) {
                // 设置时间搓
                recorder.setTimestamp(frameGrabber.getTimestamp());
                // 设置帧率
                recorder.setFrameRate(frameGrabber.getFrameRate());
                // 设置视频比特率
                recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
                // 设置视频格式
                recorder.setFormat(format);
                // 设置视频编码
                recorder.setVideoCodec(frameGrabber.getVideoCodec());
                // 设置音频编码器
                recorder.setAudioCodec(frameGrabber.getAudioCodec());
                // 设置音频采样率
                recorder.setSampleRate(frameGrabber.getSampleRate());
                // 设置音频比特率
                recorder.setAudioBitrate(frameGrabber.getAudioBitrate());
                // 最大延时
                recorder.setMaxDelay(frameGrabber.getMaxDelay());
                // 帧号
                recorder.setFrameNumber(frameGrabber.getFrameNumber());
                // 宽高比
                recorder.setAspectRatio(frameGrabber.getAspectRatio());
                // 开启录制器
                recorder.start();
                int index = 0;
                while (true) {
                    Frame frame = frameGrabber.grabFrame();
                    if (frame == null) {
                        log.info("视频处理完成，帧值：{}，原文件：{}，新文件：{}，进度：100", index, tempFile.getName(), newFile.getName());
                        break;
                    }
                    //判断图片帧
                    if (frame.image != null) {
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        BufferedImage buffImg = converter.convert(frame);
                        buffImg = function.apply(buffImg);
                        // 重新合成视频
                        recorder.record(converter.convert(buffImg));
                    }
                    //设置音频
                    if (frame.samples != null) {
                        recorder.recordSamples(frame.sampleRate, frame.audioChannels, frame.samples);
                    }
                    if (index % 1000 == 0) {
                        int totalFrame = frameGrabber.getLengthInAudioFrames() + frameGrabber.getLengthInVideoFrames();
                        Integer schedule = index / (totalFrame / 100);
                        log.info("帧值：{}，原文件：{}，新文件：{}，进度：{}", index, tempFile.getName(), newFile.getName(), schedule);
                        MemoryUtils.threadWatchMemoryInfo(1, 0, log::info);
                    }
                    index++;
                }
                recorder.stop();
                recorder.release();
            }
            frameGrabber.stop();
            frameGrabber.release();
        } catch (Exception e) {
            log.info("加水印过程中出现异常！");
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * m3u8转mp4格式
     *
     * @param inputFile  m3u8文件
     * @param outputPath 输出文件路劲
     */
    public static String m3u8ToMp4(File inputFile, String outputPath) {
        String filePath = m3u8FileHandle(inputFile);
        m3u8ToMp4(filePath, outputPath);
        FileUtils.deleteFile(filePath);
        return outputPath;
    }

    /**
     * m3u8转mp4格式
     *
     * @param url        m3u8的url地址
     * @param outputPath 输出文件路劲
     */
    public static String m3u8ToMp4(String url, String outputPath) {
        long startTime = System.currentTimeMillis();
        try {
            List<String> command = new ArrayList<>();
            //获取JavaCV中的ffmpeg本地库的调用路径
            String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
            command.add(ffmpeg);
            // 设置支持的网络协议
            command.add("-allowed_extensions");
            command.add("ALL");
            command.add("-y");
            command.add("-protocol_whitelist");
            command.add("concat,file,http,https,tcp,tls,crypto");
            command.add("-i");
            command.add(url);
            command.add(outputPath);
            Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
            fixedThreadPool.execute(() -> {
                try (InputStream is = videoProcess.getErrorStream()) {
                    while (true) {
                        int ch = is.read();
                        if (ch != -1) {
                            System.out.print((char) ch);
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            fixedThreadPool.execute(() -> {
                try (InputStream is = videoProcess.getInputStream()) {
                    while (true) {
                        int ch = is.read();
                        if (ch != -1) {
                            System.out.print((char) ch);
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            videoProcess.waitFor();
            return outputPath;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            log.info("转换已完成，生成文件：" + outputPath);
            long endTime = System.currentTimeMillis();
            log.info("用时:" + (int) ((endTime - startTime) / 1000) + "秒");
        }
    }

    /**
     * m3u8转mp4格式
     *
     * @param inputFile 需要处理的文件对象
     */
    public static String m3u8FileHandle(File inputFile) {
        String inputPath = inputFile.getPath();
        String name = FileUtils.getNotSuffixFileName(inputPath.replace(File.separator, "/"));
        String path = FileUtils.getFilePath(inputPath.replace(File.separator, "/"));
        StringBuilder content = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(inputFile.toPath()));
             BufferedReader in = new BufferedReader(reader)) {
            String line;
            while ((line = in.readLine()) != null) {
                String outPath = (path + "/" + name).replace("/", File.separator);
                String keyPath = outPath.replace(File.separator, "\\\\");
                if (line.contains(name) && line.contains(".key") && !line.contains(keyPath)) {
                    line = line.replace(name, keyPath);
                    content.append(line).append("\n");
                } else if (line.contains(name) && line.contains(".ts") && !line.contains(outPath)) {
                    line = line.replace(name, outPath);
                    content.append(line).append("\n");
                } else {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        String fileOutPath = inputFile.getParentFile().getPath() + File.separator + name + "-temp" + FileUtils.getFileSuffix(inputPath);
        FileUtils.deleteFile(fileOutPath);
        try (BufferedWriter output = new BufferedWriter(new FileWriter(fileOutPath, true))) {
            output.write(content.toString());
            output.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return fileOutPath;
    }

    /**
     * 音频转换
     *
     * @param inFile   原始音频文件（原始格式：mp3/aac/amr/flac/wav）
     * @param outFile  输出文件
     * @param toFormat 目标格式：mp3/aac/amr/flac/wav/voskwav
     */
    public static void audioConvert(File inFile, File outFile, String toFormat) {
        Object[] params = FORMAT_PARAMS.get(toFormat);
        String codecName = (String) params[0]; // 编码器名称（1.5.4 推荐按名称查找）
        int sampleRate = (int) params[1];      // 采样率
        int channels = (int) params[2];        // 声道数
        int bitrate = (int) params[3];         // 比特率
        String outputFormat = (String) params[4]; // 输出格式
        // 1.5.4 构造器仅传：输出文件 + 声道数（避免触发视频逻辑）
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inFile);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outFile, channels)) {
            // 启动抓取器（自动解码 WebM Opus 编码）
            grabber.start();
            // 关键配置：仅设置音频相关参数，明确告知是音频转码
            recorder.setFormat(outputFormat);        // 输出格式（音频）
            recorder.setAudioCodecName(codecName);   // 音频编码器（按名称设置，无歧义）
            recorder.setSampleRate(sampleRate); // 音频采样率
            recorder.setAudioChannels(channels);     // 音频声道数
            recorder.setAudioBitrate(bitrate);       // 音频比特率
            // 提前校验音频编码器（避免启动失败）
            if (avcodec.avcodec_find_encoder_by_name(codecName) == null) {
                throw new RuntimeException("找不到音频编码器：" + codecName + "（格式：" + toFormat + "）");
            }
            recorder.start(); // 启动音频录制器
            // 逐帧处理：仅录制音频帧（过滤视频帧）
            Frame frame;
            while ((frame = grabber.grabSamples()) != null) {
                if (frame.samples != null) { // samples 不为空 → 音频帧
                    recorder.record(frame);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        File inFile = new File("D:\\zpc\\xc\\FCloud\\xc-file\\file-server\\src\\main\\java\\com\\xc\\file\\utils\\202512221738102482.webm");
        File outFile = new File("D:\\zpc\\xc\\FCloud\\xc-file\\file-server\\src\\main\\java\\com\\xc\\file\\utils\\demo.wav");
        FfmpegUtils.audioConvert(inFile, outFile, "voskwav");
    }
}

