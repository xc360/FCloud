import {DatePipe} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {CommonTool, LoginTool} from '@ccxc/tool';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [DatePipe]
})
export class LoginComponent implements OnInit {
  /**
   * 路由地址
   */
  private redirectUri: any;

  constructor() {
  }

  ngOnInit(): void {
    // 登录逻辑
    const param = CommonTool.getUrlParam(location.href);
    if (param['redirectUri']) {
      this.redirectUri = param['redirectUri'];
      localStorage.setItem('redirectUri', param['redirectUri']);
    }
    LoginTool.initLogin(null);
  }

  loginJump() {
    localStorage.removeItem('state');
    if (this.redirectUri) {
      localStorage.removeItem('redirectUri');
      CommonTool.jump(this.redirectUri);
    } else {
      LoginTool.initLogin(null);
    }
  }
}
