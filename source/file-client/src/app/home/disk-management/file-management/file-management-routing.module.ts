import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {FileListComponent} from './file-list/file-list.component';


const routes: Routes = [
  {path: '', redirectTo: 'fileList', pathMatch: 'full'},
  {path: 'fileList', component: FileListComponent}
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FileManagementRoutingModule {
}
