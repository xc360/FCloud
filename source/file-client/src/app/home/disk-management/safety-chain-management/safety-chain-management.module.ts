import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SafetyChainManagementRoutingModule } from './safety-chain-management-routing.module';
import {SafetyChainListComponent} from './safety-chain-list/safety-chain-list.component';
import {NgZorroAntdModule} from 'ng-zorro-antd';
import {FormsModule} from '@angular/forms';
import {ListFindModule} from '@ccxc/common';
import {TableFindModule} from '@ccxc/common';
import {CreateFolderModule} from '../create-folder/create-folder.module';


@NgModule({
  declarations: [SafetyChainListComponent],
  imports: [
    CommonModule,
    FormsModule,
    NgZorroAntdModule,
    ListFindModule,
    TableFindModule,
    CreateFolderModule,
    SafetyChainManagementRoutingModule
  ]
})
export class SafetyChainManagementModule { }
