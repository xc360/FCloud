import {Component, OnInit, ViewChild} from '@angular/core';
import {AppService} from '../../app.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {NzMessageService} from 'ng-zorro-antd';
import {DiskManagementService} from '../disk-management/disk-management.service';
import {HomeService} from '../home.service';
import Viewer from 'viewerjs';
import {BasicService} from '../../basic/basic.service';
import {CreateFolderComponent} from '../disk-management/create-folder/create-folder.component';
import {MusicPlayerComponent, OverallSelectComponent, TableFindModel} from '@ccxc/common';
import {CommonTool, DictTool, LoginTool} from '@ccxc/tool';
import {PAGE_URLS} from '../../config/app-page.url';

@Component({
  selector: 'app-file-management',
  templateUrl: './share-file.component.html',
  styleUrls: ['./share-file.component.scss']
})
export class ShareFileComponent implements OnInit {
  @ViewChild(MusicPlayerComponent, {static: true})
  public musicPlayerComponent: MusicPlayerComponent;
  public videoPlay = {url: '', isVisible: false};
  public showMusicPlay = false;
  public pictureFiles: Array<any>;
  public pictureShowFiles: Array<any> = [];
  // 保存弹框
  public selectFolder = {folderId: '', isVisible: false, type: '', okText: '保存'};

  // 授权码
  public isDrawCodeVisible = false;
  public drawCode = '';

  // 共享id和父级id
  public shareId;
  public folderId;
  public shareDiskId;
  public visitCode;

  // 文件目录配置
  public COMMON_TYPE;
  public tableFindModel: TableFindModel;
  public tableFindModel1: TableFindModel;
  public viewer;
  public imgConfig = {};
  public code;
  public recommendMovie: Array<{ name, value }> = [];
  public compressUrl = '?open=0&w=30&h=50';
  public compressUrl1 = '?open=0';
  @ViewChild(CreateFolderComponent, {static: true})
  public createFolderComponent: CreateFolderComponent;
  public isDiskVisible = false;
  public diskId;
  @ViewChild(OverallSelectComponent, {static: true})
  public overallSelectComponent: OverallSelectComponent;


  constructor(public diskManagementService: DiskManagementService,
              private router: Router,
              private homeService: HomeService,
              public activateRoute: ActivatedRoute,
              public appService: AppService,
              private message: NzMessageService,
              private basicService: BasicService,
              public activatedRoute: ActivatedRoute) {
    this.tableFindModel = new TableFindModel();
    this.tableFindModel1 = new TableFindModel();
    this.COMMON_TYPE = TableFindModel.COMMON_TYPE;
    this.folderId = 'root';
    this.code = this.activatedRoute.snapshot.params['code'];
    this.initFun();
  }

  public confirmDisk() {
    this.isDiskVisible = false;
    this.selectFolder = {folderId: '', isVisible: true, type: 'copy', okText: '保存'};
    this.tableFindModel1 = this.diskManagementService.selectFolder(this.imgConfig, this.createFolderComponent, (label) => {
      this.selectFolder.folderId = label.folderId;
    }, this.diskId);
  }

  /**
   * 磁盘选中
   */
  public diskSelectedFun($event) {
    this.diskId = $event;
  }

  // 初始化
  ngOnInit(): void {
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
  }

  /**
   * 登录成功回调
   */
  public initFun() {
    // 获取字典信息
    const array = DictTool.getDictList('fileIcon');
    for (const dict of array) {
      this.imgConfig[dict.name] = dict.value;
    }
    this.recommendMovie = DictTool.getDictList('recommendMovie');
    const share = JSON.parse(localStorage.getItem(this.code));
    if (CommonTool.notNull(share)) {
      this.shareId = share.shareId;
      this.shareDiskId = share.diskId;
      this.visitCode = share.visitCode;
      this.getShareParentFolderList();
    } else {
      this.diskManagementService.verifyShareCode(this.code).subscribe(res => {
        if (!res['needDrawCode']) {
          localStorage.setItem(this.code, JSON.stringify(res));
          this.shareDiskId = res.diskId;
          this.shareId = res.shareId;
          this.visitCode = res.visitCode;
          this.getShareParentFolderList();
        } else {
          this.isDrawCodeVisible = true;
        }
      });
    }
  }

  /**
   * 验证提取码
   */
  public verifyDrawCode() {
    const code = this.activatedRoute.snapshot.params['code'];
    this.diskManagementService.verifyShareCode(code, this.drawCode).subscribe(res => {
      if (CommonTool.notNull(res)) {
        this.isDrawCodeVisible = false;
        localStorage.setItem(code, JSON.stringify(res));
        this.shareId = res.shareId;
        this.shareDiskId = res.diskId;
        this.visitCode = res.visitCode;
        this.getShareParentFolderList();
      }
    });
  }

