import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { IMaskModule } from 'angular-imask';
import { BreadCrumbComponent } from './components/bread-crumb/bread-crumb.component';
import { RouterModule } from '@angular/router';
import { PageHeaderComponent } from './components/page-header/page-header.component';
import { FormFieldErrorComponent } from './components/form-field-error/form-field-error.component';
import { ServerErrorMessageComponent } from './components/server-error-message/server-error-message.component';


@NgModule({
  declarations: [
    BreadCrumbComponent, 
    PageHeaderComponent, 
    FormFieldErrorComponent, 
    ServerErrorMessageComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CalendarModule,
    IMaskModule,
    RouterModule
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    CalendarModule,
    IMaskModule,
    RouterModule,
    BreadCrumbComponent,
    PageHeaderComponent,
    FormFieldErrorComponent,
    ServerErrorMessageComponent
  ]
})
export class SharedModule { }
