import { Routes, RouterModule } from '@angular/router';

import { EArchiveComponent } from './earchive.component';
import { ModuleWithProviders } from '@angular/core';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: EArchiveComponent,
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
