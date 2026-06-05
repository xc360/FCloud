import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CreateFolderComponent} from './create-folder.component';
import {FormsModule} from '@angular/forms';
import {NgZorroAntdModule, NzMessageModule} from 'ng-zorro-antd';

@NgModule({
  declarations: [CreateFolderComponent],
  exports: [
    CreateFolderComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgZorroAntdModule,
    NzMessageModule
  ]
})
export class CreateFolderModule {
}
