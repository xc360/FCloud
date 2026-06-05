package com.xc.file.enums;

import java.io.File;

/**
 * <p>文件后缀</p>
 *
 * @author xc
 * @version 1.0.0
 */
public enum FileSuffix {
    PACK(".pack"), //打包临时文件后缀，正式文件目录
    UPLOAD(".upload"),// 上传中的文件后缀，正式文件目录
    SUCCESS(".xc"), //上传成功后文件的后缀，正式文件目录
    COMPRESS(".compress"), // 压缩文件后缀，临时文件目录
    WATERMARK(".watermark"), // 水印文件后缀，临时文件目录
    M3U8(".m3u8"), // 水印文件后缀，临时文件目录
    AC(".ac"), // 音频转换临时文件目录
    ;


    /**
     * 错误code
     */
    private final String suffix;

    FileSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
