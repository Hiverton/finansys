import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {path: 'tipo-insumo', loadChildren:'./pages/categories/categories.module#CategoriesModule'},
  {path: 'insumo', loadChildren:'./pages/entries/entries.module#EntriesModule'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
