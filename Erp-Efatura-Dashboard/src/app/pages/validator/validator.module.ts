import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Validator } from './validator.component';
import { routing } from './validator.routing';
import { PopoverModule } from 'ngx-popover';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PopoverModule,
    routing,
  ],
  declarations: [
    Validator,
  ],
})
export class ValidatorModule {}
