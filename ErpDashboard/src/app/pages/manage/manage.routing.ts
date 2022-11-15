import { Routes, RouterModule } from '@angular/router';

import { ManageComponent } from './manage.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: ManageComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
