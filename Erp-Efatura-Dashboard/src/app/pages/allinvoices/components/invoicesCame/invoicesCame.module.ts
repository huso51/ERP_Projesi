import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MyDatePickerModule } from 'mydatepicker';

import { InvoicesCameComponent } from './invoicesCame.component';
import { routing } from './invoicesCame.routing';
import { SwitchModule } from 'app/pages/allinvoices/switch.module';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    MyDatePickerModule,
    SwitchModule,
    
  ],
  declarations: [
    InvoicesCameComponent,
    
  ],
})
export class InvoicesCameModule {}
