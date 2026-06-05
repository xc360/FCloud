// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
// 系统配置
export const environment = {
  production: false,
  appKey: 'ADMIN_INFO', // 应用信息
  tokenKey: 'ADMIN_TOKEN', // token信息
  buttonAuthorityKey: 'BUTTON_AUTHORITY', // 权限信息
  authorityKey: 'AUTHORITY_INFO', // 权限信息
  groupKey: 'GROUP', // 组权限
  dataKey: 'DATA', // 其他数据
  config: window['basicConfig']
};
/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
