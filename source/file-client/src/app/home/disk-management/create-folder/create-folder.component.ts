import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {AppService} from '../../../app.service';
import {DiskManagementService} from '../disk-management.service';
import {NzMessageService} from 'ng-zorro-antd';
import {CommonTool} from '@ccxc/tool';

@Component({
  selector: 'app-create-folder',
  templateUrl: './create-folder.component.html',
  styleUrls: ['./create-folder.component.scss']
})
export class CreateFolderComponent implements OnInit {
  /**
   * 创建目录
   */
  public createFolder = {
    isVisible: false,
    folderId: '',
    name: '',
    nameStatus: ''
  };
  @Output()
  public createSuccess: EventEmitter<any> = new EventEmitter<any>();
  @ViewChild('createFolderName', {static: true})
  public createFolderName: ElementRef;
  private diskId;

  constructor(public appService: AppService,
              private message: NzMessageService,
              public diskManagementService: DiskManagementService) {
  }

  ngOnInit() {
  }

  /**
   * 确认创建弹窗
   */
  public createFolderOk() {
    this.appService.setLoading(true);
    const req = {
      name: this.createFolder.name
    };
    this.diskManagementService.createFolder(this.diskId, this.createFolder.folderId, req).subscribe(res => {
      this.appService.setLoading(false);
      this.createFolder.name = '';
      this.createFolder.isVisible = false;
      this.message.success('创建成功！');
      this.createSuccess.emit(res);
    });
  }

  /**
   * 打开模板
   */
  public modalOpen(key) {
    this[key].nativeElement.focus();
  }

  /**
   * 验证文件目录输入框
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
   * 创建
   */
  public create(diskId, folderId) {
    this.diskId = diskId;
    this.createFolder = {
      isVisible: true,
      folderId,
      name: '',
      nameStatus: ''
    };
  }
}
