package com.xc.file.enums;


import com.xc.core.exception.OperateException;

/**
 * <p>故障代码</p>
 *
 * @author xc
 * @version v1.0.0
 */
public enum FailCode {
    // 基础
    SIGN_DISK_NO_NOT_NULL("signDiskNoNotNull", "签名accessId不能为空！"),
    SIGN_DISK_NO_ERROR("signDiskNoError", "签名diskNo错误！"),
    // 磁盘
    DISK_NO_ERROR("diskNoError", "磁盘编号错误！"),
    DISK_UPDATE_FAIL("diskUpdateFail", "磁盘修改失败！"),
    DISK_CREATE_FAIL("diskCreateFail", "磁盘创建失败！"),
    DISK_ID_ERROR("diskIdError", "磁盘主键错误！"),
    DISK_SPACE_SHORTAGE("diskSpaceShortage", "你的磁盘空间不足！"),
    DISK_NOT_USER("notUserDisk", "不是用户的磁盘！"),
    // 文件
    FILE_CREATE_FAIL("fileCreateFail", "文件创建失败,请联系管理员！"),
    FILE_UPLOAD_FAIL("fileUploadFail", "文件更新失败,请联系管理员！"),
    FILE_DELETE_FAIL("fileDeleteFail", "文件删除失败,请联系管理员！"),
    FILE_EXIST("fileExist", "文件已存在！"),
    FILE_NOT_EXIST("fileNotExist", "文件不存在！"),
    FILE_NOT_DISK("fileNotDisk", "不是磁盘文件！"),
    FILE_FORMAT_ERROR("fileFormatError", "文件格式错误！"),
    // 文件夹
    FOLDER_UPDATE_FAIL("folderUpdateFail", "文件夹修改失败,请稍后重试！"),
    FOLDER_CREATE_FAIL("folderCreateFail", "文件夹创建失败,请稍后重试！"),
    FOLDER_DELETE_FAIL("folderDeleteFail", "文件夹删除失败,请稍后重试！"),
    FOLDER_COPY_FAIL("folderCopyFail", "文件夹复制失败！"),
    FOLDER_PATH_ERROR("folderPathError", "文件夹路径错误！"),
    FOLDER_EXIST("folderExist", "文件夹已存在！"),
    FOLDER_NOT_EXIST("folderNotExist", "文件夹不存在！"),
    FOLDER_NAME_NOT_NULL("folderNameNotNull", "文件夹名称不能为空！"),
    FOLDER_NOT_DISK("folderNotDisk", "不是磁盘文件夹！"),
    // 安全链接
    SAFETY_CHAIN_CREATE_FAIL("safetyChainCreateFail", "安全链接创建失败！"),
    SAFETY_CHAIN_UPDATE_FAIL("safetyChainUpdateFail", "安全链接更新失败！"),
    SAFETY_CHAIN_DELETE_FAIL("safetyChainDeleteFail", "安全链接删除失败！"),
    SAFETY_CHAIN_NOT_EXIST("safetyChainNotExist", "安全链接信息不存在！"),
    // 共享
    SHARE_INFO_NOT_EXIST("shareInfoNotExist", "共享信息不存在！"),//已确认
    SHARE_VISIT_CODE_ERROR("shareVisitCodeError", "共享访问code错误，请刷新重试！"),
    // 共享文件
    SHARE_FILE_INVALID("shareFileInvalid", "共享文件已失效！"), //已确认
    SHARE_FILE_NOT_EXIST("shareFileNotExist", "共享文件不存在！"), // 已确认;
    SHARE_FILE_CREATE_FAIL("shareFileCreateFail", "共享文件创建失败,请联系管理员！"),//已确认
    SHARE_FILE_UPDATE_FAIL("shareFileUpdateFail", "共享文件修改文件失败,请联系管理员！"),
    SHARE_FILE_RELATION_CREATE_FAIL("shareFileRelationCreateFail", "共享文件关联信息创建失败,请联系管理员！"),//已确认
    SHARE_FILE_RELATION_DELETE_FAIL("shareFileRelationDeleteFail", "共享文件关联信息删除失败,请联系管理员！"),//已确认
    // 服务器异常,以s开头
    RENAME_FAIL("renameFail", "重命名失败，你的文件可能未上传成功，请联系管理员！"),
    INIT_CREATE_FOLDER_FAIL("initCreateFolderFail", "初始化创建文件夹失败!"),
    INIT_CREATE_TEMP_FOLDER_FAIL("initCreateTempFolderFail", "初始化创建临时文件夹失败！"),
    TEMP_FILE_CREATE_FAIL("tempFileCreateFail", "临时文件创建失败,请联系管理员！"),
    SERVER_SPACE_SHORTAGE("serverSpaceShortage", "服务器硬盘空间不足,请联系管理员！"),
    DOWNLOAD_FILE_NOT_EXIST("downloadFileNotExist", "你下载的文件不存在,请联系管理员！"),
    UPLOAD_FILE_OVERTIME("uploadFileOvertime", "上传文件超时！"),
    UPDATE_FILE_FAIL("updateFileFail", "修改文件失败,请联系管理员！"),//已确认
    UPDATE_FREE_FLOW_FAIL("updateFreeFlowFail", "修改可用流量失败,请联系管理员！"),//已确认
    CREATE_FILE_HASH_FAIL("createFileHashFail", "创建文件hash失败,请联系管理员！"),
    NO_FILE_SERVICE_AVAILABLE("noFileServiceAvailable", "没有可用的文件服务,请联系管理员！"),
    COMPRESS_FAIL_SERVER_SPACE_SHORTAGE("compressFailServerSpaceShortage", "压缩失败，服务器硬盘空间不足,请联系管理员！"),
    REPLACE_FILE_FAIL("replaceFileFail", "替换文件失败,请联系管理员！"),
    COPY_FILE_FAIL("copyFileFail", "复制文件失败！"),
    FILE_DOWNLOAD_ERROR("fileDownloadError", "文件下载错误！"),
    PARAM_ERROR("paramError", "参数错误！"),
    PACK_NAME_NOT_NULL("packNameNotNull", "包名称不能为空！"),
    FILE_DATA_ERROR("fileDataError", "你上传的文件数据错误！"),
    FILE_NOT_EMPTY("fileNotEmpty", "你上传的文件不能为空！"),
    NOT_DATA_AUTHORITY("notDataAuthority", "你没有数据权限！"), //已确认
    SAFETY_CHAIN_REPEAT("safetyChainRepeat", "安全连接重复！"),
    CANNOT_DOWN_MOVE("cannotDownMove", "不能向下级移动！"),
    NOT_MOVE_OTHERS_FOLDER("notMoveOthersFolder", "不能移动他人文件夹，请复制后再移动！"),
    NOT_TOWARDS_OTHERS_FOLDER_MOVE("notTowardsOthersFolderMove", "不能向他人文件夹移动，请复制后再移动！"),
    NOT_MOVE_OTHERS_FILE("notMoveOthersFile", "不能移动他人文件，请复制后再移动！"),
    NOT_SHARE_OTHERS_FOLDER("notShareOthersFolder", "不能共享他人文件夹，请复制后再共享！"),
    NOT_SHARE_OTHERS_FILE("notShareOthersFile", "不能共享他人文件，请复制后再共享！"),
    NOT_TOWARDS_OTHERS_FOLDER_SAVE("notTowardsOthersFolderSave", "不能向他人文件夹保存！"),
    NOT_TOWARDS_OTHERS_FOLDER_UPLOAD("notTowardsOthersFolderUpload", "不能向他人文件夹上传！"),
    FREE_FLOW_SHORTAGE("freeFlowShortage", "你的可用流量不足！"),
    DRAW_CODE_ERROR("drawCodeError", "你的提取码错误!"), //已确认
    PARENT_FOLDER_ERROR("parentFolderError", "父级目录错误，请无非法操作！"),
    PARENT_FOLDER_NOT_EXIST("parentFolderNotExist", "父级文件夹不存在"), //已确认
    NOT_VISIT_AUTHORITY("notVisitAuthority", "你没有访问权限！"),
    DOWNLOAD_CODE_ERROR("downloadCodeError", "下载code错误，请刷新页面重试！"),
    FIXED_NOT_NULL("fixedNotNull", "固定的地址不能为空！"),
    TARGET_FOLDER_NOT_NULL("targetFolderNotNull", "目标文件夹不存在！"),
    NOT_INTERFACE_DATA_AUTHORITY("notInterfaceDataAuthority", "你没有该接口的数据权限，请联系管理员关联账户组！"),
    CREATE_WATERMARK_FAIL("createWatermarkFail", "创建水印失败！"),
    ;


    /**
     * 错误code
     */
    private String code;
    /**
     * 消息
     */
    private String message;

    FailCode() {
    }

    FailCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public OperateException getOperateException() {
        return new OperateException(this.code, this.getMessage());
    }
}
