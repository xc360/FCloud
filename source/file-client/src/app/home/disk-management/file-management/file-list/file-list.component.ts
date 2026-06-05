import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import {NzContextMenuService, NzMessageService, NzModalService} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import Viewer from 'viewerjs';
import {AppService} from '../../../../app.service';
import {MusicPlayerComponent, TableFindModel} from '@ccxc/common';
import {DiskManagementService} from '../../disk-management.service';
import {BUTTON_CODE} from '../../../../config/button-code';
import {CreateFolderComponent} from '../../create-folder/create-folder.component';
import {CommonTool, DictTool, FileTool, LoginTool, UploadTool} from '@ccxc/tool';
import {PAGE_URLS} from '../../../../config/app-page.url';
import UploadData from '@ccxc/tool/upload/model/upload-data';

@Component({
  selector: 'app-file-management',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.scss']
})
export class FileListComponent implements OnInit {

  @ViewChild(MusicPlayerComponent, {static: true})
  public musicPlayerComponent: MusicPlayerComponent;
  public commonTool = CommonTool;
  public recommendMovie: Array<{ name, value }> = [];
  public tableFindModel: TableFindModel;
  public tableFindModel1: TableFindModel;
  public tableFindModel2: TableFindModel;
  public edit = {isFile: true, name: '', id: '', isVisible: false, nameStatus: ''};
  public share = {
    isVisible: false,
    fileIds: [],
    folderIds: [],
    okDisabled: false,
    needCode: '0',
    validTime: '0',
    remark: ''
  };
  public selectFolder = {folderId: '', isVisible: false, type: '', okText: ''};


  public shareUrl = {
    url: '',
    isVisible: false,
    drawCode: ''
  };
  public showMusicPlay = false;
  public pictureFiles: Array<any> = [];
  public pictureShowFiles: Array<any> = [];
  public showPicture = false;
  public videoPlay = {url: '', isVisible: false};
  public upload = {isVisible: false};
  public package = {
    isVisible: false,
    nameStatus: '',
    fileIds: [],
    folderIds: [],
    name: ''
  };
  public renewalSize = 10485760; // 文件读取大小
  public count; // 计数
  public viewer;
  public imgConfig = {};
  public cdn = {isVisible: false, url: ''};

  public compressUrl = '?open=0&s=0.05';
  public compressUrl1 = '?open=0';

  @ViewChild('editFolderName', {static: true}) editFolderName: ElementRef;
  @ViewChild('shareRemark', {static: true}) shareRemark: ElementRef;
  public fileLength = 0;
  public folderLength = 0;
  @ViewChild('createFolder', {static: true})
  public createFolderComponent: CreateFolderComponent;

  @ViewChild('createFolder1', {static: true})
  public createFolderComponent1: CreateFolderComponent;
  public diskId;


  constructor(private nzContextMenuService: NzContextMenuService,
              public diskManagementService: DiskManagementService,
              private modalService: NzModalService,
              public appService: AppService,
              public router: Router,
              public activateRoute: ActivatedRoute,
              private message: NzMessageService,
              public renderer: Renderer2,
              public elementRef: ElementRef) {
    // 获取字典信息
    const array = DictTool.getDictList('fileIcon');
    for (const dict of array) {
      this.imgConfig[dict.name] = dict.value;
    }
    this.recommendMovie = DictTool.getDictList('recommendMovie');
    this.count = 0;
    this.tableFindModel = new TableFindModel();
    this.tableFindModel1 = new TableFindModel();
    this.tableFindModel2 = new TableFindModel();
    this.diskId = this.activateRoute.snapshot.queryParams['diskId'];
    if (this.diskId) {
      const folderId = this.activateRoute.snapshot.queryParams['folderId'];
      if (folderId) {
        this.diskManagementService.getParentFolderList(this.diskId, folderId).subscribe(folders => {
          this.init(folderId, folders);
        });
      } else {
        this.init(folderId, []);
      }
      this.uploadInit();
    }
  }

  // 初始化
  ngOnInit(): void {
    if (this.diskId) {
      this.onInit();
    }
  }

