import { Routes, RouterModule } from '@angular/router';

import { AddressesComponent } from './addresses.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: AddressesComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
