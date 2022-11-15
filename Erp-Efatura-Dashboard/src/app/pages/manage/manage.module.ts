import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { ManageComponent } from './manage.component';
import { routing } from './manage.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
  ],
  declarations: [
    ManageComponent,
  ],
})
export class ManageModule {}
