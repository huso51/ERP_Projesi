import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PopoverModule } from 'ngx-popover';

import { EinvoiceRegisterComponent } from './einvoice-register.component';
import { routing } from './einvoice-register.routing';


@NgModule({
  imports: [
    CommonModule,
    PopoverModule,
    ReactiveFormsModule,
    FormsModule,
    
    routing,
  ],
  declarations: [
    EinvoiceRegisterComponent,
  ],
})
export class EinvoiceRegisterModule {}
