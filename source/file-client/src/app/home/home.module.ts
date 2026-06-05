import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {
  NzAvatarModule,
  NzBackTopModule,
  NzButtonModule,
  NzDropDownModule, NzFormModule,
  NzIconModule,
  NzInputModule,
  NzLayoutModule,
  NzMenuModule, NzMessageModule,
  NzModalModule
} from 'ng-zorro-antd';
import {DefaultPageComponent} from './home-page/default-page.component';
import {HomeRoutingModule} from './home-routing.module';
import {HomeComponent} from './home.component';
import {ShareFileComponent} from './share-file/share-file.component';
import {FormInfoModule, ListFindModule} from '@ccxc/common';
import {MusicPlayerModule} from '@ccxc/common';
import {PipeModule} from '@ccxc/common';
import {ProgressBallModule} from '@ccxc/common';
import {CreateFolderModule} from './disk-management/create-folder/create-folder.module';
import {OverallSelectModule} from '@ccxc/common';

@NgModule({
  declarations: [HomeComponent, DefaultPageComponent, ShareFileComponent],
  imports: [
    CommonModule,
    FormsModule,
    HomeRoutingModule,
    NzIconModule,
    NzMenuModule,
    NzButtonModule,
    NzLayoutModule,
    NzAvatarModule,
    NzDropDownModule,
    NzModalModule,
    NzInputModule,
    NzBackTopModule,
    PipeModule,
    ListFindModule,
    NzFormModule,
    NzInputModule,
    NzMessageModule,
    MusicPlayerModule,
    ProgressBallModule,
    CreateFolderModule,
    OverallSelectModule,
    FormInfoModule,
  ]
})
export class HomeModule {
}
