import {Component, OnInit, ViewChild} from '@angular/core';
import {TableFindModel} from '@ccxc/common';
import {FormInfoModel} from '@ccxc/common';
import {DiskManagementService} from '../disk-management.service';
import {BUTTON_CODE} from '../../../config/button-code';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {AppService} from '../../../app.service';
import {FormInfoComponent} from '@ccxc/common';
import {environment} from '../../../../environments/environment';
import {LoginTool} from '@ccxc/tool';
import {HomeService} from "../../home.service";

@Component({
  selector: 'app-disk-list',
  templateUrl: './disk-list.component.html',
  styleUrls: ['./disk-list.component.scss']
})
export class DiskListComponent implements OnInit {
  public tableFindModel: TableFindModel;
  public formInfoModel: FormInfoModel;
  public diskInfo = {isVisible: false, diskId: null};
  public secretInfo: { isVisible: boolean, id: string, diskNo: string, diskSecret: string };
  @ViewChild(FormInfoComponent, {static: true})
  public formInfoComponent: FormInfoComponent;
  private groupList;
  private userList;

  constructor(private diskManagementService: DiskManagementService,
              public modalService: NzModalService,
              public appService: AppService,
              public homeService: HomeService,
              public message: NzMessageService) {
    this.secretInfo = {isVisible: false, id: '', diskNo: '', diskSecret: ''};
    this.formInfoModel = new FormInfoModel();
    this.formInfoModel = new FormInfoModel();
  }

  ngOnInit(): void {
    this.diskManagementService.getMyAppUserList().subscribe(res => {
      const array = [];
      for (const row of res) {
        array.push({
          name: row['account'],
          value: row['userId']
        });
      }
      this.userList = array;
      if (this.userList && this.groupList) {
        this.init();
      }
    });
    this.diskManagementService.getMyAppUserGroupList().subscribe(res => {
      const array = [{name: '无', value: ''}];
      for (const row of res) {
        array.push({
          name: row['name'],
          value: row['id']
        });
      }
      this.groupList = array;
      if (this.userList && this.groupList) {
        this.init();
      }
    });
  }


  /**
   * 初始化列表数据
   */
  public init() {
    const myThis = this;
    this.tableFindModel = new TableFindModel();
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    this.tableFindModel.initConfig({
      tables: [
        {name: '磁盘编码', field: 'diskNo', type: COMMON_TYPE.table.TEXT},
        {name: '磁盘名称', field: 'name', type: COMMON_TYPE.table.TEXT},
        {name: '签名有效期', field: 'signValidTime', type: COMMON_TYPE.table.TEXT},
        {name: '用户组', field: 'groupId', type: COMMON_TYPE.table.DICT_DESC, options: this.groupList},
        {name: '所属用户', field: 'userId', type: COMMON_TYPE.table.DICT_DESC, options: this.userList},
        {name: '修改时间', field: 'updateTime', type: COMMON_TYPE.table.TEXT, isSort: true},
        {
          name: '操作', field: '', type: COMMON_TYPE.table.OPERATE,
          operates: [
            {
              name: '编辑',
              field: 'edit',
              bAuthority: BUTTON_CODE.b_disk_edit,
              confirmFun(dataModel, that) {
                myThis.diskInfo = {isVisible: true, diskId: dataModel.data.id};
                myThis.initForm({
                  name: dataModel.data.name,
                  groupId: dataModel.data.groupId,
                  userId: dataModel.data.userId,
                  signValidTime: dataModel.data.signValidTime
                });
              }
            },
            {
              name: '查看秘钥',
              field: 'diskSecret',
              bAuthority: BUTTON_CODE.b_disk_query_secret,
              confirmFun(dataModel, that) {
                myThis.appService.setLoading(true);
                myThis.diskManagementService.getDiskSecret(dataModel.data['id']).subscribe(res => {
                  myThis.appService.setLoading(false);
                  myThis.secretInfo = {
                    id: dataModel.data['id'],
                    diskNo: dataModel.data['diskNo'],
                    isVisible: true,
                    diskSecret: res['diskSecret']
                  };
                });
              }
            },
            {
              name: '删除',
              field: 'delete',
              bAuthority: BUTTON_CODE.b_disk_delete,
              confirmFun(dataModel, that) {
                myThis.modalService.confirm({
                  nzTitle: '确认要删除磁盘吗?',
                  nzContent: '<b style="color: red;">磁盘删除后关联的所有数据都将删除！</b>',
                  nzOkText: '确认',
                  nzOkType: 'danger',
                  nzOnOk: () => {
                    myThis.appService.setLoading(true);
                    myThis.diskManagementService.deleteDisk(dataModel.data.id).subscribe(res => {
                      myThis.appService.setLoading(false);
                      myThis.message.success('删除成功！');
                      myThis.tableFindModel.getData(null, null);
                    });
                  },
                  nzCancelText: '取消'
                });
              }
            }
          ]
        }
      ],
      finds: [
        {
          name: '磁盘名称', field: 'name', input: {
            maxLength: 60
          }
        }
      ],
      buttons: [
        {
          name: '新增',
          type: COMMON_TYPE.button.MIDDLING,
          icon: 'plus-circle',
          bAuthority: BUTTON_CODE.b_disk_add,
          confirmFun(formInfo, that) {
            myThis.diskInfo = {isVisible: true, diskId: null};
            myThis.initForm({
              name: '',
              groupId: null,
              signValidTime: 1800000
            });
          }
        }
      ],
      initDataFun(formInfo, sortPage, callback, that) {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        const req = {
          size: sortPage.size,
          likeFields: ['name'],
          sortField: sortPage['sortField'],
          sortRule: sortPage['sortRule']
        };
        if (!req['sortField']) {
          req['sortField'] = 'updateTime';
        }
        if (!req['sortRule']) {
          req['sortRule'] = 'descend';
        }
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.diskManagementService.getDiskPage(sortPage.current, req).subscribe(res => {
          const array = res['resData'].map((data) => {
            return {data};
          });
          callback(array, {total: res['total']});
        });
      },
      frontPaging: false,
      frontSort: false
    });
  }

