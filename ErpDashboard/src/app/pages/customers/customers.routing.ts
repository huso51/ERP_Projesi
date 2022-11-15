import { Routes, RouterModule } from '@angular/router';

import { CustomersComponent } from './customers.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: CustomersComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
