import { Routes, RouterModule } from '@angular/router';

import { EiUsersComponent } from './ei-users.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: EiUsersComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
