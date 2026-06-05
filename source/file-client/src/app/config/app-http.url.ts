import {environment} from '../../environments/environment';

const PATH = environment.config.url;
const FILE = PATH + environment.config.filePath; // 文件服务
export const HTTP_URLS = {
  // 我的应用
  getMyApp: FILE + '/my_app',
  getMyAppInfo: FILE + '/my_app/info', // 获取应用信息集合
  getMyAppToken: FILE + '/my_app/token', // 获取应用的token信息
  deleteMyAppToken: FILE + '/my_app/token', // 删除应用的token信息
  getMyAppMenuList: FILE + '/my_app/menu_list', // 获取应用的菜单信息集合
  getMyAppUser: FILE + '/my_app/user', // 获取我的应用的用户信息
  updateMyAppUserPassword: FILE + '/my_app/user/password', // 修改我的应用的用户密码
  getMyAppDictList: FILE + '/my_app/dict_list', // 获取我的应用的字典集合
  updateMyAppToken: FILE + '/my_app/token/{refreshToken}', // 更新应用的token信息
  updateMyAppUserMail: FILE + '/my_app/user/email', // 修改用户手机号
  updateMyAppUserPhone: FILE + '/my_app/user/phone',  // 修改用户邮箱
  deleteMyAppUser: FILE + '/my_app/user', // 注销账号
  getMyAppUserList: FILE + '/my_app/user_list', // 获取用户集合
  getMyAppUserGroupList: FILE + '/my_app/user/group_list', // 获取用户组集合
  createMyAppUserCaptcha: FILE + '/my_app/user_captcha/{messageCode}/{accountType}', // 发送用户验证码
  getMyAppUserCaptcha: FILE + '/my_app/user_captcha/{messageCode}', // 获取用户验证码信息
  createMyAppCaptcha: FILE + '/my_app/captcha/{messageCode}/{accountType}', //  发送验证码
  getMyAppImgCaptcha: FILE + '/my_app/img_captcha', // 获取图片验证码
  // 磁盘管理
  getDisk: FILE + '/disk/{diskId}', // 获取当前用户网盘信息
  getDiskPage: FILE + '/disk_pages/{current}', // 获取磁盘分页
  getDiskList: FILE + '/disk_list', // 获取磁盘集合
  createDisk: FILE + '/disk', // 创建磁盘
  updateDisk: FILE + '/disk/{diskId}', // 修改磁盘
  deleteDisk: FILE + '/disk/{diskId}', // 删除磁盘
  getDiskSecret: FILE + '/disk/{diskId}/secret', // 获取秘钥
  updateDiskSecret: FILE + '/disk/{diskId}/secret', // 更新秘钥
  // 网盘管理
  getDiskFolderFilePage: FILE + '/disk/{diskId}/folder_file_page/{current}', // 获取文件夹及文件
  createDiskFolder: FILE + '/disk/{diskId}/folder', // 创建文件夹
  updateDiskFolder: FILE + '/disk/{diskId}/folder/{folderId}', // 修改当前用户的文件夹
  updateDiskFile: FILE + '/disk/{diskId}/file/{fileId}', // 修改当前用户的文件
  createDiskShare: FILE + '/disk/{diskId}/share', // 创建当前用户共享
  createDiskFolderFile: FILE + '/disk/{diskId}/folder_file', //  复制文件夹及文件
  updateDiskFolderFile: FILE + '/disk/{diskId}/folder_file', // 批量移动文件夹及文件 put
  diskUploadFile: FILE + '/disk/{diskId}/upload_file', // 上传文件
  createDiskCdnUrl: FILE + '/disk/{diskId}/file/{fileId}/cdn_url', // 创建当前用户的cdn地址
  deleteDiskFolder: FILE + '/disk/{diskId}/folder/{folderId}', // 删除当前用户的文件夹
  deleteDiskFile: FILE + '/disk/{diskId}/file/{fileId}', // 删除文件  // 首页
  createDiskFolderFilePack: FILE + '/disk/{diskId}/folder_file/pack', // 打包文件夹及文件
  getDiskFolderSize: FILE + '/disk/{diskId}/folder/{folderId}/size', // 获取当前用户文件夹大小
  getParentFolderList: FILE + '/disk/{diskId}/parent_folder_list', // 获取父级文件夹集合
  // 安全连接
  getDiskSafetyChainPage: FILE + '/disk/{diskId}/safety_chain_pages/{current}', // 获取当前用户的安全链接页
  createDiskSafetyChain: FILE + '/disk/{diskId}/safety_chain', // 创建当前用户的安全链接
  deleteDiskSafetyChain: FILE + '/disk/{diskId}/safety_chain/{safetyChainId}', // 删除当前用户的安全链接
  updateDiskSafetyChain: FILE + '/disk/{diskId}/safety_chain/{safetyChainId}', // 修改当前用户的安全链接
  // 共享文件
  createDiskShareFolderFile: FILE + '/disk/{diskId}/share_folder_file', // 保存共享文件夹及文件
  deleteDiskShare: FILE + '/disk/{diskId}/share/{shareId}', // 删除当前用户的共享
  getDiskSharePage: FILE + '/disk/{diskId}/share_pages/{current}', // 获取当前用户的共享页
  verifyShareCode: FILE + '/verify_share/{code}', // 根据共享的code获取共享信息
  getShareFolderFilePage: FILE + '/share_folder_file_page/{current}', // 查询共享的文件夹及文件
  getShareFolderSize: FILE + '/share/{shareId}/folder/{folderId}/size', // 获取共享文件夹的大小
  getShareParentFolderList: FILE + '/share/{shareId}/parent_folder_list', // 获取共享父级文件夹集合
  // 共享，安全连接，网盘
  getDiskFolderPage: FILE + '/disk/{diskId}/folder_page/{current}' // 查询当前用户的文件夹集合

};

