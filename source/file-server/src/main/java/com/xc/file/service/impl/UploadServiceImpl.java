package com.xc.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.xc.api.file.dto.UploadDto;
import com.xc.core.enums.EffectStatus;
import com.xc.core.enums.Whether;
import com.xc.file.bean.FileBean;
import com.xc.file.config.Constants;
import com.xc.file.config.FolderConfig;
import com.xc.file.entity.FileEntity;
import com.xc.file.entity.FileHashEntity;
import com.xc.file.entity.FolderEntity;
import com.xc.file.enums.FailCode;
import com.xc.file.enums.FileSuffix;
import com.xc.file.enums.UploadType;
import com.xc.file.model.FileModel;
import com.xc.file.model.IndexModel;
import com.xc.file.model.UploadModel;
import com.xc.file.service.*;
import com.xc.tool.utils.FileUtils;
import com.xc.tool.utils.Md5Utils;
import com.xc.tool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>文件操作接口</p>
 *
 * @author xc
 * @version v1.0.0
 */
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    private static final Map<String, Long> lockMap = new Hashtable<>();

    @Autowired
    private Constants constants;
    @Autowired
    private FileService fileService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private FileHashService fileHashService;

    @Override
    public UploadDto uploadFile(UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        try {
            lock(hashCode);
            // 检测本服务器是否有空间上传
            String path = getFilePathBySize(uploadModel.getSize());
            if (path == null) {// 本服务器无空间上传,返回可以上传的服务器地址
                UploadDto uploadDto = new UploadDto(0L);
                uploadDto.setFileIndex(0L);
                uploadDto.setHashCode(hashCode);
                uploadDto.setUploadUrl(fileCacheService.getServiceIp(uploadModel.getSize()));
                return uploadDto;
            }
            // 判断是否开启续传
            if (Whether.YES.getValue().equals(uploadModel.getResumed())) {
                // 启用续传
                return subsectionUpload(path, uploadModel);
            } else {
                // 秒传处理
                UploadDto uploadDto = fastUploadFile(uploadModel);
                if (uploadDto != null) {
                    return uploadDto;
                }
                // 不启用续传
                return singleUpload(path, uploadModel);
            }
        } finally {
            unlock(hashCode);
        }
    }

    @Override
    public void deleteFile(String code, String suffix) {
        List<FolderConfig> folderPath = constants.getFolderPaths();
        for (FolderConfig folderConfig : folderPath) {
            List<File> fileList = FileUtil.loopFiles(folderConfig.getPath());
            for (File file : fileList) {
                if (file.getName().equals(code + suffix)) {
                    if (file.exists() && !file.delete()) {
                        throw FailCode.FILE_DELETE_FAIL.getOperateException();
                    }
                }
            }
        }
    }

    @Override
    public void rename(File tempFile, File newFile, String hashCode) {
        if (!newFile.exists()) {
            if (!tempFile.renameTo(newFile)) {
                //删除文件信息和文件
                deleteFileAndIndex(hashCode);
                throw FailCode.RENAME_FAIL.getOperateException();
            }
        }
    }

    @Override
    public void renameFile(File tempFile, File newFile) {
        if (!newFile.exists()) {
            if (!tempFile.renameTo(newFile)) {
                throw FailCode.RENAME_FAIL.getOperateException();
            }
        }
    }

    /**
     * <p>单个文件上传</p>
     *
     * @param uploadModel 上传信息
     * @return 上传返回参数
     */
    private UploadDto singleUpload(String path, UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        //上传文件，存为临时文件
        String tempPath = path + File.separator + hashCode + FileSuffix.UPLOAD.getSuffix();
        uploadFileHandle(tempPath, uploadModel);
        //文件上传完成
        return uploadFinish(path, uploadModel);
    }

    /**
     * <p>分段上传</p>
     *
     * @param uploadModel 上传信息
     * @return 返回参数
     */
    private UploadDto subsectionUpload(String usePath, UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        log.info("上传缓存大小：{}", constants.getFileInfoTable().size());
        FileModel info = constants.getFileInfoTable().get(hashCode);
        //判断临时文件是否存在
        if (!verifyFileExist(hashCode, FileSuffix.UPLOAD.getSuffix()) || info == null) {
            // 秒传处理
            UploadDto uploadDto = fastUploadFile(uploadModel);
            if (uploadDto != null) {
                return uploadDto;
            }
            //删除临时文件和信息
            deleteFileAndIndex(hashCode);
            //创建临时文件信息
            return createIndex(usePath, uploadModel);
        }
        // 获取当前上传文件的信息
        boolean bool = true;
        for (IndexModel indexModel : info.getIndex()) {
            if (indexModel.getStartIndex().equals(uploadModel.getFileIndex()) && !indexModel.getState()) {
                long size = uploadModel.getFileIndex() + (indexModel.getStopIndex() - indexModel.getStartIndex());
                if (uploadModel.getFileIndex() + uploadModel.getFile().getSize() == size) {
                    bool = false;
                }
            }
        }
        UploadDto uploadDto = new UploadDto(constants.getRenewalSize());
        // 验证上传的文件信息是否正确，不正确返回重新上传
        if (bool) {
            IndexModel indexModel = getIndex(hashCode);
            if (indexModel != null) {
                uploadDto.setFileIndex(indexModel.getStartIndex());
                uploadDto.setFinish(false);
                return uploadDto;
            }
        }
        //上传文件
        String tempPath = info.getFilePath() + File.separator + hashCode + FileSuffix.UPLOAD.getSuffix();
        uploadFileHandle(tempPath, uploadModel);
        if (uploadModel.getSize() == new File(tempPath).length()) {
            return uploadFinish(info.getFilePath(), uploadModel);
        }
        //计算剩余的上传次数
        for (IndexModel indexModel : info.getIndex()) {
            if (indexModel.getStartIndex().equals(uploadModel.getFileIndex())) {
                indexModel.setState(true);
            }
        }
        IndexModel indexModel = getIndex(hashCode);
        if (indexModel != null) {
            uploadDto.setFileIndex(indexModel.getStartIndex());
            uploadDto.setFinish(false);
            return uploadDto;
        }
        throw FailCode.FILE_UPLOAD_FAIL.getOperateException();
    }


    /**
     * <p>快速上传</p>
     *
     * @param uploadModel 上传信息
     * @return 上传返回信息
     */
    private UploadDto fastUploadFile(UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        FileHashEntity fileHashEntity = fileHashService.getFileHash(hashCode, uploadModel.getGroupCode());
        if (fileHashEntity != null) {
            // 秒传
            UploadDto uploadDto = new UploadDto();
            uploadDto.setHashCode(hashCode);
            uploadDto.setFastFinish(true);
            uploadDto.setFinish(true);
            FileEntity fileEntity = uploadSuccess(fileHashEntity.getId(), fileHashEntity.getSize(), uploadModel);
            String folderPath = folderService.getPathByFolderId(fileEntity.getFolderId());
            uploadDto.setFilePath(folderPath + "/" + fileEntity.getName());
            return uploadDto;
        } else {
            return null;
        }
    }

    /**
     * <p>上传成功处理</p>
     *
     * @param uploadModel 上传信息
     * @param path        存放路径
     * @return 返回成功信息
     */
    private UploadDto uploadFinish(String path, UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        //文件上传完成，计算文件hash值
        String tempPath = path + File.separator + hashCode + FileSuffix.UPLOAD.getSuffix();
        File tempFile = new File(tempPath);
        String md5 = Md5Utils.getFileMd5(tempFile);
        if (!hashCode.equals(md5)) {
            deleteFileAndIndex(hashCode);
            throw FailCode.FILE_DATA_ERROR.getOperateException();
        }
        FileHashEntity fileHashEntity;
        String code = StringUtils.generateOnlyId(constants.getMachineId());
        fileHashEntity = fileHashService.getFileHash(hashCode, uploadModel.getGroupCode());
        if (fileHashEntity == null) {
            // 创建文件hash
            fileHashEntity = fileHashService.createFileHash(hashCode, uploadModel.getGroupCode(), tempFile.length(), code);
            //重命名文件
            String newPath = path + File.separator + fileHashEntity.getCode() + FileSuffix.SUCCESS.getSuffix();
            File newFile = new File(newPath);
            rename(tempFile, newFile, hashCode);
        }
        //删除文件源信息
        deleteFileAndIndex(hashCode);
        if (constants.getFileInfoTable().get(hashCode) != null) {
            constants.getFileInfoTable().remove(hashCode);
        }
        // 上传成功
        UploadDto uploadDto = new UploadDto();
        uploadDto.setHashCode(hashCode);
        uploadDto.setFinish(true);
        FileEntity fileEntity = uploadSuccess(fileHashEntity.getId(), fileHashEntity.getSize(), uploadModel);
        String folderPath = folderService.getPathByFolderId(fileEntity.getFolderId());
        uploadDto.setFilePath(folderPath + "/" + fileEntity.getName());
        return uploadDto;
    }

    /**
     * 成功创建目录和文件
     *
     * @param hashId      哈希主键
     * @param size        文件胆小
     * @param uploadModel 上传文件的model信息
     * @return 文件信息
     */
    public FileEntity uploadSuccess(String hashId, long size, UploadModel uploadModel) {
        String folderId = constants.getRoot();
        String diskId;
        if (uploadModel.getUploadType().equals(UploadType.TOKEN)) {
            String parentNode;
            if (uploadModel.getFolderId() != null && !"".equals(uploadModel.getFolderId())) {
                // 查询父级文件夹
                FolderEntity folder = folderService.getById(uploadModel.getFolderId());
                // 验证父级文件夹是否存在
                if (folder == null) {
                    throw FailCode.PARENT_FOLDER_NOT_EXIST.getOperateException();
                }
                // 验证父级文件夹是不是当前磁盘的
                parentNode = folder.getNode();
                diskId = folder.getDiskId();
                folderId = folder.getId();
            } else {
                parentNode = constants.getRoot();
                diskId = uploadModel.getDiskId();
            }
            // 创建文件夹
            FolderEntity entity = folderService.createParentFolders(parentNode, diskId, uploadModel.getFolderPath());
            if (entity != null) {
                folderId = entity.getId();
            }
        } else {
            // 查询磁盘信息
            String parentNode;
            String fixedPath = uploadModel.getFixedPath();
            diskId = uploadModel.getDiskId();
            // 验证固定地址
            FolderEntity entity;
            if (fixedPath != null && !"".equals(fixedPath)) {
                // 验证固定地址是否正确
                entity = folderService.getFolderByPath(diskId, fixedPath);
                if (entity == null) {
                    throw FailCode.FOLDER_NOT_EXIST.getOperateException();
                }
                parentNode = entity.getNode();
            } else {
                parentNode = constants.getRoot();
            }
            // 创建文件夹
            entity = folderService.createParentFolders(parentNode, uploadModel.getDiskId(), uploadModel.getFolderPath());
            if (entity != null) {
                folderId = entity.getId();
                // 不能向他人文件夹保存
                if (!entity.getDiskId().equals(uploadModel.getDiskId())) {
                    throw FailCode.NOT_TOWARDS_OTHERS_FOLDER_UPLOAD.getOperateException();
                }
            }
        }
        // 处理文件状态，默认无效
        FileBean fileBean = new FileBean();
        if (uploadModel.getStatus() != null) {
            fileBean.setStatus(uploadModel.getStatus());
        } else {
            fileBean.setStatus(EffectStatus.VALID.getStatus());
        }
        // 封装上传数据
        fileBean.setName(uploadModel.getName());
        fileBean.setSize(size);
        fileBean.setHashId(hashId);
        fileBean.setFolderId(folderId);
        fileBean.setStatus(uploadModel.getStatus());
        // 创建文件
        return fileService.createFile(diskId, fileBean, true);
    }

    /**
     * <p>创建文件信息<p/>
     *
     * @param uploadModel 上传信息
     * @return 返回临时文件路径
     */
    private UploadDto createIndex(String path, UploadModel uploadModel) {
        String hashCode = uploadModel.getHashCode();
        long size = uploadModel.getSize();
        FileModel info = new FileModel();
        //计算数据段
        List<IndexModel> indexModels = new ArrayList<>();
        long len = (size - (size % constants.getRenewalSize())) / constants.getRenewalSize();
        for (int i = 0; i < len; i++) {
            IndexModel indexModel = new IndexModel();
            long start = i * constants.getRenewalSize();
            indexModel.setStartIndex(start);
            indexModel.setStopIndex(start + constants.getRenewalSize());
            indexModels.add(indexModel);
        }
        //剩余的数据段
        IndexModel indexModel = new IndexModel();
        indexModel.setStartIndex(size - (size % constants.getRenewalSize()));
        indexModel.setStopIndex(size);
        indexModels.add(indexModel);
        //文件信息
        info.setIndex(indexModels);
        info.setSize(size);
        info.setFilePath(path);
        String tempPath = path + File.separator + hashCode + FileSuffix.UPLOAD.getSuffix();
        File tempFile = new File(tempPath);
        if (tempFile.exists()) {
            FileUtils.deleteFile(tempPath);
        }
        //创建文件
        try {
            if (!tempFile.createNewFile()) {
                throw FailCode.TEMP_FILE_CREATE_FAIL.getOperateException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        constants.getFileInfoTable().put(hashCode, info);
        //获取临时文件信息
        UploadDto uploadDto = new UploadDto(constants.getRenewalSize());
        for (IndexModel index : info.getIndex()) {
            if (!index.getState()) {
                uploadDto.setFinish(false);
                uploadDto.setFileIndex(index.getStartIndex());
                return uploadDto;
            }
        }
        uploadDto.setFinish(false);
        uploadDto.setFileIndex(0L);
        return uploadDto;
    }

    /**
     * <p>获取index信息<p/>
     *
     * @param hashCode 文件hash值
     * @return index信息
     */
    private IndexModel getIndex(String hashCode) {
        FileModel info = constants.getFileInfoTable().get(hashCode);
        for (IndexModel indexModel : info.getIndex()) {
            if (!indexModel.getState()) {
                return indexModel;
            }
        }
        return null;
    }

    /**
     * <p>文件分段上传</p>
     *
     * @param path        路径
     * @param uploadModel 上传参数
     */
    private void uploadFileHandle(String path, UploadModel uploadModel) {
        File uploadFile = new File(path);
        try (RandomAccessFile loOutput = new RandomAccessFile(uploadFile, "rws")) {
            InputStream in = uploadModel.getFile().getInputStream();
            loOutput.seek(uploadModel.getFileIndex());
            //<2>创建缓存大小
            byte[] buffer = new byte[constants.getFileCache()]; // 1KB
            //每次读取到内容的长度
            //<3>开始读取输入流中的内容
            int temp;
            while ((temp = in.read(buffer)) != -1) { //当等于-1说明没有数据可以读取了
                loOutput.write(buffer, 0, temp);   //把读取到的内容写到输出流中
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>加锁,30分钟超时</p>
     *
     * @param key 锁的对象
     * @return 返回是否获取到锁，true：获取到锁，false：没获取到锁
     */
    private boolean lock(String key) {
        synchronized (UploadServiceImpl.class) {
            while (true) {
                Long lock = lockMap.get(key);
                long currentTime = System.currentTimeMillis();
                if (lock == null) {
                    lockMap.put(key, currentTime);
                    return true;
                } else if ((currentTime - lock) > 1800000) {
                    throw FailCode.UPLOAD_FILE_OVERTIME.getOperateException();
                }
            }
        }
    }

    /**
     * <p>删除锁</p>
     *
     * @param key 锁的对象
     */
    private void unlock(String key) {
        lockMap.remove(key);
    }

    /**
     * <p>删除文件信息和文件</p>
     *
     * @param hashCode 文件的hash值
     */
    private void deleteFileAndIndex(String hashCode) {
        //删除文件源信息
        if (constants.getFileInfoTable().get(hashCode) != null) {
            constants.getFileInfoTable().remove(hashCode);
        }
        deleteFile(hashCode, FileSuffix.UPLOAD.getSuffix());
    }

    /**
     * <p>验证文件是否存在</p>
     *
     * @param code 文件标识
     */
    private boolean verifyFileExist(String code, String suffix) {
        List<FolderConfig> folderPath = constants.getFolderPaths();
        for (FolderConfig folderConfig : folderPath) {
            List<File> fileList = FileUtil.loopFiles(folderConfig.getPath());
            for (File file : fileList) {
                if (file.getName().equals(code + suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getFilePathBySize(Long size) {
        long freeSpace = 0;
        String path = null;
        for (FolderConfig folderConfig : constants.getFolderPaths()) {
            File file = new File(folderConfig.getPath());
            if (!file.exists() && !file.isDirectory()) {
                if (!file.mkdirs()) {
                    throw FailCode.INIT_CREATE_FOLDER_FAIL.getOperateException();
                }
            }
            if ((file.getFreeSpace() - folderConfig.getReserveSpace()) >= size) {
                if (freeSpace < file.getFreeSpace()) {
                    freeSpace = file.getFreeSpace();
                    path = folderConfig.getPath();
                }
            }
        }
        path = path + File.separator + DateUtil.today();
        FileUtil.mkdir(path);
        return path;
    }
}