  public onInit() {
    const that = this;
    if (window.history && window.history.pushState) {
      window.onpopstate = (e) => {
        const url = e['target']['location'].href;
        const data = CommonTool.getUrlParam(url);
        if (data['folderId']) {
          that.tableFindModel.getData({folderId: data['folderId']}, null);
        } else {
          that.tableFindModel.getData({folderId: null}, null);
        }
      };
    }
    const drop = this.elementRef.nativeElement.querySelector('#upload-file');
    // 拖动文件上传
    this.renderer.listen(drop, 'dragover', (event) => {
      // 取消默认浏览器拖拽效果
      event.preventDefault();
    });
    this.renderer.listen(drop, 'drop', (e) => {
      const dataTransfer = e.dataTransfer;
      if (dataTransfer && dataTransfer.files && dataTransfer.files.length) {
        e.preventDefault();
        FileTool.handleFileDirectory(dataTransfer).then((files) => {
          that.uploadFolderFile(files, that.tableFindModel.formInfo['folderId']);
        });
      }
    });
  }

  public modalOpen(key) {
    this[key].nativeElement.focus();
  }

  /**
   * 初始化
   */
  public init(folderId, folders) {
    const myThis = this;
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    const disabledFun = (formInfo, that) => {
      let bool = true;
      that.dataModels.forEach(dataModel => {
        if (dataModel.selected) {
          bool = false;
        }
      });
      return bool;
    };
    const labels = [{name: '全部', folderId: ''}];
    for (const folder of folders) {
      labels.push({
        name: folder.name,
        folderId: folder.id
      });
    }
    this.tableFindModel.setPagingModel({
      size: 100
    });
    this.tableFindModel.initAll({
      finds: [
        {
          name: '文件名',
          field: 'name',
          input: {
            maxLength: 255
          }
        }
      ],
      buttons: [
        {
          name: '创建目录',
          type: COMMON_TYPE.button.BUTTON,
          bAuthority: BUTTON_CODE.b_folder_add,
          confirmFun: (formInfo) => {
            myThis.createFolderComponent.create(myThis.diskId, formInfo['folderId']);
          }
        },
        {
          name: '上传文件',
          bAuthority: BUTTON_CODE.b_folder_file_upload,
          type: COMMON_TYPE.button.BUTTON,
          confirmFun: () => {
            myThis.selectFile();
          }
        },
        {
          name: '上传文件夹',
          bAuthority: BUTTON_CODE.b_folder_file_upload,
          type: COMMON_TYPE.button.BUTTON,
          confirmFun: () => {
            myThis.uploadFolder();
          }
        },
        {
          name: '音乐播放器',
          bAuthority: BUTTON_CODE.b_music_play,
          type: COMMON_TYPE.button.BUTTON,
          confirmFun: (formInfo, that) => {
            myThis.showMusicPlay = true;
            myThis.handleMp3(null);
          }
        },
        {
          name: '分享',
          bAuthority: BUTTON_CODE.b_folder_file_share,
          type: COMMON_TYPE.button.BUTTON,
          disabledFun,
          confirmFun: (formInfo, that) => {
            myThis.shareRemark.nativeElement.focus();
            const selectedArray = that.getSelectedArray();
            const fileIds = [];
            const folderIds = [];
            selectedArray.forEach(dataModel => {
              if (dataModel.isFile) {
                fileIds.push(dataModel.data.id);
              } else {
                folderIds.push(dataModel.data.id);
              }
            });
            myThis.share.isVisible = true;
            myThis.share.fileIds = fileIds;
            myThis.share.folderIds = folderIds;
          }
        },
        {
          name: '复制',
          bAuthority: BUTTON_CODE.b_folder_file_copy,
          type: COMMON_TYPE.button.BUTTON,
          disabledFun,
          confirmFun: (formInfo, that) => {
            myThis.selectFolder = {folderId: '', isVisible: true, type: 'copy', okText: '复制'};
            myThis.tableFindModel1 = myThis.diskManagementService.selectFolder(myThis.imgConfig, myThis.createFolderComponent1, (label) => {
              myThis.selectFolder.folderId = label.folderId;
            }, myThis.diskId);
          }
        },
        {
          name: '移动',
          type: COMMON_TYPE.button.BUTTON,
          disabledFun,
          bAuthority: BUTTON_CODE.b_folder_file_move,
          confirmFun: (formInfo, that) => {
            myThis.selectFolder = {folderId: '', isVisible: true, type: 'move', okText: '移动'};
            myThis.tableFindModel1 = myThis.diskManagementService.selectFolder(myThis.imgConfig, myThis.createFolderComponent1, (label) => {
              myThis.selectFolder.folderId = label.folderId;
            }, myThis.diskId);
          }
        },
        {
          name: '打包',
          type: COMMON_TYPE.button.BUTTON,
          disabledFun,
          bAuthority: BUTTON_CODE.b_folder_file_pack,
          confirmFun: (formInfo, that) => {
            const selectedArray = that.getSelectedArray();
            const fileIds = [];
            const folderIds = [];
            selectedArray.forEach(dataModel => {
              if (dataModel.isFile) {
                fileIds.push(dataModel.data.id);
              } else {
                folderIds.push(dataModel.data.id);
              }
            });
            myThis.package.isVisible = true;
            myThis.package.fileIds = fileIds;
            myThis.package.folderIds = folderIds;
          }
        },
        {
          name: '删除',
          type: COMMON_TYPE.button.BUTTON,
          disabledFun,
          bAuthority: BUTTON_CODE.b_folder_file_delete,
          confirmFun(formInfo, that) {
            myThis.delete(myThis.tableFindModel.getSelectedArray());
          }
        }
      ],
      breadcrumb: {
        breadcrumbFun: (label, i, that) => {
          if (i + 1 < labels.length) {
            labels.splice(i + 1, labels.length);
          }
          that.getData({folderId: labels[i].folderId}, null);
        },
        labels: [] // 面包屑
      },
      tables: [
        {name: '', field: 'checkbox', type: COMMON_TYPE.table.CHECKBOX},
        {
          name: '', field: 'imageUrl', type: COMMON_TYPE.table.IMAGE,
          tdStyle: {cursor: 'pointer'},
          confirmFun: (dataModel, table, that) => {
            if (!dataModel['isFile']) {
              const label = {name: dataModel.data.name, folderId: dataModel.data.id};
              labels.push(label);
              that.getData({folderId: dataModel.data.id}, null);
            } else {
              myThis.showPicture = true;
              myThis.openFile(dataModel.data);
            }
          },
          initFun(dataModel, that) {// dataModel：初始化的数据，that:当前对象
            if (dataModel['isFile']) {
              // 获取后缀
              const suffix = CommonTool.getSuffix(dataModel.data.name);
              if (myThis.diskManagementService.verifyImageSuffix(suffix)) {
                dataModel.data['imageUrl'] = dataModel.data['url'] + myThis.compressUrl;
                myThis.pictureFiles.push(dataModel.data);
              } else {
                const i = dataModel.data.name.lastIndexOf('.');
                const fileSuffix = dataModel.data.name.substring(i + 1, dataModel.data.name.length);
                if (myThis.imgConfig['disk_file_icon_' + fileSuffix]) {
                  dataModel.data['imageUrl'] = myThis.imgConfig['disk_file_icon_' + fileSuffix];
                } else {
                  dataModel.data['imageUrl'] = myThis.imgConfig['disk_icon_file'];
                }
              }
            }
          }
        },
        {
          name: '文件名',
          field: 'name',
          type: COMMON_TYPE.table.TITLE,
          isSort: true,
          tdStyle: {cursor: 'pointer'},
          confirmFun: (dataModel, table, that) => {
            if (!dataModel['isFile']) {
              const label = {name: dataModel.data.name, folderId: dataModel.data.id};
              labels.push(label);
              that.getData({folderId: dataModel.data.id}, null);
            } else {
              myThis.openFile(dataModel.data);
            }
          }
        },
        {
          name: '', field: 'createTime', type: COMMON_TYPE.table.DESCRIPTION,
          confirmFun: (dataModel, table, that) => {
            if (!dataModel['isFile']) {
              const label = {name: dataModel.data.name, folderId: dataModel.data.id};
              labels.push(label);
              that.getData({folderId: dataModel.data.id}, null);
            }
          }
        },
        {
          name: '', field: 'size', type: COMMON_TYPE.table.DESCRIPTION,
          tdStyle: {cursor: 'pointer'},
          confirmFun: (dataModel) => {
            if (!dataModel['isFile']) {
              if (dataModel.data['size'] === '计算') {
                myThis.appService.setLoading(true);
                myThis.diskManagementService.getDiskFolderSize(myThis.diskId, dataModel.data['id']).subscribe(res => {
                  dataModel.data['size'] = CommonTool.getSpace(res.size);
                  myThis.appService.setLoading(false);
                });
              }
            }
          }
        },
        {
          name: '', field: '', type: COMMON_TYPE.table.OPERATE,
          initFun(dataModel, that) {// data：初始化的数据，that:全局this
            if (!dataModel['isFile']) {
              dataModel['cdn'] = false;
              dataModel['download'] = false;
            }
          },
          operates: [
            {
              name: '下载',
              field: 'download',
              bAuthority: BUTTON_CODE.b_file_download,
              confirmFun(dataModel, that) {
                location.href = dataModel.data['url'];
              }
            },
            {
              name: '编辑',
              field: 'edit',
              bAuthority: BUTTON_CODE.b_folder_file_edit,
              confirmFun(dataModel, that) {
                myThis.editFolderName.nativeElement.focus();
                myThis.edit = {
                  id: dataModel.data.id,
                  name: dataModel.data.name,
                  isVisible: true,
                  nameStatus: '',
                  isFile: dataModel['isFile']
                };
              }
            },
            {
              name: 'cdn',
              field: 'cdn',
              bAuthority: BUTTON_CODE.b_file_cdn,
              confirmFun(dataModel, that) {
                myThis.createCdnOk(dataModel.data.id);
              }
            },
            {
              name: '删除',
              field: 'delete',
              bAuthority: BUTTON_CODE.b_folder_file_delete,
              confirmFun(dataModel, that) {
                myThis.delete([dataModel]);
              }
            }
          ]
        }
      ],
      initDataFun(formInfo, sortPage, callback, that) {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        myThis.appService.setLoading(true);
        const req = {
          size: sortPage.size,
          sortField: sortPage.sortField,
          sortRule: sortPage.sortRule,
          likeFields: [],
          folderId: formInfo['folderId']
        };
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.diskManagementService.getDiskFolderFilePage(myThis.diskId, sortPage.current, req).subscribe(res => {
          myThis.appService.setLoading(false);
          const array = [];
          myThis.pictureFiles = [];
          myThis.fileLength = 0;
          myThis.folderLength = 0;
          for (const obj of res['resData']) {
            if (obj.isFile === '0') {
              obj.size = CommonTool.getSpace(obj.size);
              array.push({
                data: obj,
                isFile: true
              });
              myThis.fileLength++;
            } else {
              obj.size = '计算';
              obj['imageUrl'] = myThis.imgConfig['disk_icon_folder'];
              array.push({
                data: obj,
                isFile: false
              });
              myThis.folderLength++;
            }
          }
          // 设置labels
          that.config.breadcrumb.labels = labels;
          // 跳转记录
          if (formInfo['folderId'] !== null) {
            CommonTool.updateUrlParam({
              diskId: myThis.diskId,
              folderId: formInfo['folderId']
            });
          }
          callback(array, {total: res['total']});
        });
      },
      frontPaging: false,
      frontSort: false
    }, null, {folderId});
  }

