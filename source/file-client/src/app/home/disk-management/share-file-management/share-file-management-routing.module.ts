import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {ShareFileListComponent} from './share-file-list/share-file-list.component';


const routes: Routes = [
  {path: '', redirectTo: 'shareFileList', pathMatch: 'full'},
  {path: 'shareFileList', component: ShareFileListComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ShareFileManagementRoutingModule {
}
