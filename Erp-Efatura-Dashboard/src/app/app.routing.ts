import { Routes, RouterModule, PreloadAllModules  } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
import { AuthGuard } from 'app/pages/authguard';

export const routes: Routes = [
  { path: '', redirectTo: 'pages', pathMatch: 'full' },
  { path: '**', redirectTo: 'pages/dashboard'  , data: {breadcrumb: 'Anasayfa'}},
  // { path: 'login', loadChildren: 'app/pages/login/login.module#LoginModule'},
  // { path: 'register', loadChildren: 'app/pages/register/register.module#RegisterModule' },
  // { path: '**', component: NotFoundComponent }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);