  /**
   * 打开文件
   */
  public openFile(data) {
    const suffix = CommonTool.getSuffix(data.name);
    if (suffix === '.mp3') {
      this.handleMp3(data.id);
    } else if (data['url']) {
      this.handleFile(data);
    } else {
      this.handleFile(data);
    }
  }

  /**
   * 处理mp3
   */
  public handleMp3(id) {
    this.musicPlayerComponent.setPlayList([]);
    this.tableFindModel.dataModels.forEach((ref) => {
      const suffix = CommonTool.getSuffix(ref.data['name']);
      if (suffix === '.mp3' && !ref.data['url']) {
        const playId = this.musicPlayerComponent.addMusic({
          name: ref.data['name'],
          url: ref.data['url']
        });
        if (id === ref.data['id']) {
          this.showMusicPlay = true;
          this.musicPlayerComponent.play(playId);
        }
      } else if (suffix === '.mp3') {
        const playId = this.musicPlayerComponent.addMusic({
          name: ref.data['name'],
          url: ref.data['url']
        });
        if (id === ref.data['id']) {
          this.showMusicPlay = true;
          this.musicPlayerComponent.play(playId);
        }
      }
    });
  }

  /**
   * 处理文件
   */
  public handleFile(data) {
    const that = this;
    const suffix = CommonTool.getSuffix(data.name);
    if (suffix === '.mp4') {
      this.videoPlay.isVisible = true;
      this.videoPlay.url = data['url'];
    } else if (this.diskManagementService.verifyImageSuffix(suffix)) {
      const imageDom = document.getElementById('image');
      let bool = false;
      let bool1 = false;
      this.pictureShow(imageDom, data['id']);
      if (!this.viewer) {
        this.viewer = new Viewer(imageDom, {
          initialViewIndex: 0,
          loop: false,
          url(image) {
            return that.pictureShowFiles[image.index].url + that.compressUrl1;
          },
          view(e) {
            const id = e.detail.originalImage.id;
            const files = that.pictureFiles;
            if (bool) {
              if ((e.detail.index === 0 || e.detail.index === 10) && files.length > 11) {
                bool = false;
                bool1 = true;
                that.pictureShow(imageDom, id);
              }
            } else {
              bool = true;
            }
          },
          viewed(e) {
            const id = e.detail.originalImage.id;
            that.viewer.viewed = false;
            if (bool1) {
              bool1 = false;
              bool = false;
              if (that.viewer) {
                that.viewer.update();
              }
              for (let i = 0; i < that.pictureShowFiles.length; i++) {
                if (id === that.pictureShowFiles[i]['id']) {
                  that.viewer.indent = i;
                  that.viewer.view(i);
                }
              }
            }
          }
        });
      } else {
        this.viewer.update();
      }
      for (let i = 0; i < that.pictureShowFiles.length; i++) {
        if (data['id'] === that.pictureShowFiles[i]['id']) {
          that.viewer.view(i);
        }
      }
      this.viewer.show();
    }
  }

