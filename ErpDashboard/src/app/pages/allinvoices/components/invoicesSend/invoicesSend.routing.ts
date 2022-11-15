import { Routes, RouterModule } from '@angular/router';

import { InvoicesSendComponent } from './invoicesSend.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: InvoicesSendComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
