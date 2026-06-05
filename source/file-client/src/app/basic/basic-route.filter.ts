import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {AppService} from '../app.service';
import {BasicService} from './basic.service';
import {HTTP_URLS} from '../config/app-http.url';
import {LoginTool} from '@ccxc/tool';

@Injectable()
export class BaseRouteFilter implements CanActivate {
  constructor(public basicService: BasicService,
              public appService: AppService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const loginDto = LoginTool.getToken();
    if (loginDto) {
      return true;
    } else {
      const tokenModel = LoginTool.getRefreshToken(HTTP_URLS.updateMyAppToken);
      if (tokenModel) {
        return true;
      } else {
        this.basicService.logOut();
        return false;
      }
    }
  }
}
