import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../../../theme/nga.module';
import { MyDatePickerModule } from 'mydatepicker';

import { InvoicesCameComponent } from './invoicesCame.component';
import { routing } from './invoicesCame.routing';
import { SwitchModule } from 'app/pages/allinvoices/switch.module';


@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    MyDatePickerModule,
    SwitchModule,
    
  ],
  declarations: [
    InvoicesCameComponent,
    
  ],
})
export class InvoicesCameModule {}
