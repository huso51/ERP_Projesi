import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';

import { StocksComponent } from './stocks.component';
import { routing } from './stocks.routing';
import { ItemsModule } from './components/items/items.module';
import { CategoriesModule } from './components/categories/categories.module';
import { CustomFormsModule } from 'ng2-validation';


@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    ItemsModule,
    CategoriesModule,
    CustomFormsModule,
  ],
  declarations: [
    StocksComponent,
  ],
})
export class StocksModule {}
