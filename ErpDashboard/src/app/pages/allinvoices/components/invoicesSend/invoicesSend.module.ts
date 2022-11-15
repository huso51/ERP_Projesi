import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../../../theme/nga.module';
import { MyDatePickerModule } from 'mydatepicker';

import { InvoicesSendComponent } from './invoicesSend.component';
import { routing } from './invoicesSend.routing';
import { JWBootstrapSwitchModule } from 'jw-bootstrap-switch-ng2/src';
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
    InvoicesSendComponent,
  ],
})
export class InvoicesSendModule {}