  public pictureShow(image, id) {
    let array = [];
    const files = this.pictureFiles;
    for (let i = 0; i < files.length; i++) {
      if (id === files[i]['id']) {
        let index = 0;
        let index1 = 5;
        for (let j = 0; j < 11; j++) {
          let file;
          if (j < 5) {
            if (i < 5) {
              file = files[index];
              index++;
            } else {
              file = files[i - index1];
              index1--;
            }
          } else if (j === 5 && i < 5) {
            if (i < 5) {
              file = files[index];
              index++;
            } else {
              file = files[i];
              index1 = 1;
            }
          } else {
            if (i < 5) {
              file = files[index];
              index++;
            } else {
              file = files[i + index1];
              index1++;
            }
          }
          if (file) {
            array.push(file);
          }
        }
      }
    }
    image.innerHTML = '';
    if (array.length < 11 && files.length > 11) {
      files.reverse();
      array = [];
      for (let i = 0; i < 11; i++) {
        array.push(files[i]);
      }
      array.reverse();
      files.reverse();
    }
    for (let i = 0; i < array.length; i++) {
      const file = array[i];
      const ul = document.createElement('ul');
      const img = document.createElement('img');
      img.id = file.id;
      img['index'] = i;
      img.src = file.url + this.compressUrl;
      ul.appendChild(img);
      image.appendChild(ul);
    }
    this.pictureShowFiles = array;
  }

