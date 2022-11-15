import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MyDatePickerModule } from 'mydatepicker';

import { UsersComponent } from './users.component';
import { routing } from './users.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
    MyDatePickerModule,
  ],
  declarations: [
    UsersComponent,
  ],
})
export class UsersModule {}
