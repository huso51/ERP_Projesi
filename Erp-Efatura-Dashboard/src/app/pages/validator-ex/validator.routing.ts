import { Routes, RouterModule } from '@angular/router';

import { Validator } from './validator.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: Validator,
  }
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