  /**
   * 打包提交
   */
  public packageOk(pack) {
    this.appService.setLoading(true);
    let name = pack.name;
    if (name.indexOf('.zip') !== -1) {
      name = name.slice(0, name.indexOf('.zip'));
    }
    this.diskManagementService.createDiskFolderFilePack(this.diskId, {
      fileIds: pack.fileIds,
      folderIds: pack.folderIds,
      name,
      folderId: this.tableFindModel.formInfo['folderId'],
      format: 'zip'
    }).subscribe(res => {
      pack.name = '';
      pack.isVisible = false;
      this.tableFindModel.getData(null, null);
      this.message.success('打包成功！');
    });
  }

  /**
   * 移动复制文件及文件夹
   */
  public copyMoveFolderFile() {
    this.appService.setLoading(true);
    const selectedArray = this.tableFindModel.getSelectedArray();
    const fileIds = [];
    const folderIds = [];
    selectedArray.forEach(dataModel => {
      if (dataModel.isFile) {
        fileIds.push(dataModel.data.id);
      } else {
        folderIds.push(dataModel.data.id);
      }
    });
    const req = {
      fileIds,
      folderIds,
      folderId: this.selectFolder.folderId
    };
    if (this.selectFolder.type === 'copy') {
      this.diskManagementService.createDiskFolderFile(this.diskId, req).subscribe(res => {
        this.selectFolder.isVisible = false;
        this.tableFindModel.getData(null, null);
        this.message.success('复制成功！');
      });
    } else {
      this.diskManagementService.updateDiskFolderFile(this.diskId, req).subscribe(res => {
        this.selectFolder.isVisible = false;
        this.tableFindModel.getData(null, null);
        this.message.success('移动成功！');
      });
    }
  }

