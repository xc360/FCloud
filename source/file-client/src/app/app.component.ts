import {Component, OnInit} from '@angular/core';
import {AppService} from './app.service';
import {HTTP_URLS} from './config/app-http.url';
import {HomeService} from './home/home.service';
import {environment} from '../environments/environment';
import {Router} from '@angular/router';
import {BasicService} from './basic/basic.service';
import {NzModalService} from 'ng-zorro-antd';
import {CommonTool, DictTool, LoginTool} from '@ccxc/tool';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(public appService: AppService,
              public homeService: HomeService,
              public basicService: BasicService) {
    this.basicService.setConfig();
    this.appService.getMyApp().subscribe(res => {
      this.homeService.setAppInfo(res);
      document.getElementsByTagName('title')[0].innerText = this.homeService.getAppInfoValue('appName');
    });
  }

  ngOnInit(): void {
  }
}
