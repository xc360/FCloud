import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {SafetyChainListComponent} from './safety-chain-list/safety-chain-list.component';


const routes: Routes = [
  {path: '', redirectTo: 'safetyChainList', pathMatch: 'full'},
  {path: 'safetyChainList', component: SafetyChainListComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SafetyChainManagementRoutingModule { }
