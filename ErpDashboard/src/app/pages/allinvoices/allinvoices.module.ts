import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

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
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    MyDatePickerModule,
    InvoicesCameModule,
    InvoicesSendModule,
  ],
  
})
export class AllinvoicesModule {}
