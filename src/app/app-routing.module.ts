import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuardService } from './shared/services/auth-guard.service';

const routes: Routes = [
  {path: 'tipo-insumo', canActivate: [AuthGuardService], loadChildren:'./pages/categories/categories.module#CategoriesModule'},
  {path: 'insumo', canActivate: [AuthGuardService], loadChildren:'./pages/entries/entries.module#EntriesModule'},
  {path: 'login', component: LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
