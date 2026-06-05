import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CommonTool} from '@ccxc/tool';
import {HTTP_URLS} from '../../config/app-http.url';
import {TableFindModel} from '@ccxc/common';
import {BUTTON_CODE} from '../../config/button-code';
import {CreateFolderComponent} from './create-folder/create-folder.component';

@Injectable({
  providedIn: 'root'
})
export class DiskManagementService {

  constructor(private http: HttpClient) {

  }

  /**
   * 获取用户集合
   */
  public getMyAppUserList() {
    return this.http.get<any>(HTTP_URLS.getMyAppUserList);
  }

  /**
   * 获取用户组集合
   */
  public getMyAppUserGroupList() {
    return this.http.get<any>(HTTP_URLS.getMyAppUserGroupList);
  }

  /**
   * 创建目录
   */
  public createFolder(diskId, folderId, req: any): Observable<Array<any>> {
    let url = CommonTool.analysisUrl(HTTP_URLS.createDiskFolder, {diskId});
    url = CommonTool.analysisParam(url, {folderId});
    return this.http.post<Array<any>>(url, req);
  }

  /**
   * 查询目录集合
   */
  public getDiskFolderFilePage(diskId, current, req): Observable<Array<any>> {
    let url = CommonTool.analysisUrl(HTTP_URLS.getDiskFolderFilePage, {diskId, current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get<Array<any>>(url);
  }

  /**
   * 删除文件夹
   */
  public deleteFolder(diskId, folderId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.deleteDiskFolder, {folderId, diskId});
    return this.http.delete<any>(url);
  }

  /**
   * 删除文件夹
   */
  public deleteFile(diskId, fileId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.deleteDiskFile, {fileId, diskId});
    return this.http.delete<any>(url);
  }

  /**
   * 查询目录集合信息
   */
  public getDiskFolderPage(diskId, current, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getDiskFolderPage, {diskId, current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get<any>(url);
  }

  /**
   * 移动目录
   */
  public updateDiskFolderFile(diskId, req): Observable<any> {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskFolderFile, {diskId});
    return this.http.put<any>(url, req);
  }

  /**
   * 复制文件夹及文件
   */
  public createDiskFolderFile(diskId, req) {
    const url = CommonTool.analysisUrl(HTTP_URLS.createDiskFolderFile, {diskId});
    return this.http.post<any>(url, req);
  }

