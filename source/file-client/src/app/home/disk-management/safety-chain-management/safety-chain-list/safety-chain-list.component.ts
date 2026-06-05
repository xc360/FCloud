import {Component, OnInit, ViewChild} from '@angular/core';
import {AppService} from '../../../../app.service';
import {DiskManagementService} from '../../disk-management.service';
import {CommonTool, DictTool} from '@ccxc/tool';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {TableFindModel} from '@ccxc/common';
import {BUTTON_CODE} from '../../../../config/button-code';
import {CreateFolderComponent} from '../../create-folder/create-folder.component';
import {ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-safety-chain-management',
  templateUrl: './safety-chain-list.component.html',
  styleUrls: ['./safety-chain-list.component.scss']
})
export class SafetyChainListComponent implements OnInit {

  public commonTool = CommonTool;
  public tableFindModel: TableFindModel;
  public tableFindModel1: TableFindModel;
  public isEditVisible = false;
  public selectFolder = {path: null, isVisible: false, okText: ''};
  public formInfo = {id: '', name: '', url: '', path: '', allowSuffix: '', verifySign: true, status: true};
  public imgConfig = {};
  @ViewChild(CreateFolderComponent, {static: true})
  public createFolderComponent: CreateFolderComponent;
  public diskId;

  constructor(public diskManagementService: DiskManagementService,
              private message: NzMessageService,
              public appService: AppService,
              public activateRoute: ActivatedRoute,
              private modalService: NzModalService) {
    // 获取系统信息
    const array = DictTool.getDictList('fileIcon');
    for (const dict of array) {
      this.imgConfig[dict.name] = dict.value;
    }
    this.tableFindModel = new TableFindModel();
    this.tableFindModel1 = new TableFindModel();
    this.activateRoute.queryParamMap.subscribe((params: ParamMap) => {
      this.diskId = params.get('diskId');
      if (this.diskId) {
        this.init();
      }
    });
  }

  ngOnInit() {
  }

  init() {
    const myThis = this;
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    this.tableFindModel.initConfig({
      tables: [
        {name: '规则名称', field: 'name', type: COMMON_TYPE.table.TEXT, isSort: true},
        {name: '文件目录', field: 'path', type: COMMON_TYPE.table.TEXT},
        {name: '链接规则', field: 'url', type: COMMON_TYPE.table.TEXT},
        {name: '验证签名', field: 'verifySign', type: COMMON_TYPE.table.DICT_DESC, dictType: 'whether'},
        {name: '状态', field: 'status', type: COMMON_TYPE.table.DICT_DESC, dictType: 'effectStatus'},
        {
          name: '操作', field: '', type: COMMON_TYPE.table.OPERATE,
          operates: [
            {
              name: '编辑',
              field: 'edit',
              bAuthority: BUTTON_CODE.b_safety_chain_edit,
              confirmFun(dataModel, that) {
                myThis.isEditVisible = true;
                myThis.formInfo = {
                  id: dataModel.data.id,
                  name: dataModel.data.name,
                  path: dataModel.data.path ? dataModel.data.path : 'root',
                  url: dataModel.data.url,
                  allowSuffix: dataModel.data.allowSuffix,
                  verifySign: dataModel.data.verifySign === '0',
                  status: dataModel.data['status'] === '0'
                };
              }
            },
            {
              name: '删除',
              field: 'delete',
              bAuthority: BUTTON_CODE.b_safety_chain_delete,
              confirmFun(dataModel, that) {
                myThis.delete(dataModel.data.id);
              }
            }
          ]
        }
      ],
      finds: [
        {
          name: '名称',
          field: 'name',
          input: {
            maxLength: 255
          }
        },
        {
          name: '地址',
          field: 'url',
          input: {
            maxLength: 255
          }
        },
        {
          name: '状态',
          field: 'status',
          select: {
            dictType: 'effectStatus'
          }
        }
      ],
      buttons: [
        {
          name: '新增',
          type: COMMON_TYPE.button.MIDDLING,
          icon: 'plus-circle',
          bAuthority: BUTTON_CODE.b_safety_chain_add,
          confirmFun(formInfo, that) {
            myThis.formInfo = {
              id: '',
              name: '',
              path: '',
              url: '',
              allowSuffix: '',
              verifySign: true,
              status: true
            };
            myThis.isEditVisible = true;
          }
        }
      ],
      initDataFun(formInfo, sortPage, callback, that) {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        const req = {
          size: sortPage.size,
          sortField: sortPage.sortField,
          sortRule: sortPage.sortRule,
          likeFields: ['name', 'url']
        };
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.diskManagementService.getDiskSafetyChainPage(myThis.diskId, sortPage.current, req).subscribe(res => {
          myThis.appService.setLoading(false);
          const array = [];
          for (const resData of res['resData']) {
            if (!resData['path']) {
              resData['path'] = 'root';
            }
            array.push({data: resData});
          }
          callback(array, {total: res.total});
        });
      },
      frontPaging: false,
      frontSort: false
    });
  }

  /**
   * 选择文件夹
   */
  public selectFolderClick() {
    this.selectFolder = {path: '', isVisible: true, okText: '保存'};
    this.tableFindModel1 = this.diskManagementService.selectFolder(this.imgConfig, this.createFolderComponent, (label) => {
      this.selectFolder.path = label.path;
    }, this.diskId);
  }

  /**
   * 选中文件夹
   */
  public saveFolderFile() {
    this.selectFolder.isVisible = false;
    if (!this.selectFolder.path) {
      this.formInfo['path'] = 'root';
    } else {
      this.formInfo['path'] = this.selectFolder.path;
    }
  }

  /**
   * 提交
   */
  public createOk() {
    this.appService.setLoading(true);
    const req = {
      name: this.formInfo.name,
      path: this.formInfo.path !== 'root' ? this.formInfo.path : '',
      url: this.formInfo.url,
      allowSuffix: this.formInfo.allowSuffix,
      verifySign: this.formInfo.verifySign ? '0' : '1',
      status: this.formInfo.status ? '0' : '1'
    };
    if (CommonTool.notNull(this.formInfo.id)) {
      this.diskManagementService.updateDiskSafetyChain(this.diskId, this.formInfo.id, req).subscribe(res => {
        this.tableFindModel.getData(null, null);
        this.isEditVisible = false;
        this.message.success('更新成功！');
      });
    } else {
      this.diskManagementService.createDiskSafetyChain(this.diskId, req).subscribe(res => {
        this.isEditVisible = false;
        this.tableFindModel.getData(null, null);
        this.message.success('创建成功！');
      });
    }
  }

  /**
   * 删除
   * 参数 id 数据主键
   */
  public delete(id) {
    this.modalService.confirm({
      nzTitle: '确实要删除吗?',
      nzContent: '<b style="color: red;">删除后将无法恢复！</b>',
      nzOkText: '确定',
      nzOnOk: () => {
        this.appService.setLoading(true);
        this.diskManagementService.deleteDiskSafetyChain(this.diskId, id).subscribe(res => {
          this.tableFindModel.getData(null, null);
          this.message.success('删除成功！');
        });
      },
      nzCancelText: '取消'
    });
  }

  /**
   * 确认创建弹窗
   */
  public createFolderOk() {
    this.tableFindModel1.getData(null, null);
  }
}
