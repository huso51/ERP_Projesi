import { Routes, RouterModule } from '@angular/router';

import { SpendingTypesComponent } from './spendingtypes.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: SpendingTypesComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