  /**
   * 保存共享文件夹及文件
   */
  public createDiskShareFolderFile(diskId, shareId, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.createDiskShareFolderFile, {diskId});
    url = CommonTool.analysisParam(url, {shareId});
    return this.http.post<any>(url, req);
  }

  /**
   * 修改文件
   */
  public updateDiskFile(diskId, fileId, req): Observable<any> {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskFile, {diskId, fileId});
    return this.http.put<any>(url, req);
  }

  /**
   * 修改目录
   */
  public updateDiskFolder(diskId, folderId, req): Observable<any> {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskFolder, {diskId, folderId});
    return this.http.put<any>(url, req);
  }

  /**
   * 创建用户的文件包
   * 打包文件
   */
  public createDiskFolderFilePack(diskId, req) {
    const url = CommonTool.analysisUrl(HTTP_URLS.createDiskFolderFilePack, {diskId});
    return this.http.post<any>(url, req);
  }

  /**
   * 分享文件
   */
  public createDiskShare(diskId, req: { fileIds, folderIds, needCode; validTime, remark }): Observable<any> {
    const url = CommonTool.analysisUrl(HTTP_URLS.createDiskShare, {diskId});
    return this.http.post<any>(url, req);
  }

  /**
   * 查询共享文件
   *
   */
  public getDiskSharePage(diskId, current, req): Observable<any> {
    let url = CommonTool.analysisUrl(HTTP_URLS.getDiskSharePage, {diskId, current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get(url);
  }

  /**
   * 删除共享文件
   */
  public deleteDiskShare(diskId, shareId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.deleteDiskShare, {diskId, shareId});
    return this.http.delete<any>(url);
  }

  /**
   * 分页查询链接
   */
  public getDiskSafetyChainPage(diskId, current, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getDiskSafetyChainPage, {diskId, current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get<any>(url);
  }

  /**
   * 创建安全链接
   */
  public createDiskSafetyChain(diskId, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.createDiskSafetyChain, {diskId});
    return this.http.post<any>(url, req);
  }

  /**
   * 更新安全链接
   */
  public updateDiskSafetyChain(diskId, safetyChainId, req) {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskSafetyChain, {diskId, safetyChainId});
    return this.http.put<any>(url, req);
  }

  /**
   * 删除安全链接
   */
  public deleteDiskSafetyChain(diskId, safetyChainId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskSafetyChain, {diskId, safetyChainId});
    return this.http.delete<any>(url);
  }

  /**
   * 查询共享文件
   */
  public getShareFolderFilePage(current, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getShareFolderFilePage, {current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get<any>(url);
  }

  /**
   * 获取系统文件集合
   */
  public verifyShareCode(code, drawCode?) {
    let url = CommonTool.analysisUrl(HTTP_URLS.verifyShareCode, {code});
    url = CommonTool.analysisParam(url, {drawCode});
    return this.http.get<any>(url);
  }

  /**
   * 获取系统文件集合
   */
  public getUploadUrl(diskId) {
    return CommonTool.analysisUrl(HTTP_URLS.diskUploadFile, {diskId});
  }

  /**
   * 获取文件夹大小
   */
  public getDiskFolderSize(diskId, folderId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.getDiskFolderSize, {diskId, folderId});
    return this.http.get<any>(url);
  }

  /**
   * 获取父级文件夹集合
   */
  public getParentFolderList(diskId, folderId) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getParentFolderList, {diskId});
    url = CommonTool.analysisParam(url, {folderId});
    return this.http.get<any>(url);
  }


  /**
   * 获取共享文件夹大小
   */
  public getShareFolderSize(shareId, folderId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.getShareFolderSize, {shareId, folderId});
    return this.http.get<any>(url);
  }

  /**
   * 获取共享父级文件夹集合
   */
  public getShareParentFolderList(shareId, folderId) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getShareParentFolderList, {shareId});
    url = CommonTool.analysisParam(url, {folderId});
    return this.http.get<any>(url);
  }

  /**
   * 创建当前用户的cdn地址
   */
  public createCdnUrl(diskId, fileId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.createDiskCdnUrl, {diskId, fileId});
    return this.http.post<any>(url, {});
  }

  /**
   * 获取当前用户网盘信息
   */
  public getDisk(diskId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.getDisk, {diskId});
    return this.http.get<any>(url);
  }

  /**
   * 获取磁盘分页
   */
  public getDiskPage(current, req) {
    let url = CommonTool.analysisUrl(HTTP_URLS.getDiskPage, {current});
    url = CommonTool.analysisParam(url, req);
    return this.http.get<any>(url);
  }


  /**
   * 创建磁盘
   */
  public createDisk(req) {
    return this.http.post<Array<any>>(HTTP_URLS.createDisk, req);
  }

  /**
   * 修改磁盘
   */
  public updateDisk(diskId, req) {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDisk, {diskId});
    return this.http.put<Array<any>>(url, req);
  }

  /**
   * 删除磁盘
   */
  public deleteDisk(diskId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.deleteDisk, {diskId});
    return this.http.delete<any>(url);
  }

  /**
   * 获取用户磁盘秘钥
   */
  public getDiskSecret(diskId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.getDiskSecret, {diskId});
    return this.http.get(url);
  }

  /**
   * 更新用户磁盘秘钥
   */
  public updateDiskSecret(diskId) {
    const url = CommonTool.analysisUrl(HTTP_URLS.updateDiskSecret, {diskId});
    return this.http.put(url, {});
  }

  /**
   * 获取文件夹数组
   *
   * folderPath 文件夹字符串路径
   * 文件夹数据
   */
  public getFolderPaths(folderPath) {
    let folderPaths = [];
    if (folderPath) {
      if ('/' === folderPath.substring(0, 1)) {
        folderPath = folderPath.substring(1);
      }
      folderPaths = folderPath.split('/');
    }
    return folderPaths;
  }

  /**
   * 验证后缀是否是图片
   * 图片名称 name
   */
  public verifyImageSuffix(suffix) {
    return suffix === '.jpg' || suffix === '.png' || suffix === '.jpeg' || suffix === 'gif' || suffix === 'bmp';
  }

  /**
   * 初始化选择数据
   */
  public selectFolder(imgConfig, createFolderComponent: CreateFolderComponent, fun, diskId) {
    const tableFindModel = new TableFindModel();
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    const myThis = this;
    let path = '';
    const labels = [{name: '全部', folderId: '', path}];
    tableFindModel.setPagingModel({
      size: 100
    });
    tableFindModel.initAll({
      finds: [
        {
          name: '文件夹名',
          field: 'name',
          input: {
            maxLength: 255
          }
        }
      ],
      tables: [
        {
          name: '', field: 'imageUrl', type: COMMON_TYPE.table.IMAGE,
          tdStyle: {cursor: 'pointer'},
          confirmFun: (dataModel, table, that) => {
            path = path + '/' + dataModel.data.name;
            const label = {name: dataModel.data.name, folderId: dataModel.data.id, path};
            labels.push(label);
            fun(label);
            that.getData({folderId: dataModel.data.id}, null);
          }
        },
        {
          name: '文件夹名',
          field: 'name',
          type: COMMON_TYPE.table.TITLE,
          isSort: true,
          tdStyle: {cursor: 'pointer'},
          confirmFun: (dataModel, table, that) => {
            path = path + '/' + dataModel.data.name;
            const label = {name: dataModel.data.name, folderId: dataModel.data.id, path};
            labels.push(label);
            fun(label);
            that.getData({folderId: dataModel.data.id}, null);
          }
        },
        {
          name: '', field: 'createTime', type: COMMON_TYPE.table.DESCRIPTION,
          confirmFun: (dataModel, table, that) => {
            path = path + '/' + dataModel.data.name;
            const label = {name: dataModel.data.name, folderId: dataModel.data.id, path};
            labels.push(label);
            fun(label);
            that.getData({folderId: dataModel.data.id}, null);
          }
        }
      ],
      buttons: [
        {
          name: '创建目录',
          type: COMMON_TYPE.button.BUTTON,
          bAuthority: BUTTON_CODE.b_folder_add,
          confirmFun: (formInfo) => {
            createFolderComponent.create(diskId, formInfo['folderId']);
          }
        }
      ],
      breadcrumb: {
        breadcrumbFun: (label, i, that) => {
          if (i + 1 < labels.length) {
            labels.splice(i + 1, labels.length);
          }
          path = labels[i]['path'];
          fun(labels[i]);
          that.getData({folderId: labels[i].folderId}, null);
        },
        labels: [] // 面包屑
      },
      initDataFun(formInfo, sortPage, callback, that) {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        const req = {
          size: sortPage.size,
          sortField: sortPage.sortField,
          sortRule: sortPage.sortRule,
          likeFields: ['name'],
          folderId: formInfo['folderId']
        };
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.getDiskFolderPage(diskId, sortPage.current, req).subscribe(res => {
          const array = [];
          let userId = null;
          for (const arr of res['resData']) {
            userId = arr.userId;
            arr['imageUrl'] = imgConfig['disk_icon_folder'];
            array.push({data: arr, isFile: false});
          }
          that.config.breadcrumb.labels = labels;
          callback(array, {total: res['total']});
        });
      },
      frontPaging: false,
      frontSort: false
    }, null, {folderId: ''});
    return tableFindModel;
  }
}
