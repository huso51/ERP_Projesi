import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { StocksComponent } from './stocks.component';
import { routing } from './stocks.routing';
import { ItemsModule } from './components/items/items.module';
import { CategoriesModule } from './components/categories/categories.module';
import { CustomFormsModule } from 'ng2-validation';
import { StorageModule } from 'app/pages/stocks/components/storage/storage.module';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    ItemsModule,
    CategoriesModule,
    StorageModule,
    CustomFormsModule,
  ],
  declarations: [
    StocksComponent,
  ],
})
export class StocksModule {}
