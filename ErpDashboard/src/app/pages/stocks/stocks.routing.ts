import { Routes, RouterModule } from '@angular/router';

import { CategoriesComponent } from './components/categories/categories.component';
import { ModuleWithProviders } from '@angular/core';
import { StocksComponent } from 'app/pages/stocks';
import { ItemsComponent } from 'app/pages/stocks/components/items/items.component';

// noinspection TypeScriptValidateTypes
export const routes: Routes = [
  {
    path: '',
    component: StocksComponent,
    children: [
      { path: 'categories', component: CategoriesComponent },
      { path: 'items', component: ItemsComponent },
    ],
  },
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
