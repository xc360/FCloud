// 生产环境
// const CONFIG = {
//   url: location.origin,
//   filePath: "",
//   pagePath: "",
//   appId: "xc0019616597",
//   oauthLogin: "https://basic.ccxc.vip/oauth/login"
// };

// 开发环境
const CONFIG = {
  url: "",
  filePath: "http://localhost:8812",
  // url: location.origin,
  // filePath: '/api', // 本项目后台地址
  pagePath: location.origin + '/admin', // 页面路径
  appId: 'xc0019616597', // 应用ID
  oauthLogin: 'https://basic.ccxc.vip/oauth/login' // 单点登录地址
}

window['basicConfig'] = CONFIG
