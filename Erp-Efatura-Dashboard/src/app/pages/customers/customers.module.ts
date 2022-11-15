import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { CustomersComponent } from './customers.component';
import { routing } from './customers.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
  ],
  declarations: [
    CustomersComponent,
  ],
})
export class CustomersModule {}
