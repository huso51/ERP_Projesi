import { Routes, RouterModule } from '@angular/router';

import { TenantsComponent } from './tenants.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: TenantsComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
