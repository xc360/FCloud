import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FileManagementRoutingModule } from './file-management-routing.module';
import {FileListComponent} from './file-list/file-list.component';
import {ListFindModule} from '@ccxc/common';
import {CreateFolderModule} from '../create-folder/create-folder.module';
import {NgZorroAntdModule} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {MusicPlayerModule} from '@ccxc/common';
import {TableFindModule} from '@ccxc/common';


@NgModule({
  declarations: [FileListComponent],
  imports: [
    CommonModule,
    FormsModule,
    NgZorroAntdModule,
    MusicPlayerModule,
    ListFindModule,
    TableFindModule,
    CreateFolderModule,
    FileManagementRoutingModule
  ]
})
export class FileManagementModule { }
