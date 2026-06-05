import {environment} from '../../environments/environment';

const PATH = environment.config.url;
let PAGE = PATH + environment.config.pagePath; // 页面基础路径
if (!environment.config.pagePath) {
  PAGE = location.origin;
}
export const PAGE_URLS = {
  /************************其他页面************************/
  shareUrl: PAGE + '/#/share/', // 分享
  login: PAGE + '/#/login' // 重定向到登录
};
