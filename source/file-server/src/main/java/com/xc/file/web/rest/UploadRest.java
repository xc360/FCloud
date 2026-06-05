package com.xc.file.web.rest;

import com.xc.api.file.dto.UploadDto;
import com.xc.core.annotation.Authority;
import com.xc.core.model.TokenModel;
import com.xc.file.bean.TokenUploadBean;
import com.xc.file.config.Constants;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.UploadType;
import com.xc.file.model.UploadModel;
import com.xc.file.service.DiskService;
import com.xc.file.service.UploadService;
import com.xc.tool.utils.ObjectUtils;
import com.xc.tool.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>【用户】上传文件</p>
 *
 * @author xc
 * @version v1.0.0
 */
@RestController
@Api(tags = "【用户】上传文件")
public class UploadRest {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private Constants constants;
    @Autowired
    private DiskService diskService;

    @ApiOperation(value = "token上传文件",
            notes = "1，单次上传，传入的size和文件的size必须相等\n" +
                    "2，分段上传，首次请求fileIndex必须等于零，根据返回的renewalSize确定每次上传多少\n")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户token", name = "token", paramType = "header", required = true),
            @ApiImplicitParam(value = "文件数据", name = "file", paramType = "body", required = true),
            @ApiImplicitParam(value = "磁盘主键", name = "diskId", paramType = "path", required = true)
    })
    @PostMapping("/disk/{diskId}/upload_file")
    @Authority
    public UploadDto diskUploadFile(TokenModel tokenModel, @PathVariable String diskId, TokenUploadBean tokenUploadBean, MultipartFile file) {
        diskService.verifyUserDisk(tokenModel, diskId);
        if (file == null) {
            throw FailCode.FILE_NOT_EMPTY.getOperateException();
        }
        // 封装数据上传文件
        UploadModel uploadModel = ObjectUtils.convert(new UploadModel(), tokenUploadBean);
        uploadModel.setGroupCode(diskId);
        uploadModel.setDiskId(diskId);
        uploadModel.setFile(file);
        uploadModel.setUploadType(UploadType.TOKEN);
        UploadDto uploadDto = uploadService.uploadFile(uploadModel);
        if (uploadDto.getUploadUrl() != null) {
            uploadDto.setUploadUrl(uploadDto.getUploadUrl() + StringUtils.analysisPath(constants.getUploadPath(), diskId));
        } else {
            uploadDto.setUploadUrl(constants.getLocalUrl() + StringUtils.analysisPath(constants.getUploadPath(), diskId));
        }
        return uploadDto;
    }
}
