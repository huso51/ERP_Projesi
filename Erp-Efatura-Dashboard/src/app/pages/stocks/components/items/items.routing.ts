import { Routes, RouterModule } from '@angular/router';

import { ItemsComponent } from './items.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: ItemsComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
