import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppTranslationModule } from '../../app.translation.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgaModule } from '../../theme/nga.module';
import { MyDatePickerModule } from 'mydatepicker';

import { TenantsComponent } from './tenants.component';
import { routing } from './tenants.routing';


@NgModule({
  imports: [
    CommonModule,
    AppTranslationModule,
    ReactiveFormsModule,
    FormsModule,
    NgaModule,
    routing,
    MyDatePickerModule,
  ],
  declarations: [
    TenantsComponent,
  ],
})
export class TenantsModule {}
