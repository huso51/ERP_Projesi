import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SpendingTypesComponent } from './spendingtypes.component';
import { routing } from './spendingtypes.routing';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
  ],
  declarations: [
    SpendingTypesComponent,
  ],
})
export class SpendingTypesModule {}
