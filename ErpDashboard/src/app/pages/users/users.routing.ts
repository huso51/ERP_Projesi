import { Routes, RouterModule } from '@angular/router';

import { UsersComponent } from './users.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: UsersComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
