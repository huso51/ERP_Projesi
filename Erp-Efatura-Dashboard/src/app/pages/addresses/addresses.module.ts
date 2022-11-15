import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { AddressesComponent } from './addresses.component';
import { routing } from './addresses.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
  ],
  declarations: [
    AddressesComponent,
  ],
})
export class AddressesModule {}
