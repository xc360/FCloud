import {Component, OnInit} from '@angular/core';
import {HomeService} from '../../home.service';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {AppService} from '../../../app.service';
import {BUTTON_CODE} from '../../../config/button-code';
import {BasicService} from '../../../basic/basic.service';
import {TableDetailModel} from '@ccxc/common';
import {DomSanitizer} from '@angular/platform-browser';
import {CommonTool} from '@ccxc/tool';

@Component({
  selector: 'app-personal-info-details',
  templateUrl: './personal-info-details.component.html',
  styleUrls: ['./personal-info-details.component.scss']
})
export class PersonalInfoDetailsComponent implements OnInit {

  public tableDetailModel: TableDetailModel;
  public userId;
  public editBasicUser: {
    isVisible: boolean
    messageCode: string
    accountType: string
    account: any
    captcha: string
    confirmCaptcha: string
    confirmType: string
  } = {
    isVisible: false,
    messageCode: null,
    accountType: '',
    account: '',
    captcha: '',
    confirmCaptcha: '',
    confirmType: ''
  };
  public captchaInfo: {
    [messageCode: string]: {
      isCaptcha
      interval
      buttonName
      code
      imgUrl,
      captcha,
    }
  } = {};
  public logOffBasic: {
    isVisible: boolean
    messageCode: string
    confirmType: string
    account: string
    captcha: string
  } = {
    isVisible: false,
    messageCode: '',
    confirmType: '',
    account: '',
    captcha: ''
  };
  public userCaptcha: {
    isVisible: boolean
    messageCode: string
    confirmType: string
    accountType: string
    account: string
    captcha: string
  } = {
    isVisible: false,
    messageCode: null,
    confirmType: '',
    accountType: '',
    account: '',
    captcha: ''
  };

  constructor(public homeService: HomeService,
              public message: NzMessageService,
              public appService: AppService,
              public basicService: BasicService,
              private sanitizer: DomSanitizer,
              public modalService: NzModalService) {
    this.tableDetailModel = new TableDetailModel();
    const that = this;
    const COMMON_DETAIL = TableDetailModel.COMMON_DETAIL;
    this.tableDetailModel.setConfig({
      revertName: '返回',
      revertUrl: '/home',
      modules: [
        {name: '基础信息', type: COMMON_DETAIL.header},
        {
          name: '账号', field: 'account', type: COMMON_DETAIL.block,
          operate: {
            name: '注销',
            style: {marginLeft: '5px'},
            bAuthority: BUTTON_CODE.b_delete_user,
            confirmFun(formInfo) {
              that.modalService.confirm({
                nzTitle: '确认要注销吗?',
                nzContent: '<b style="color: red;">注销以后将删除你在本平台的所有数据哦！</b>',
                nzOkText: '确认',
                nzOkType: 'danger',
                nzOnOk: () => {
                  if (formInfo.phone) {
                    that.logOffBasic = {
                      isVisible: true,
                      messageCode: 'phone_unsubscribe',
                      confirmType: 'phone',
                      account: formInfo.phone,
                      captcha: ''
                    };
                  } else if (formInfo.email) {
                    that.logOffBasic = {
                      isVisible: true,
                      messageCode: 'email_unsubscribe',
                      confirmType: 'email',
                      account: formInfo.email,
                      captcha: ''
                    };
                  } else {
                    that.message.info('该账号未绑定手机或邮箱，无法注销！');
                  }
                  const messageCode = that.logOffBasic.messageCode;
                  if (that.captchaInfo[messageCode]) {
                    that.captchaInfo[messageCode].isCaptcha = true;
                    that.captchaInfo[messageCode].buttonName = '获取验证码';
                    clearInterval(that.captchaInfo[messageCode].interval);
                  }
                },
                nzCancelText: '取消'
              });
            }
          }
        },
        {
          name: '邮箱', field: 'email', type: COMMON_DETAIL.block,
          operate: {
            name: '修改邮箱',
            style: {marginLeft: '5px'},
            bAuthority: BUTTON_CODE.b_update_email,
            confirmFun(formInfo) {
              if (formInfo.email) {
                that.userCaptcha = {
                  messageCode: 'email_update_email',
                  isVisible: true,
                  account: formInfo.email,
                  captcha: '',
                  confirmType: 'email',
                  accountType: 'email'
                };
              } else if (formInfo.phone) {
                that.userCaptcha = {
                  messageCode: 'phone_update_email',
                  isVisible: true,
                  account: formInfo.phone,
                  captcha: '',
                  confirmType: 'phone',
                  accountType: 'email'
                };
              } else {
                that.editBasicUser = {
                  messageCode: 'email_update',
                  isVisible: true,
                  account: '',
                  captcha: '',
                  confirmCaptcha: null,
                  confirmType: null,
                  accountType: 'email'
                };
              }
              const messageCode = that.editBasicUser.messageCode;
              if (that.captchaInfo[messageCode]) {
                that.captchaInfo[messageCode].isCaptcha = true;
                that.captchaInfo[messageCode].buttonName = '获取验证码';
                clearInterval(that.captchaInfo[messageCode].interval);
              }
            }
          }
        },
        {
          name: '手机号', field: 'phone', type: COMMON_DETAIL.block,
          operate: {
            name: '修改手机号',
            style: {marginLeft: '5px'},
            bAuthority: BUTTON_CODE.b_update_phone,
            confirmFun(formInfo) {
              if (formInfo.phone) {
                that.userCaptcha = {
                  messageCode: 'phone_update_phone',
                  isVisible: true,
                  account: formInfo.phone,
                  captcha: '',
                  confirmType: 'phone',
                  accountType: 'phone'
                };
              } else if (formInfo.email) {
                that.userCaptcha = {
                  messageCode: 'email_update_phone',
                  isVisible: true,
                  account: formInfo.email,
                  captcha: '',
                  confirmType: 'email',
                  accountType: 'phone'
                };
              } else {
                that.editBasicUser = {
                  messageCode: 'phone_update',
                  isVisible: true,
                  account: '',
                  captcha: '',
                  confirmCaptcha: null,
                  confirmType: null,
                  accountType: 'phone'
                };
              }
              const messageCode = that.editBasicUser.messageCode;
              if (that.captchaInfo[messageCode]) {
                that.captchaInfo[messageCode].isCaptcha = true;
                that.captchaInfo[messageCode].buttonName = '获取验证码';
                clearInterval(that.captchaInfo[messageCode].interval);
              }
            }
          }
        },
        {name: '初始化管理员', field: 'initialAdmin', type: COMMON_DETAIL.dict, dictType: 'whether'},
        {name: '用户状态', field: 'status', type: COMMON_DETAIL.dict, dictType: 'userStatus'}
      ]
    });
    this.getData();
  }

