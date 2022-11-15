import { Routes, RouterModule } from '@angular/router';

import { ModuleWithProviders } from '@angular/core';
import { AllinvoicesComponent } from './allinvoices.component';
import { InvoicesSendComponent } from './components/invoicesSend/invoicesSend.component';
import { InvoicesCameComponent } from 'app/pages/allinvoices/components/invoicesCame/invoicesCame.component';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: AllinvoicesComponent,
    children: [
      { path: 'invoices', component: InvoicesSendComponent },
      { path: 'invoicesCame', component: InvoicesCameComponent },
    ],
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
