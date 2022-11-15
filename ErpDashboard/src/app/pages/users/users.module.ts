import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';
import { MyDatePickerModule } from 'mydatepicker';

import { UsersComponent } from './users.component';
import { routing } from './users.routing';


@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    MyDatePickerModule,
  ],
  declarations: [
    UsersComponent,
  ],
})
export class UsersModule {}
