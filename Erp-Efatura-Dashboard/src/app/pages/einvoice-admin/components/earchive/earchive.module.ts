import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EArchiveComponent } from './earchive.component';
import { routing } from './earchive.routing';
import { MyDatePickerModule } from 'mydatepicker';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MyDatePickerModule,
    routing,
  ],
  declarations: [
    EArchiveComponent,
  ],
})
export class EArchiveModule {}
