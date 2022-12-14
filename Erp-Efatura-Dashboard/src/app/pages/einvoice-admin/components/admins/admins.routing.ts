import { Routes, RouterModule } from '@angular/router';

import { AdminsComponent } from './admins.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: AdminsComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
