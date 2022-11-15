import { Routes, RouterModule } from '@angular/router';
import { Pages } from './pages.component';
import { ModuleWithProviders } from '@angular/core';
import { UsersModule } from './users/users.module';
import { AuthGuard } from './authguard';
// noinspection TypeScriptValidateTypes

// export function loadChildren(path) { return System.import(path); };

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: 'app/pages/login/login.module#LoginModule',
    canLoad: [AuthGuard],
  },
  {
    path: 'register',
    loadChildren: 'app/pages/register/register.module#RegisterModule',
    canLoad: [AuthGuard],
  },
  {
    path: 'validator',
    loadChildren: 'app/pages/validator/validator.module#ValidatorModule',
    canLoad: [AuthGuard],
  },
  {
    path: 'pages',
    component: Pages,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule' },
      { path: 'users', loadChildren: './users/users.module#UsersModule' },
      { path: 'tenants', loadChildren: './tenants/tenants.module#TenantsModule' },
      { path: 'customers', loadChildren: './customers/customers.module#CustomersModule' },
      { path: 'addresses', loadChildren: './addresses/addresses.module#AddressesModule' },
      { path: 'stocks', loadChildren: './stocks/stocks.module#StocksModule' },
      { path: 'allinvoices', loadChildren: './allinvoices/allinvoices.module#AllinvoicesModule' },
      { path: 'options', loadChildren: './options/options.module#OptionsModule' },
      { path: 'manage', loadChildren: './manage/manage.module#ManageModule' },
    ]
  }
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
