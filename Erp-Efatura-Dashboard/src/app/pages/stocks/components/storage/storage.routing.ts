import { Routes, RouterModule } from '@angular/router';

import { StorageComponent } from './storage.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: StorageComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
