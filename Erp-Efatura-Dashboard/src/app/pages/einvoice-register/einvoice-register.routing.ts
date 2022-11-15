import { Routes, RouterModule } from '@angular/router';

import { EinvoiceRegisterComponent } from './einvoice-register.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: EinvoiceRegisterComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
