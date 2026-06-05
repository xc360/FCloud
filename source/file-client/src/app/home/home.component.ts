import {HttpClient} from '@angular/common/http';
import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {NzIconService, NzMessageService} from 'ng-zorro-antd';
import {environment} from '../../environments/environment';
import {AppService} from '../app.service';
import {HomeService} from './home.service';
import {BasicService} from '../basic/basic.service';
import {BUTTON_CODE} from '../config/button-code';
import {CommonTool, DictTool, LoginTool} from '@ccxc/tool';
import {OverallSelectComponent} from '@ccxc/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  public menus: Array<any>;
  public isCollapsed: boolean;
  public isPassWordVisible: boolean;
  public setupPasswordVisible: boolean;
  public passwordInfo: {
    password: string
    newPassword: string
    againPassword: string,
  };
  public passwordConfig: {
    password: boolean,
    newPassword: boolean
    againPassword: boolean
    passwordType: string,
  };
  public isUpdateSecret: boolean;
  public url = environment.config.url + '/share/page/';
  public tokenModel: any;
  public userInfo: any;
  public BUTTON_CODE = BUTTON_CODE;
  public diskId;
  public diskList = [];
  @ViewChild(OverallSelectComponent, {static: true})
  public overallSelectComponent: OverallSelectComponent;

  constructor(private router: Router,
              private http: HttpClient,
              public appService: AppService,
              public homeService: HomeService,
              public basicService: BasicService,
              public iconService: NzIconService,
              private message: NzMessageService,
              public activatedRoute: ActivatedRoute) {
    let iconUrl = environment.config.iconUrl;
    if (!iconUrl) {
      iconUrl = './assets/icon/iconfont.js';
    }
    this.iconService.fetchFromIconfont({
      scriptUrl: iconUrl
    });
    // 获取用户token信息
    this.tokenModel = LoginTool.getToken();
    // 获取用户信息
    this.userInfo = {};
    this.homeService.getMyAppUser().subscribe((res) => {
      this.userInfo = res;
    });
    this.isPassWordVisible = false;
    this.setupPasswordVisible = false;
    this.passwordInfo = {password: '', newPassword: '', againPassword: ''};
    this.passwordConfig = {password: false, newPassword: false, againPassword: false, passwordType: ''};
    this.isUpdateSecret = false;
  }

  ngOnInit() {
    const that = this;
    const config = {
      openFun: (initOptionsFun) => {
        // 查询用户应用
        that.homeService.getDiskList({}).subscribe(res => {
          that.diskList = res;
          const array = [];
          for (const data of res) {
            array.push({
              name: data.name,
              value: data.id
            });
          }
          initOptionsFun(array);
        });
      }
    };
    this.overallSelectComponent.init(config);
  }

  /**
   * 初始化基础数据
   */
  public initBasicData($event) {
    if (this.diskId !== $event) {
      this.diskId = $event;
      const that = this;
      if (this.diskId) {
        const disk = this.diskList.find((data) => data.id === this.diskId);
        LoginTool.setGroupId(disk.groupId);
      }
      this.basicService.getMyAppMenuList().subscribe(menus => {
        // 菜单处理
        const menuList = that.basicService.menuHandle(menus);
        if (menuList) {
          // 菜单存储
          that.menus = menuList;
          const diskId = this.activatedRoute.snapshot.queryParams.diskId;
          if (this.diskId && diskId !== this.diskId) {
            that.jump('/home');
          }
        }
      });
    }
  }

  /**
   * 验证按钮权限
   */
  public verifyButtonAuthority(code) {
    return LoginTool.verifyAuthority(code);
  }

  /**
   * 打开菜单
   */
  public toggleCollapsed() {
    this.isCollapsed = !this.isCollapsed;
  }

  /**
   * 修改密码关闭
   */
  public modifyPassWordCancel() {
    this.isPassWordVisible = false;
    this.passwordInfo = {password: '', newPassword: '', againPassword: ''};
    this.passwordConfig = {password: false, newPassword: false, againPassword: false, passwordType: ''};
  }

  /**
   * 修改密码提交
   */
  public modifyPassWordOk(data) {
    if (data.newPassword === data.password) {
      this.message.warning('新密码不能和旧密码相同！');
      return;
    }
    this.appService.setLoading(true);
    this.homeService.updateMyAppUserPassword({
      newPassword: data.newPassword,
      password: data.password ? data.password : null
    }).subscribe(() => {
      this.passwordInfo = {
        password: '',
        newPassword: '',
        againPassword: ''
      };
      this.userInfo['passwordExist'] = true;
      this.appService.setLoading(false);
      this.message.success('密码修改成功！');
      this.isPassWordVisible = false;
      this.basicService.logOut();
    });
  }

  /**
   * 退出登录
   */
  public logOut() {
    this.basicService.logOut();
  }

  /**
   * 跳转到个人信息
   */
  public jumpPersonal(url) {
    this.router.navigateByUrl(url);
  }

  /**
   * 复制
   */
  public copy(data, html, doc) {
    const input = document.createElement('input');
    input.value = data;
    document.body.appendChild(input);
    input.select();
    if (document.execCommand('Copy')) {
      doc.style.display = 'none';
      html.style.display = 'inline-block';
    }
    input.style.display = 'none';
  }

  /**
   * 跳转
   */
  public jump(url) {
    if (url) {
      const httpUrl = CommonTool.analysisParam(url, {diskId: this.diskId});
      this.router.navigateByUrl(httpUrl);
    }
  }
}