  /**
   * 验证文件目录输入框
   * 参数 data 数据
   * 参数 field 字段
   */
  public verifyInput(data, field) {
    if (!CommonTool.notNull(data[field].trim())) {
      return true;
    }
    const pattern = new RegExp('[/\\\\:*?"<>|]');
    if (pattern.test(data[field])) {
      data[field + 'Status'] = 'error';
      return true;
    } else {
      data[field + 'Status'] = '';
      return false;
    }
  }

  /**
   * 修改文件名称
   */
  public updateFileFolder(edit) {
    this.appService.setLoading(true);
    if (edit.isFile) {
      this.diskManagementService.updateDiskFile(this.diskId, edit.id, {name: edit.name}).subscribe(() => {
        edit.isVisible = false;
        this.tableFindModel.getData(null, null);
        this.message.success('修改成功！');
      });
    } else {
      this.diskManagementService.updateDiskFolder(this.diskId, edit.id, {name: edit.name}).subscribe(() => {
        edit.isVisible = false;
        this.tableFindModel.getData(null, null);
        this.message.success('修改成功！');
      });
    }

  }

  /**
   * 共享文件关闭弹窗
   */
  public fileShareCancel() {
    this.share = {
      isVisible: false,
      fileIds: [],
      folderIds: [],
      okDisabled: false,
      needCode: '0',
      validTime: '0',
      remark: ''
    };
  }

  /**
   * 分享文件
   */
  public fileShare() {
    this.appService.setLoading(true);
    this.diskManagementService.createDiskShare(this.diskId, {
      fileIds: this.share.fileIds,
      folderIds: this.share.folderIds,
      needCode: this.share.needCode,
      validTime: this.share.validTime,
      remark: this.share.remark
    }).subscribe((res) => {
      this.shareUrl.url = PAGE_URLS.shareUrl + res.code;
      this.shareUrl.isVisible = true;
      this.shareUrl.drawCode = res.drawCode;
      this.fileShareCancel();
      this.appService.setLoading(false);
    });
  }

  /**
   * 确认创建弹窗
   */
  public createFolderOk() {
    this.tableFindModel.getData(null, null);
  }

  /**
   * 确认创建弹窗
   */
  public createFolderOk1() {
    this.tableFindModel1.getData(null, null);
  }

  /**
   * 创建cdn确认回调
   */
  public createCdnOk(fileId) {
    this.appService.setLoading(true);
    this.diskManagementService.createCdnUrl(this.diskId, fileId).subscribe(res => {
      this.cdn.isVisible = true;
      this.cdn.url = res.cdnUrl;
      this.appService.setLoading(false);
    });
  }

  /**
   * 删除
   * 参数 data 数据
   */
  public delete(dataModels) {
    let index = 0;
    this.modalService.confirm({
      nzTitle: '确认要删除文件/目录吗?',
      nzContent: '<b style="color: red;">文件/目录删除后将无法找回哦！</b>',
      nzOkText: '确认',
      nzOkType: 'danger',
      nzOnOk: () => {
        this.appService.setLoading(true);
        for (const dataModel of dataModels) {
          if (dataModel.isFile) {
            this.diskManagementService.deleteFile(this.diskId, dataModel.data.id).subscribe(res => {
              index++;
              if (index === dataModels.length) {
                this.message.success('删除成功！');
                this.tableFindModel.getData(null, null);
              }
            });
          } else {
            this.diskManagementService.deleteFolder(this.diskId, dataModel.data.id).subscribe(res => {
              index++;
              if (index === dataModels.length) {
                this.message.success('删除成功！');
                this.tableFindModel.getData(null, null);
              }
            });
          }
        }
      },
      nzCancelText: '取消'
    });
  }

  /**
   * 视频播放
   */
  public playMovie(url) {
    this.videoPlay.isVisible = true;
    this.videoPlay.url = url;
  }

