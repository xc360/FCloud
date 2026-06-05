import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DiskManagementRoutingModule} from './disk-management-routing.module';
import {DiskListComponent} from './disk-list/disk-list.component';
import {TableFindModule} from '@ccxc/common';
import {FormInfoModule} from '@ccxc/common';
import {NzButtonModule, NzFormModule, NzModalModule} from 'ng-zorro-antd';


@NgModule({
  declarations: [DiskListComponent],
  imports: [
    CommonModule,
    NzModalModule,
    DiskManagementRoutingModule,
    TableFindModule,
    FormInfoModule,
    NzFormModule,
    NzButtonModule
  ]
})
export class DiskManagementModule {
}
