import { Routes, RouterModule } from '@angular/router';

import { InvoicesCameComponent } from './invoicesCame.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: InvoicesCameComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