  /**
   * 上传文件初始化
   */
  public uploadInit() {
    const myThis = this;
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    this.tableFindModel2.initConfig({
      tables: [
        {name: '名称', field: 'name', type: COMMON_TYPE.table.TEXT},
        {name: '大小', field: 'size', type: COMMON_TYPE.table.TEXT},
        {name: '进度', field: 'progress', type: COMMON_TYPE.table.PROGRESS},
        {
          name: '操作', field: '', type: COMMON_TYPE.table.OPERATE,
          initFun(dataModel, that) {// data：初始化的数据，that:全局this
            dataModel['operateCancel'] = true;
            dataModel['operateRetry'] = false;
            if (dataModel['upload'].isStop) {
              dataModel['operateStart'] = true;
              dataModel['operateStop'] = false;
            } else {
              dataModel['operateStart'] = false;
              dataModel['operateStop'] = true;
            }
          },
          operates: [
            {
              name: '暂停', field: 'operateStop', confirmFun(dataModel, table, that) {
                dataModel['operateStart'] = true;
                dataModel['operateStop'] = false;
                dataModel['upload'].stop();
              }
            },
            {
              name: '开始', field: 'operateStart', confirmFun(dataModel, table, that) {
                dataModel['operateStart'] = false;
                dataModel['operateStop'] = true;
                dataModel['upload'].start().then(res => {
                  dataModel['progress']['fastFinish'] = res['fastFinish'];
                }).catch(res => {
                  dataModel['progress']['status'] = 'exception';
                  dataModel['operateRetry'] = true;
                  if (res) {
                    const message = res.response ? JSON.parse(res.response).message : '';
                    myThis.message.error('上传失败：' + message);
                  } else {
                    myThis.message.error('上传失败！');
                  }
                });
              }
            },
            {
              name: '重试', field: 'operateRetry', confirmFun(dataModel, table, that) {
                dataModel['operateStop'] = true;
                dataModel['operateRetry'] = false;
                dataModel['upload'].retry().then(res => {
                  dataModel['progress']['fastFinish'] = res['fastFinish'];
                }).catch(res => {
                  dataModel['progress']['status'] = 'exception';
                  dataModel['operateRetry'] = true;
                  if (res) {
                    const message = res.response ? JSON.parse(res.response).message : '';
                    myThis.message.error('上传失败：' + message);
                  } else {
                    myThis.message.error('上传失败！');
                  }
                });
              }
            },
            {
              name: '取消', field: 'operateCancel', confirmFun(dataModel, table, that) {
                dataModel['upload'].stop();
                that.dataModels.splice(dataModel.data.index, 1);
                const array = [];
                that.dataModels.forEach((model, index) => {
                  model.data.index = index;
                  array.push(model);
                });
                that.dataModels = array;
              }
            }
          ]
        }
      ],
      paging: {
        limits: [] // 每页多少数据的可选参数
      },
      finds: [],
      buttons: [
        {
          name: '全部开始',
          type: COMMON_TYPE.button.MIDDLING,
          confirmFun(formInfo, that) {
            for (const dataModel1 of that.dataModels) {
              dataModel1['upload'].start().then(res => {
                dataModel1['progress']['fastFinish'] = res['fastFinish'];
              }).catch(res => {
                dataModel1['progress']['status'] = 'exception';
                dataModel1['operateRetry'] = true;
                if (res) {
                  const message = res.response ? JSON.parse(res.response).message : '';
                  myThis.message.error('上传失败：' + message);
                } else {
                  myThis.message.error('上传失败！');
                }
              });
            }
          }
        },
        {
          name: '全部暂停',
          type: COMMON_TYPE.button.MIDDLING,
          confirmFun(formInfo, that) {
            for (const dataModel1 of that.dataModels) {
              dataModel1['upload'].stop();
            }
          }
        }],
      initDataFun: null,
      openPaging: true,
      frontSort: false,
      openFind: false
    });
  }

  /**
   * 关闭上传
   */
  public cancelUpload() {
    this.tableFindModel.getData(null, null);
    for (const dataMode of this.tableFindModel2.dataModels) {
      dataMode['isStop'] = true;
    }
    this.tableFindModel2.setPagingModel({total: 0});
    this.tableFindModel2.setDataModels([]);
    this.upload.isVisible = false;
  }

  /**
   * 选择目录
   */
  public uploadFolder() {
    const $this = this;
    const input = document.createElement('input');
    input.style.display = 'none';
    input.setAttribute('multiple', '');
    input.setAttribute('webkitdirectory', '');
    input.type = 'file';
    const event = document.createEvent('MouseEvents');
    event.initEvent('click', true, true); // 这里的click可以换成你想触发的行为
    input.dispatchEvent(event); // 这里的clickME可以换成你想触发行为的DOM结点
    input.onchange = (e) => {
      $this.uploadFolderFile(e.target['files'], $this.tableFindModel.formInfo['folderId']);
    };
  }

