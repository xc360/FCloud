import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PersonalInfoDetailsComponent} from './personal-info-details/personal-info-details.component';

const routes: Routes = [
  {path: '', redirectTo: 'details', pathMatch: 'full'},
  {path: 'details', component: PersonalInfoDetailsComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PersonalInfoRoutingModule {
}
