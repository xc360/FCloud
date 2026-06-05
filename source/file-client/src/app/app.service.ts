import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {HTTP_URLS} from './config/app-http.url';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  public loading = false;
  public desc = '加载中...';

  constructor(private http: HttpClient) {
  }

  setLoading(loading, desc?) {
    this.loading = loading;
    if (desc) {
      this.desc = desc;
    } else {
      this.desc = '加载中...';
    }
  }

  getLoading() {
    return this.loading;
  }

  getDesc() {
    return this.desc;
  }

  public getMyApp() {
    return this.http.get(HTTP_URLS.getMyApp);
  }

  public getMyAppInfo() {
    return this.http.get(HTTP_URLS.getMyAppInfo);
  }
}
