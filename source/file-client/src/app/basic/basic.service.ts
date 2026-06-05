import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {HTTP_URLS} from '../config/app-http.url';
import {Node} from './basic.model';
import {Router} from '@angular/router';
import {CommonTool, DictTool, LoginTool} from '@ccxc/tool';
import {environment} from '../../environments/environment';
import {NzModalService} from 'ng-zorro-antd';
import {PAGE_URLS} from '../config/app-page.url';

@Injectable({
  providedIn: 'root'
})
export class BasicService {


  constructor(private http: HttpClient,
              public modalService: NzModalService,
              private router: Router) {

  }

  /**
   * 登录配置
   */
  public setConfig() {
    const that = this;
    LoginTool.setConfig({
      appId: environment.config.appId,
      loginUrl: environment.config.oauthLogin,
      redirectUri: PAGE_URLS.login,
      getToken(param) {
        return new Promise((resolve) => {
          that.getToken(param['code'], param['accessToken']).subscribe(res => {
            resolve(res);
          });
        });
      },
      loginFail(error) {
        return new Promise((resolve) => {
          that.modalService.info({
            nzTitle: error.title,
            nzContent: error.content,
            nzOkText: '确认',
            nzOnOk() {
              resolve(true);
            },
          });
        });
      },
      loginSuccess() {
        that.http.get(HTTP_URLS.getMyAppDictList).subscribe(res => {
          DictTool.setDict(res);
          const redirectUri = localStorage.getItem('redirectUri');
          localStorage.removeItem('redirectUri');
          if (redirectUri) {
            CommonTool.jump(redirectUri);
          } else {
            that.router.navigateByUrl('/home');
          }
        });
      }
    });
  }

  /**
   * 登录
   */
  public login(redirectUri?) {
    if (!redirectUri) {
      redirectUri = location.origin + location.pathname;
    }
    const url = CommonTool.analysisParam('/login', {redirectUri});
    this.router.navigateByUrl(url);
  }

  /**
   * 退出登录
   */
  public logOut(isRefresh?) {
    const that = this;
    LoginTool.logout(() => {
      that.http.delete(HTTP_URLS.deleteMyAppToken).subscribe(res => {
        if (isRefresh) {
          location.reload();
        } else {
          that.router.navigateByUrl('/login');
        }
      }, () => {
        if (isRefresh) {
          location.reload();
        } else {
          that.router.navigateByUrl('/login');
        }
      });
    });
  }

  /**
   *  获取token
   */
  public getToken(code, accessToken) {
    const url = CommonTool.analysisParam(HTTP_URLS.getMyAppToken, {code, accessToken});
    return this.http.get<any>(url);
  }

  /***
   * 获取应用的菜单信息集合
   */
  public getMyAppMenuList(): Observable<any> {
    return this.http.get(HTTP_URLS.getMyAppMenuList);
  }

  /**
   * 菜单处理
   */
  public menuHandle(array) {
    array = this.getMenus(array);
    const nodes = [];
    for (const arr of array) {
      nodes.push({
        node: arr.node,
        parentNode: arr.parentNode,
        seq: arr.seq,
        title: arr.name,
        icon: arr.icon,
        url: arr.url,
        open: false,
        selected: false,
        disabled: false
      });
    }
    return this.getTrees('root', nodes);
  }

  /**
   * 获取菜单
   */
  private getMenus(menus: Array<any>) {
    const result = [];
    for (const menu of menus) {
      if (LoginTool.verifyAuthority(menu.code)) {
        result.push(menu);
      }
    }
    return result;
  }

  /**
   * 获取节点树
   */
  public getTrees(parentNode: string, nodes: Array<Node>) {
    const trees: Array<Node> = [];
    const array = this.getChildrenNodes(parentNode, nodes);
    for (const arr of array) {
      const children = this.getTrees(arr.node, nodes);
      if (children != null && children.length > 0) {
        arr.children = children;
        arr.isLeaf = false;
      } else {
        arr.isLeaf = true;
      }
      trees.push(arr);
    }
    trees.sort((a, b) => {
      if (a.seq != null && b.seq != null) {
        return a.seq - b.seq;
      }
      return 1;
    });
    return trees;
  }

  /**
   * 获取当前节点的子节点集合
   */
  private getChildrenNodes(parentNode, nodes: Array<Node>) {
    const array: Array<Node> = [];
    for (const node of nodes) {
      if (parentNode === node.parentNode) {
        array.push(node);
      }
    }
    return array;
  }
}
