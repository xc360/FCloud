import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AppService} from '../app.service';
import {catchError, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';
import {HTTP_URLS} from '../config/app-http.url';
import {LoginTool} from '@ccxc/tool';

@Injectable()
export class BaseHttpInterceptor implements HttpInterceptor {
  /**
   * 刷新失败
   */
  private refreshFail = true;

  constructor(private router: Router,
              public appService: AppService,
              private modalService: NzModalService,
              private message: NzMessageService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let headers = null;
    try {
      const tokenModel = LoginTool.getToken();
      if (tokenModel) {
        headers = req.headers.set('token', tokenModel.accessToken ? tokenModel.accessToken : '');
      }
    } catch (e) {
      LoginTool.clear();
    }
    const authReq = req.clone({headers});
    return next.handle(authReq).pipe(
      catchError((err) => {
        if (err.status === 400 && err.error.code === 'tokenExpire') {
          const loginDto = LoginTool.getRefreshToken(HTTP_URLS.updateMyAppToken);
          if (loginDto) {
            const auth = req.clone({headers: req.headers.set('token', loginDto.accessToken)});
            return next.handle(auth).pipe();
          } else {
            if (this.refreshFail) {
              this.refreshFail = false;
              this.modalService.info({
                nzTitle: '提示',
                nzContent: '<p>' + err.error.message + '</p>',
                nzOkText: '确认',
                nzOnOk: () => {
                  this.refreshFail = true;
                  localStorage.clear();
                  if (this.router.url === '/login') {
                    location.reload();
                  } else {
                    this.router.navigate(['/login']);
                  }
                }
              });
            }
          }
        }
        throw err;
      }),
      tap(res => {
        return res;
      }, err => {
        this.appService.setLoading(false);
        if (err.status === 400 && err.error.code === 'tokenCannotBeEmpty') {
          this.modalService.info({
            nzTitle: '提示',
            nzContent: '<p>token失效，请重新登录！</p>',
            nzOkText: '确认',
            nzOnOk: () => {
              localStorage.clear();
              if (this.router.url === '/login') {
                location.reload();
              } else {
                this.router.navigate(['/login']);
              }
            }
          });
        } else {
          if (err && err.error && err.error.message) {
            this.message.error(err.error.message);
          } else {
            console.error(err);
          }
        }
      }));
  }
}
