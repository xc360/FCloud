import {Component, OnInit} from '@angular/core';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {AppService} from '../../../../app.service';
import {DiskManagementService} from '../../disk-management.service';
import {TableFindModel} from '@ccxc/common';
import {CommonTool} from '@ccxc/tool';
import {BUTTON_CODE} from '../../../../config/button-code';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {PAGE_URLS} from '../../../../config/app-page.url';

@Component({
  selector: 'app-file-management',
  templateUrl: './share-file-list.component.html',
  styleUrls: ['./share-file-list.component.scss']
})
export class ShareFileListComponent implements OnInit {
  public tableFindModel: TableFindModel;
  public isUrlVisible: boolean;
  public currentObj: {
    shareUrl
    drawCode
  };
  public diskId;

  constructor(public diskManagementService: DiskManagementService,
              private appService: AppService,
              private modalService: NzModalService,
              public activateRoute: ActivatedRoute,
              private message: NzMessageService) {
    this.tableFindModel = new TableFindModel();
    this.currentObj = {
      shareUrl: '',
      drawCode: ''
    };
    this.activateRoute.queryParamMap.subscribe((params: ParamMap) => {
      this.diskId = params.get('diskId');
      if (this.diskId) {
        this.init();
      }
    });
  }

  // 初始化
  ngOnInit(): void {

  }

  public init() {
    const myThis = this;
    const COMMON_TYPE = TableFindModel.COMMON_TYPE;
    this.tableFindModel.initConfig({
      tables: [
        {name: '名称', field: 'name', type: COMMON_TYPE.table.TEXT, isSort: false},
        {name: '有效期', field: 'validText', type: COMMON_TYPE.table.TEXT, isSort: false},
        {name: '分享时间', field: 'createTime', type: COMMON_TYPE.table.TEXT, isSort: true},
        {name: '下载次数', field: 'downloadNum', type: COMMON_TYPE.table.TEXT, isSort: true},
        {name: '浏览次数', field: 'browseNum', type: COMMON_TYPE.table.TEXT, isSort: true},
        {name: '保存次数', field: 'preserveNum', type: COMMON_TYPE.table.TEXT, isSort: true},
        {name: '备注', field: 'remark', type: COMMON_TYPE.table.TEXT, isSort: false},
        {
          name: '操作', field: '', type: COMMON_TYPE.table.OPERATE,
          operates: [
            {
              name: '取消共享',
              field: 'delete',
              bAuthority: BUTTON_CODE.b_share_delete,
              confirmFun(dataModel, that) {
                myThis.delete(dataModel.data.id);
              }
            }, {
              name: '详情',
              field: 'details',
              bAuthority: BUTTON_CODE.b_share_details,
              confirmFun(dataModel, that) {
                myThis.currentObj = {
                  shareUrl: PAGE_URLS.shareUrl + dataModel.data.code,
                  drawCode: dataModel.data.drawCode
                };
                myThis.isUrlVisible = true;
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
          name: '备注',
          field: 'remark',
          input: {
            maxLength: 255
          }
        }
      ],
      buttons: [],
      initDataFun(formInfo, sortPage, callback, that) {// 数据处理回调 config：配置，dataModels：数据，that：当前对象
        const req = {
          size: sortPage.size,
          sortField: sortPage.sortField,
          sortRule: sortPage.sortRule,
          likeFields: ['name']
        };
        for (const find of that.config.finds) {
          req[find.field] = formInfo[find.field];
        }
        myThis.diskManagementService.getDiskSharePage(myThis.diskId, sortPage.current, req).subscribe(res => {
          myThis.appService.setLoading(false);
          const array = [];
          for (const resData of res.resData) {
            if (resData.validTime === 0) {
              resData.validText = '永久有效';
            } else {
              const date = CommonTool.turnDate(resData.createTime);
              if ((resData.validTime + date.getTime()) < new Date().getTime()) {
                resData.validText = '已失效';
              } else {
                resData.validText = '有效';
              }
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
   * 删除
   *  id 共享id
   */
  public delete(id) {
    this.modalService.confirm({
      nzTitle: '确认要取消共享吗?',
      nzContent: '<b style="color: red;">取消共享后当前地址将无法访问！</b>',
      nzOkText: '确认',
      nzOkType: 'danger',
      nzOnOk: () => {
        this.appService.setLoading(true);
        this.diskManagementService.deleteDiskShare(this.diskId, id).subscribe(res => {
          this.tableFindModel.getData(null, null);
          this.message.success('取消共享成功！');
        });
      },
      nzCancelText: '取消'
    });
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
