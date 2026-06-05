import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BaseRouteFilter} from '../basic/basic-route.filter';
import {DefaultPageComponent} from './home-page/default-page.component';
import {HomeComponent} from './home.component';

const routes: Routes = [{
  path: 'home',
  component: HomeComponent,
  canActivate: [BaseRouteFilter],
  children: [
    {
      path: '',
      component: DefaultPageComponent,
    },
    {
      path: 'personalInfo',
      loadChildren: () => import('./personal-info/personal-info.module').then(m => m.PersonalInfoModule)
    },
    {
      path: 'diskManage',
      loadChildren: () => import('./disk-management/disk-management.module').then(m => m.DiskManagementModule)
    }
  ]
}];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class HomeRoutingModule {
}