  ngOnInit() {
  }

  /**
   * 切换验证码
   */
  switchImg(messageCode) {
    this.homeService.getMyAppImgCaptcha().subscribe(data => {
      this.captchaInfo[messageCode].code = data['code'];
      const url = URL.createObjectURL(CommonTool.dataURItoBlob(data['imgBytes']));
      this.captchaInfo[messageCode].imgUrl = this.sanitizer.bypassSecurityTrustUrl(url);
    });
  }

  /**
   * 获取数据
   */
  getData() {
    this.homeService.getMyAppUser().subscribe(res => {
      this.userId = res.id;
      this.tableDetailModel.setFormInfo(res);
      if (this.tableDetailModel.formInfo['email']) {
        this.tableDetailModel.setModule('email', 'operate.name', '修改邮箱');
      } else {
        this.tableDetailModel.setModule('email', 'operate.name', '设置邮箱');
      }
      if (this.tableDetailModel.formInfo['phone']) {
        this.tableDetailModel.setModule('phone', 'operate.name', '修改手机号');
      } else {
        this.tableDetailModel.setModule('phone', 'operate.name', '设置手机号');
      }
    });
  }

  /**
   * 获取验证码
   */
  public sendCaptcha(accountType, messageCode, account) {
    if (!this.captchaInfo[messageCode]) {
      this.captchaInfo[messageCode] = {
        isCaptcha: true,
        interval: null,
        buttonName: '获取验证码',
        code: null,
        imgUrl: null,
        captcha: null
      };
    }
    if (this.captchaInfo[messageCode].isCaptcha) {
      this.captchaInfo[messageCode].isCaptcha = false;
      this.appService.setLoading(true);
      const req = {account};
      if (this.captchaInfo[messageCode].code) {
        req['code'] = this.captchaInfo[messageCode].code;
        req['captcha'] = this.captchaInfo[messageCode].captcha;
      }
      this.homeService.createMyAppCaptcha(messageCode, accountType, req).subscribe(res => {
        this.appService.setLoading(false);
        if (res['openImgCaptcha']) {
          this.captchaInfo[messageCode].isCaptcha = true;
          if (this.captchaInfo[messageCode].code) {
            this.message.error('图片验证码错误！');
            return;
          }
          this.switchImg(messageCode);
        } else {
          this.captchaSendSuccess(messageCode);
        }
      }, () => {
        this.captchaInfo[messageCode].isCaptcha = true;
        this.switchImg(messageCode);
      });
    }
  }

