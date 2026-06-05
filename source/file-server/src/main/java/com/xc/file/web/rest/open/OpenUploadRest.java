package com.xc.file.web.rest.open;

import cn.hutool.json.JSONObject;
import com.xc.api.file.bean.DiskSignBean;
import com.xc.api.file.bean.SignUploadBean;
import com.xc.api.file.config.FileConstants;
import com.xc.api.file.dto.UploadDto;
import com.xc.api.file.enums.FileRestCode;
import com.xc.core.enums.EffectStatus;
import com.xc.file.config.Constants;
import com.xc.file.entity.DiskEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.UploadType;
import com.xc.file.model.UploadModel;
import com.xc.file.service.DiskService;
import com.xc.file.service.UploadService;
import com.xc.tool.utils.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>【开放】上传管理</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Api(tags = "【开放】上传管理")
@RestController
public class OpenUploadRest {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private Constants constants;
    @Autowired
    private DiskService diskService;
    @Autowired
    private FileConstants fileConstants;

    @ApiOperation(value = "上传文件",
            notes = "开放接口,签名方式上传,文件储存在该磁盘的目录下，1，单次上传，传入的size和文件的size必须相等\n" +
                    "2，分段上传，首次请求fileIndex必须等于零，根据返回的renewalSize确定每次上传多少\n" +
                    "3，md5签名顺序appId，folderNames，randomStr，timeStamp")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "文件数据", name = "file", paramType = "body", required = true),
    })
    @PostMapping("/open/upload_file")
    public UploadDto openUploadFile(SignUploadBean signUploadBean, @RequestPart("file") MultipartFile file) {
        if (file == null) {
            throw FailCode.FILE_NOT_EMPTY.getOperateException();
        }
        // 验证签名
        DiskSignBean diskSignBean = ObjectUtils.convert(new DiskSignBean(), signUploadBean);
        diskSignBean.setAuthorityCode(FileRestCode.openUploadFile.getCode());
        DiskEntity diskEntity = diskService.verifyDiskSign(diskSignBean);
        if (diskEntity == null) {
            throw FailCode.DISK_NO_ERROR.getOperateException();
        }
        // 封装数据上传文件
        UploadModel uploadModel = ObjectUtils.convert(new UploadModel(), signUploadBean);
        JSONObject jsonObject = diskSignBean.getSignDataObj(diskEntity.getDiskSecret(), diskEntity.getSignValidTime(), diskSignBean.getAuthorityCode());
        uploadModel.setFile(file);
        String groupCode = diskEntity.getId();
        if (jsonObject.get("groupCode") != null) {
            groupCode = groupCode + "-" + jsonObject.get("groupCode");
        }
        uploadModel.setGroupCode(groupCode);
        uploadModel.setFixedPath(String.valueOf(jsonObject.get("fixedPath")));
        if (signUploadBean.getStatus() != null) {
            uploadModel.setStatus(signUploadBean.getStatus());
        } else {
            uploadModel.setStatus(EffectStatus.INVALID.getStatus());
        }
        uploadModel.setDiskId(diskEntity.getId());
        uploadModel.setUploadType(UploadType.SIGN);
        UploadDto uploadDto = uploadService.uploadFile(uploadModel);
        // 上传完成
        if (uploadDto.getFinish()) {
            uploadDto.setDownloadUrl(constants.getLocalUrl() + constants.getOpenDiskUrl() + diskEntity.getDiskNo() + uploadDto.getFilePath());
        }
        // 返回续传地址
        if (uploadDto.getUploadUrl() != null) {
            uploadDto.setUploadUrl(uploadDto.getUploadUrl() + fileConstants.getUploadPath());
        } else {
            uploadDto.setUploadUrl(constants.getLocalUrl() + fileConstants.getUploadPath());
        }
        return uploadDto;
    }
}