  /**
   * 选择文件
   */
  public selectFile() {
    const $this = this;
    const input = document.createElement('input');
    input.style.display = 'none';
    input.type = 'file';
    input.setAttribute('multiple', '');
    const event = document.createEvent('MouseEvents');
    event.initEvent('click', true, true); // 这里的click可以换成你想触发的行为
    input.dispatchEvent(event); // 这里的clickME可以换成你想触发行为的DOM结点
    input.onchange = (e) => {
      $this.uploadFolderFile(e.target['files'], $this.tableFindModel.formInfo['folderId']);
    };
  }

  /**
   * 上传文件数据处理
   */
  public async uploadFolderFile(files, folderId) {
    const myThis = this;
    const tokenModel = LoginTool.getToken();
    this.count = 0;
    this.upload.isVisible = true;
    const array = [];
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      // 获取文件目录
      let folderPath = '';
      if (file.webkitRelativePath !== '' && file.webkitRelativePath !== null && file.webkitRelativePath !== undefined) {
        const arrayPath = file.webkitRelativePath.split('/');
        arrayPath.pop();
        folderPath = arrayPath.join('/');
      }
      const COMMON_TYPE = TableFindModel.COMMON_TYPE;
      // 封装数据
      const dataModel = {
        data: {
          index: i,
          name: file.name,
          size: CommonTool.getSpace(file.size),
          progress: '0'
        },
        progress: {
          status: 'normal',
          type: COMMON_TYPE.progress.TEXT
        }
      };
      const uploadTool = new UploadTool(null);
      uploadTool.setConfig({
        uploadUrl: this.diskManagementService.getUploadUrl(this.diskId), // 上传的url地址
        token: {
          token: tokenModel.accessToken, // 用户token
          folderId: folderId ? folderId : '' // 文件夹id
        },
        resumed: '0',
        md5Loading: {
          loadingStart(current: number, data: UploadData, that: UploadTool) {
            dataModel.data['progress'] = '0%';
          },
          loadingProgress(current: number, data: UploadData, that: UploadTool) {
            const progress = parseInt((current / (data.file.size / 100)) + '');
            dataModel.data['progress'] = progress + '%';
          },
          loadingEnd() {
            dataModel.data['progress'] = '100%';
          }
        },
        uploadLoading: {
          loadingStart(current: number, data: UploadData, that: UploadTool) {
            dataModel['progress']['status'] = 'active';
            dataModel['progress']['type'] = COMMON_TYPE.progress.PROGRESS;
            dataModel.data['progress'] = '0';
          },
          loadingProgress(current: number, data: UploadData, that: UploadTool) {
            dataModel['progress']['status'] = 'active';
            dataModel.data['progress'] = Math.round(current / data.file.size * 100) + '';
          },
          loadingEnd(current: number, data: UploadData, that: UploadTool) {
            myThis.count++;
            dataModel['progress']['status'] = 'success';
            dataModel['operateStop'] = false;
            dataModel.data['progress'] = '100';
          }
        }
      });
      uploadTool.setFileData({file, folderPath});
      dataModel['upload'] = uploadTool;
      array.push(dataModel);
    }
    this.tableFindModel2.setPagingModel({total: array.length});
    this.tableFindModel2.initDataModels(array);
    for (const dataModel of array) {
      const uploadTool: UploadTool = dataModel['upload'];
      try {
        const res = await uploadTool.start();
        dataModel['progress']['fastFinish'] = res['fastFinish'];
      } catch (res) {
        dataModel['progress']['status'] = 'exception';
        dataModel['operateRetry'] = true;
        if (res) {
          const message = res.response ? JSON.parse(res.response).message : '';
          myThis.message.error('上传失败：' + message);
        } else {
          myThis.message.error('上传失败！');
        }
      }
    }
  }

  /**
   * 复制
   */
  public copy(data) {
    const input = document.createElement('input');
    input.value = data;
    document.body.appendChild(input);
    input.select();
    if (document.execCommand('Copy')) {
      this.message.success('复制成功！');
    } else {
      this.message.success('复制失败！');
    }
    input.style.display = 'none';
  }
}
