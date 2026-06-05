import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShareFileManagementRoutingModule } from './share-file-management-routing.module';
import {ShareFileListComponent} from './share-file-list/share-file-list.component';
import {FormsModule} from '@angular/forms';
import {NgZorroAntdModule} from 'ng-zorro-antd';
import {TableFindModule} from '@ccxc/common';


@NgModule({
  declarations: [ShareFileListComponent],
  imports: [
    CommonModule,
    FormsModule,
    NgZorroAntdModule,
    TableFindModule,
    ShareFileManagementRoutingModule
  ]
})
export class ShareFileManagementModule { }
