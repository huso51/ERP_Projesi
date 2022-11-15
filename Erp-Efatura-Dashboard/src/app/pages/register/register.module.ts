import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Register } from './register.component';
import { routing } from './register.routing';
import { BlockUIModule } from 'ng-block-ui';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    BlockUIModule
  ],
  declarations: [
    Register
  ]
})
export class RegisterModule {}
