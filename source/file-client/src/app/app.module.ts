// angular
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import zh from '@angular/common/locales/zh';
// 阿里
import {NzSpinModule} from 'ng-zorro-antd/spin';
import {NzAlertModule} from 'ng-zorro-antd/alert';
// 自己
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BaseModule} from './basic/basic.module';
import {BaseRouteFilter} from './basic/basic-route.filter';
import {BaseHttpInterceptor} from './basic/basic-http.Interceptor';
import {PipeModule} from '@ccxc/common';
import {HomeModule} from './home/home.module';
import {ShareFileComponent} from './home/share-file/share-file.component';

registerLocaleData(zh);

@NgModule({
  declarations: [AppComponent],
  imports: [
    BaseModule,
    HomeModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    PipeModule,
    NzSpinModule,
    NzAlertModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: BaseHttpInterceptor, multi: true}, BaseRouteFilter
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
