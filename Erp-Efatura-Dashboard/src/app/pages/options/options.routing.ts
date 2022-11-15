import { Routes, RouterModule } from '@angular/router';

import { OptionsComponent } from './options.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: OptionsComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
