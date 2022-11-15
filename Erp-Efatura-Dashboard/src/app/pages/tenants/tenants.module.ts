import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MyDatePickerModule } from 'mydatepicker';

import { TenantsComponent } from './tenants.component';
import { routing } from './tenants.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
    MyDatePickerModule,
  ],
  declarations: [
    TenantsComponent,
  ],
})
export class TenantsModule {}
