import { Routes, RouterModule } from '@angular/router';

import { EiUsersComponent } from './components/users/ei-users.component';
import { ModuleWithProviders } from '@angular/core';
import { EinvoiceComponent } from 'app/pages/einvoice-admin';
import { AdminsComponent } from './components/admins/admins.component';
import { CreditsComponent } from './components/credits/credits.component';
import { EArchiveComponent } from './components/earchive/earchive.component';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: EinvoiceComponent,
    children: [
      { path: 'admin', component: AdminsComponent },
      { path: 'user', component: EiUsersComponent },
      { path: 'credit', component: CreditsComponent },
      { path: 'earchive', component: EArchiveComponent },
    ],
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