  /**
   * 获取labels
   */
  public getShareParentFolderList() {
    this.activateRoute.queryParamMap.subscribe((params: ParamMap) => {
      const folderId = params.get('folderId');
      if (folderId) {
        this.diskManagementService.getShareParentFolderList(this.shareId, folderId).subscribe(folders => {
          this.init(folderId, folders);
        });
      } else {
        this.init(folderId, []);
      }
    });
  }

  /**
   * 初始化
   */
  public init(folderId, folders) {
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    const myThis = this;
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
          name: '首页',
          field: 'home',
          type: COMMON_TYPE.button.BUTTON,
          confirmFun: () => {
            myThis.router.navigateByUrl('/home');
          }
        },
        {
          name: '登录', field: 'login', type: COMMON_TYPE.button.BUTTON, confirmFun: () => {
            const redirectUri = escape(PAGE_URLS.shareUrl + this.code);
            myThis.basicService.login(redirectUri);
          }
        },
        {
          name: '保存到网盘', field: 'save', type: COMMON_TYPE.button.BUTTON,
          disabledFun: () => {
            let bool = true;
            myThis.tableFindModel.dataModels.forEach(dataModel => {
              if (dataModel.selected) {
                bool = false;
              }
            });
            return bool;
          },
          confirmFun: () => {
            const config = {
              openFun: (initOptionsFun) => {
                // 查询用户应用
                myThis.homeService.getDiskList({}).subscribe(res => {
                  const array = [];
                  for (const data of res) {
                    array.push({
                      name: data.name,
                      value: data.id
                    });
                  }
                  myThis.isDiskVisible = true;
                  initOptionsFun(array);
                });
              }
            };
            myThis.overallSelectComponent.init(config);
          }
        },
        {
          name: '退出登录', field: 'logOut', type: COMMON_TYPE.button.BUTTON,
          confirmFun: () => {
            myThis.basicService.logOut(true);
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
          },
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
                myThis.diskManagementService.getShareFolderSize(myThis.shareId, dataModel.data['id']).subscribe(res => {
                  dataModel.data['size'] = CommonTool.getSpace(res.size);
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
              name: '下载', field: 'download', confirmFun(dataModel, that) {
                if (dataModel.data['url']) {
                  location.href = dataModel.data['url'];
                } else {
                  location.href = dataModel.data['url'];
                }
              }
            }
          ]
        }
      ],
      initDataFun: (formInfo, sortPage, callback, that) => {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        myThis.appService.setLoading(true);
        const req = {
          size: sortPage.size,
          sortField: sortPage.sortField,
          sortRule: sortPage.sortRule,
          likeFields: [],
          visitCode: myThis.visitCode,
          folderId: formInfo['folderId']
        };
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.diskManagementService.getShareFolderFilePage(sortPage.current, req).subscribe(res => {
          myThis.appService.setLoading(false);
          const array = [];
          myThis.pictureFiles = [];
          for (const obj of res['resData']) {
            if (obj.isFile === '0') {
              obj.size = CommonTool.getSpace(obj.size);
              array.push({
                data: obj,
                isFile: true
              });
            } else {
              obj.size = '计算';
              obj['imageUrl'] = myThis.imgConfig['disk_icon_folder'];
              array.push({
                data: obj,
                isFile: false
              });
            }
          }
          // 生成labels
          that.config.breadcrumb.labels = labels;
          // 跳转记录
          if (formInfo['folderId'] !== null) {
            CommonTool.updateUrlParam({folderId: formInfo['folderId']});
          }
          callback(array, {total: res['total']});
        });
      },
      frontPaging: false,
      frontSort: false,
    }, null, {folderId});
    this.isLogin();
  }

  /**
   * 确认创建弹窗
   */
  public createFolderOk() {
    this.tableFindModel1.getData(null, null);
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
   * 检测是否登录
   */
  public isLogin() {
    const tokenModel = LoginTool.getToken();
    const buttons = this.tableFindModel.config.buttons;
    for (const button of buttons) {
      if (button.field === 'logOut') {
        if (CommonTool.notNull(tokenModel)) {
          button.hidden = false;
        } else {
          button.hidden = true;
        }
      } else if (button.field === 'save') {
        if (CommonTool.notNull(tokenModel)) {
          button.hidden = false;
        } else {
          button.hidden = true;
        }
      } else if (button.field === 'home') {
        if (CommonTool.notNull(tokenModel)) {
          button.hidden = false;
        } else {
          button.hidden = true;
        }
      } else if (button.field === 'login') {
        if (!CommonTool.notNull(tokenModel)) {
          button.hidden = false;
        } else {
          button.hidden = true;
        }
      }
    }
  }


  /**
   * 保存目录及文件
   */
  public saveFolderFile() {
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
    this.diskManagementService.createDiskShareFolderFile(this.diskId, this.shareId, req).subscribe(res => {
      this.selectFolder.isVisible = false;
      this.message.success('保存成功!');
      this.appService.setLoading(false);
    });
  }

  public playMovie(url) {
    this.videoPlay.isVisible = true;
    this.videoPlay.url = url;
  }
}
