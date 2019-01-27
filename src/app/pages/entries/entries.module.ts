import { NgModule } from '@angular/core';

import { EntriesRoutingModule } from './entries-routing.module';
import { EntryFormComponent } from './entry-form/entry-form.component';
import { EntryListComponent } from './entry-list/entry-list.component';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [EntryFormComponent, EntryListComponent],
  imports: [
    SharedModule,
    EntriesRoutingModule
  ]
})
export class EntriesModule { }