  /**
   * 验证码发送成功
   */
  public captchaSendSuccess(messageCode) {
    let index = 60;
    this.captchaInfo[messageCode].interval = setInterval(() => {
      index--;
      if (index <= 0) {
        this.captchaInfo[messageCode].isCaptcha = true;
        this.captchaInfo[messageCode].buttonName = '获取验证码';
        clearInterval(this.captchaInfo[messageCode].interval);
      } else {
        this.captchaInfo[messageCode].buttonName = index + '秒后重试';
      }
    }, 1000);
  }

  /**
   * 获取验证码
   */
  public sendUserCaptcha(messageCode, confirmType) {
    if (!this.captchaInfo[messageCode]) {
      this.captchaInfo[messageCode] = {
        isCaptcha: true,
        interval: null,
        buttonName: '获取验证码',
        code: null,
        imgUrl: null,
        captcha: null
      };
    }
    if (this.captchaInfo[messageCode].isCaptcha) {
      this.captchaInfo[messageCode].isCaptcha = false;
      this.appService.setLoading(true);
      this.homeService.createMyAppUserCaptcha(messageCode, confirmType).subscribe(res => {
        this.appService.setLoading(false);
        this.captchaSendSuccess(messageCode);
      }, () => {
        this.captchaInfo[messageCode].isCaptcha = true;
      });
    }
  }

  /**
   * 修改手机号/邮箱确认提交
   */
  public confirmOk(data) {
    if (data.accountType === 'phone') {
      this.appService.setLoading(true);
      this.homeService.updateMyAppUserPhone({
        phone: data.account,
        captcha: data.captcha,
        confirmCaptcha: data.confirmCaptcha,
        confirmAccountType: data.confirmType
      }).subscribe(res => {
        this.editBasicUser.isVisible = false;
        this.appService.setLoading(false);
        this.tableDetailModel.formInfo.phone = res.phone;
        this.tableDetailModel.setModule('phone', 'operate.name', '修改手机号');
        this.message.success('操作成功！');
      });
    } else {
      this.appService.setLoading(true);
      this.homeService.updateMyAppUserMail({
        email: data.account,
        captcha: data.captcha,
        confirmCaptcha: data.confirmCaptcha,
        confirmAccountType: data.confirmType
      }).subscribe(res => {
        this.editBasicUser.isVisible = false;
        this.appService.setLoading(false);
        this.tableDetailModel.formInfo.email = res.email;
        this.tableDetailModel.setModule('email', 'operate.name', '修改邮箱');
        this.message.success('操作成功！');
      });
    }
  }

  /**
   * 切换手机号或邮箱
   */
  public switchMode(data) {
    const user = this.tableDetailModel.formInfo;
    if (data.confirmType === 'email') {
      if (!user.phone) {
        this.message.success('你的手机号为空，无法切换，请先绑定手机号！');
        return;
      }
      data.confirmType = 'phone';
      data.account = user.phone;
    } else if (data.confirmType === 'phone') {
      if (!user.email) {
        this.message.success('你的邮箱为空，无法切换，请先绑定邮箱！');
        return;
      }
      data.confirmType = 'email';
      data.account = user.email;
    }
    if (data.messageCode === 'phone_unsubscribe') {
      data.messageCode = 'email_unsubscribe';
    } else if (data.messageCode === 'email_unsubscribe') {
      data.messageCode = 'phone_unsubscribe';
    } else if (data.messageCode === 'email_update_email') {
      data.messageCode = 'phone_update_email';
    } else if (data.messageCode === 'phone_update_email') {
      data.messageCode = 'email_update_email';
    } else if (data.messageCode === 'phone_update_phone') {
      data.messageCode = 'email_update_phone';
    } else if (data.messageCode === 'email_update_phone') {
      data.messageCode = 'phone_update_phone';
    }
  }


  /**
   * 确认注销
   */
  public logOffConfirmOk(logOffBasic) {
    this.homeService.deleteMyAppUser(logOffBasic.captcha, logOffBasic.confirmType).subscribe(res => {
      this.logOffBasic.isVisible = false;
      this.appService.setLoading(false);
      this.message.success('操作成功！');
      this.basicService.logOut();
    });
  }

  /**
   * 验证用户
   */
  public verifyUser(data) {
    this.homeService.getMyAppUserCaptcha(data.messageCode, data.captcha, '1').subscribe(res => {
      this.userCaptcha.isVisible = false;
      if (data.accountType === 'email') {
        this.editBasicUser = {
          messageCode: 'email_update',
          isVisible: true,
          account: '',
          captcha: '',
          confirmCaptcha: data.captcha,
          confirmType: data.confirmType,
          accountType: 'email'
        };
      } else if (data.accountType === 'phone') {
        this.editBasicUser = {
          messageCode: 'phone_update',
          isVisible: true,
          account: '',
          captcha: '',
          confirmCaptcha: data.captcha,
          confirmType: data.confirmType,
          accountType: 'phone'
        };
      }
    });
  }
}
