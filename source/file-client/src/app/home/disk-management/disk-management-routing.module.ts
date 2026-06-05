import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {DiskListComponent} from './disk-list/disk-list.component';


const routes: Routes = [
  {path: '', redirectTo: 'diskList', pathMatch: 'full'},
  {path: 'diskList', component: DiskListComponent},
  {
    path: 'fileManage',
    loadChildren: () => import('./file-management/file-management.module').then(m => m.FileManagementModule)
  },
  {
    path: 'safetyChainManage',
    loadChildren: () => import('./safety-chain-management/safety-chain-management.module').then(m => m.SafetyChainManagementModule)
  },
  {
    path: 'shareFileManage',
    loadChildren: () => import('./share-file-management/share-file-management.module').then(m => m.ShareFileManagementModule)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DiskManagementRoutingModule {
}