  /**
   * 初始化表单
   */
  initForm(form) {
    const that = this;
    const tokenModel = LoginTool.getToken();
    this.formInfoModel.initForm({
      components: [
        {
          name: '磁盘名称',
          field: 'name',
          required: true,
          input: {
            maxLength: 60
          }
        },
        {
          name: '所属组',
          field: 'groupId',
          select: {
            options: this.groupList,
            disabled: this.diskInfo.diskId ? form.userId !== tokenModel.userId : false,
            openFun(formInfo, component, $event) {
              if ($event) {
                that.diskManagementService.getMyAppUserGroupList().subscribe(res => {
                  const array = [{name: '无', value: ''}];
                  for (const row of res) {
                    array.push({
                      name: row['name'],
                      value: row['id']
                    });
                  }
                  component.select.options = array;
                });
              }
            }
          }
        },
        {
          name: '签名有效期',
          field: 'signValidTime',
          required: true,
          inputNumber: {
            step: 1,
            min: 0
          }
        }
      ]
    }, form);
  }

  /**
   * 提交方法
   */
  public submitFun() {
    this.appService.setLoading(true);
    const req = {
      name: this.formInfoModel.formInfo.name,
      groupId: this.formInfoModel.formInfo.groupId,
      signValidTime: this.formInfoModel.formInfo.signValidTime,
    };
    if (!this.diskInfo.diskId) {
      this.diskManagementService.createDisk(req).subscribe(res => {
        this.appService.setLoading(false);
        this.diskInfo.isVisible = false;
        this.message.success('创建成功！');
        this.tableFindModel.getData(null, null);
      });
    } else {
      this.diskManagementService.updateDisk(this.diskInfo.diskId, req).subscribe(res => {
        this.appService.setLoading(false);
        this.diskInfo.isVisible = false;
        this.message.success('修改成功！');
        this.tableFindModel.getData(null, null);
      });
    }
  }

  /**
   * 更新秘钥
   */
  public updateSecret(secretInfo) {
    this.modalService.confirm({
      nzTitle: '确定要更新秘钥吗?',
      nzContent: '<b style="color: red;">更新秘钥后无法回退原秘钥哦！</b>',
      nzOkText: '确认',
      nzOkType: 'danger',
      nzOnOk: () => {
        this.appService.setLoading(true);
        this.diskManagementService.updateDiskSecret(secretInfo['id']).subscribe(res => {
          this.appService.setLoading(false);
          this.message.success('更新秘钥成功！');
          secretInfo['diskSecret'] = res['diskSecret'];
        });
      },
      nzCancelText: '取消'
    });
  }
}
