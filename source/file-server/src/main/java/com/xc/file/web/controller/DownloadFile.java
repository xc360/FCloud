package com.xc.file.web.controller;

import com.xc.api.basic.BasicApi;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.annotation.CloseRequestLog;
import com.xc.core.aspect.BasicConstants;
import com.xc.core.bean.SignBean;
import com.xc.file.bean.DownloadFileBean;
import com.xc.file.config.Constants;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.FileSuffix;
import com.xc.file.enums.RedisPrefix;
import com.xc.file.model.DownloadModel;
import com.xc.file.service.DownloadService;
import com.xc.file.service.FileHashService;
import com.xc.file.service.FileService;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>【页面】下载文件</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【页面】下载文件")
@Controller
@Slf4j
public class DownloadFile {

    @Autowired
    private DownloadService downloadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private Constants constants;
    @Autowired
    private FileHashService fileHashService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BasicConstants basicConstants;
    @Autowired
    private BasicApi basicApi;

    @ApiOperation(value = "下载当前磁盘的文件",
            notes = "1，下载文件必须符合下载条件，在安全链接允许范围内可下载\n" +
                    "2，宽高和比例不能同时使用，同时存在使用比例")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件id", name = "fid", paramType = "path", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true),
            @ApiImplicitParam(value = "open是否直接打开,0:可以直接打开，1:不能直接打开，默认下载", name = "open", paramType = "query"),
            @ApiImplicitParam(value = "压缩图片高", name = "w", paramType = "query"),
            @ApiImplicitParam(value = "压缩图片宽", name = "h", paramType = "query"),
            @ApiImplicitParam(value = "压缩比例,小数", name = "s", paramType = "query"),
    })
    @GetMapping("/disk/{diskId}/file/{fid}")
    @CloseRequestLog
    public void downloadDiskFile(@PathVariable String diskId, @PathVariable String fid,
                                 @RequestParam(value = "open", required = false) Integer open,
                                 @RequestParam(value = "w", required = false) Integer w,
                                 @RequestParam(value = "h", required = false) Integer h,
                                 @RequestParam(value = "s", required = false) Double s,
                                 HttpServletRequest request, HttpServletResponse response) {
        //去除文件后缀
        fid = FileUtils.getNotSuffixFileName(fid);
        if (fid == null) {
            throw FailCode.FIXED_NOT_NULL.getOperateException();
        }
        DownloadModel downloadModel = fileService.getDownloadModel(diskId, fid, RedisPrefix.USER_DOWNLOAD.getKey());
        downloadModel.setOpen(open != null ? open : 1);
        downloadModel.setRange(request.getHeader("range"));
        downloadService.downloadFile(response, downloadModel, w, h, s);
    }

    @ApiOperation(value = "根据fid下载文件",
            notes = "1，下载文件必须符合下载条件，在安全链接允许范围内可下载\n" +
                    "2，宽高和比例不能同时使用，同时存在使用比例")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件id", name = "fid", paramType = "path", required = true),
            @ApiImplicitParam(value = "共享", name = "shareId", paramType = "path", required = true),
            @ApiImplicitParam(value = "open是否直接打开,0:可以直接打开，1:不能直接打开，默认下载", name = "open", paramType = "query"),
            @ApiImplicitParam(value = "压缩图片高", name = "w", paramType = "query"),
            @ApiImplicitParam(value = "压缩图片宽", name = "h", paramType = "query"),
            @ApiImplicitParam(value = "压缩比例,小数", name = "s", paramType = "query"),
    })
    @GetMapping("/share/{visitCode}/file/{fid}")
    @CloseRequestLog
    public void downloadShareFile(@PathVariable String visitCode, @PathVariable String fid,
                                  @RequestParam(value = "open", required = false) Integer open,
                                  @RequestParam(value = "w", required = false) Integer w,
                                  @RequestParam(value = "h", required = false) Integer h,
                                  @RequestParam(value = "s", required = false) Double s,
                                  HttpServletRequest request, HttpServletResponse response) {
        //去除文件后缀
        fid = FileUtils.getNotSuffixFileName(fid);
        if (fid == null) {
            throw FailCode.FIXED_NOT_NULL.getOperateException();
        }
        //验证文件
        DownloadModel downloadModel = fileService.getDownloadModel(visitCode, fid, RedisPrefix.SHARE_DOWNLOAD.getKey());
        downloadModel.setRange(request.getHeader("range"));
        downloadModel.setOpen(open != null ? open : 1);
        downloadService.downloadFile(response, downloadModel, w, h, s);
    }

    @ApiOperation(value = "将视频转换成m3u8并下载", notes = "将视频转换成m3u8并下载")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "磁盘编号", name = "diskNo", paramType = "path", required = true),
            @ApiImplicitParam(value = "open是否直接打开,0:可以直接打开，1:不能直接打开，默认下载", name = "open", paramType = "query"),
            @ApiImplicitParam(value = "视频文件后缀", name = "suffix", paramType = "suffix"),
    })
    @GetMapping(value = "/m3u8/disk/{diskNo}/**")
    @CloseRequestLog
    public void downloadM3u8DiskFile(@PathVariable String diskNo, @ModelAttribute DownloadFileBean downloadFileBean,
                                     HttpServletRequest request, HttpServletResponse response) {
        String path = constants.getM3u8DiskUrl() + diskNo;
        String url = request.getRequestURI();
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String filePath = url.substring(path.length());
        String fileSuffix = FileUtils.getFileSuffix(filePath);
        if (fileSuffix.equals(".m3u8")) {
            // 验证文件
            String downloadPath = FileUtils.getFilePath(filePath) + "/" + FileUtils.getNotSuffixFileName(filePath) + downloadFileBean.getSuffix();
            DownloadModel downloadModel = fileService.verifySafetyChain(request.getHeader("referer"), downloadFileBean, diskNo, downloadPath);
            // 查询文件hash
            if (constants.getLocalUrl().equals(downloadModel.getServerUrl())) {
                downloadModel.setOpen(downloadFileBean.getOpen() != null ? downloadFileBean.getOpen() : 1);
                downloadModel.setName(FileUtils.getFileName(filePath));
                downloadService.downloadM3u8(response, downloadModel);
            } else {
                String httpUrl = downloadModel.getServerUrl() + url;
                httpUrl = StringUtils.analysisParam(httpUrl, downloadFileBean);
                restTemplate.execute(httpUrl, HttpMethod.GET, clientHttpRequest -> {
                    Enumeration<String> enumeration = request.getHeaderNames();
                    while (enumeration.hasMoreElements()) {
                        String headerName = enumeration.nextElement();
                        clientHttpRequest.getHeaders().set(headerName, request.getHeader(headerName));
                    }
                }, clientHttpResponse -> {
                    response.setStatus(clientHttpResponse.getRawStatusCode());
                    HttpHeaders httpHeaders = clientHttpResponse.getHeaders();
                    for (String key : httpHeaders.keySet()) {
                        List<String> strings = httpHeaders.get(key);
                        if (strings != null && strings.size() > 0) {
                            response.setHeader(key, strings.get(0));
                        }
                    }
                    InputStream inputStream = clientHttpResponse.getBody();
                    downloadService.downloadM3u8(response, downloadModel, inputStream);
                    return true;
                });
            }
        } else {
            downloadService.downloadM3u8(response, FileUtils.getFileName(filePath));
        }
    }

    @ApiOperation(value = "根据fid下载文件",
            notes = "1，下载文件必须符合下载条件，在安全链接允许范围内可下载\n" +
                    "2，/**后面对应网盘的文件目录及文件名称")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    value = "下载类型，open：开放下载，cdn：cdn下载",
                    name = "type", paramType = "path", required = true
            ),
            @ApiImplicitParam(value = "磁盘编号", name = "diskNo", paramType = "path", required = true),
            @ApiImplicitParam(value = "open是否直接打开,0:可以直接打开，1:不能直接打开，默认下载", name = "open", paramType = "query"),
    })
    @GetMapping(value = "/{type}/disk/{diskNo}/**")
    @CloseRequestLog
    public void downloadOpenDiskFile(@PathVariable String type, @PathVariable String diskNo,
                                     @ModelAttribute DownloadFileBean downloadFileBean,
                                     HttpServletRequest request, HttpServletResponse response) {
        String path;
        if (type.equals("cdn")) {
            path = constants.getCdnDiskUrl() + diskNo;
        } else {
            path = constants.getOpenDiskUrl() + diskNo;
        }
        String url = request.getRequestURI();
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String filePath = url.substring(path.length());
        DownloadModel downloadModel = fileService.verifySafetyChain(request.getHeader("referer"), downloadFileBean, diskNo, filePath);
        // 查询文件hash
        if (constants.getLocalUrl().equals(downloadModel.getServerUrl())) {
            downloadModel.setOpen(downloadFileBean.getOpen() != null ? downloadFileBean.getOpen() : 1);
            downloadModel.setRange(request.getHeader("range"));
            downloadService.downloadFile(response, downloadModel, downloadFileBean.getW(), downloadFileBean.getH(), downloadFileBean.getS());
        } else {
            String httpUrl = downloadModel.getServerUrl() + url;
            httpUrl = StringUtils.analysisParam(httpUrl, downloadFileBean);
            restTemplate.execute(httpUrl, HttpMethod.GET, clientHttpRequest -> {
                Enumeration<String> enumeration = request.getHeaderNames();
                while (enumeration.hasMoreElements()) {
                    String headerName = enumeration.nextElement();
                    clientHttpRequest.getHeaders().set(headerName, request.getHeader(headerName));
                }
            }, clientHttpResponse -> {
                response.setStatus(clientHttpResponse.getRawStatusCode());
                HttpHeaders httpHeaders = clientHttpResponse.getHeaders();
                for (String key : httpHeaders.keySet()) {
                    List<String> strings = httpHeaders.get(key);
                    if (strings != null && strings.size() > 0) {
                        response.setHeader(key, strings.get(0));
                    }
                }
                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = clientHttpResponse.getBody();
                int count;
                byte[] bytes = new byte[constants.getFileCache()];
                while ((count = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, count);
                }
                return true;
            });
        }
    }

    @ApiOperation(value = "根据文件哈希主键下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "哈希主键", name = "hashId", paramType = "path", required = true),
    })
    @GetMapping(value = "/hash_file/{hashId}")
    @CloseRequestLog
    public void downloadHashFile(@ModelAttribute SignBean signBean, @PathVariable String hashId, HttpServletRequest request, HttpServletResponse response) {
        signBean.setMyAppId(basicConstants.getAppId());
        signBean.setAuthorityCode(FileRestCode.downloadHashFile.getCode());
        basicApi.verifySign(signBean);
        // 查询文件hash信息
        FileHashEntity hashEntity = fileHashService.getById(hashId);
        // 封装下载Model
        DownloadModel downloadModel = new DownloadModel();
        downloadModel.setCode(hashEntity.getCode());
        downloadModel.setOpen(1);
        downloadModel.setRange(request.getHeader("range"));
        downloadModel.setName(hashId + FileSuffix.SUCCESS.getSuffix());
        downloadModel.setSize(hashEntity.getSize());
        downloadModel.setIsCompute(false);
        downloadService.downloadFile(response, downloadModel, null, null, null);
    }

}
