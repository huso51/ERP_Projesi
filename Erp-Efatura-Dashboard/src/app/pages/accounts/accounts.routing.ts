import { Routes, RouterModule } from '@angular/router';

import { AccountsComponent } from './accounts.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: AccountsComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
