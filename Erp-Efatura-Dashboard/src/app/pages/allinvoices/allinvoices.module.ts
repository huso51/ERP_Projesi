import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { routing } from './allinvoices.routing';

import { AllinvoicesComponent } from './allinvoices.component';
import { MyDatePickerModule } from 'mydatepicker';
import { InvoicesCameModule } from './components/invoicesCame/invoicesCame.module';
import { InvoicesSendModule } from './components/invoicesSend/invoicesSend.module';


@NgModule({
  declarations: [
    AllinvoicesComponent,
  ],
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
    MyDatePickerModule,
    InvoicesCameModule,
    InvoicesSendModule,
  ],
  
})
export class AllinvoicesModule {}
