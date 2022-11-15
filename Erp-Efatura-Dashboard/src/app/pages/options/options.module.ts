import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { OptionsComponent } from './options.component';
import { routing } from './options.routing';


@NgModule({
  imports: [
    CommonModule,
    
    ReactiveFormsModule,
    FormsModule,
    
    routing,
  ],
  declarations: [
    OptionsComponent,
  ],
})
export class OptionsModule {}
