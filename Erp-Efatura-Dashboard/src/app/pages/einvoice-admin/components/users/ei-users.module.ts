import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EiUsersComponent } from './ei-users.component';
import { routing } from './ei-users.routing';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
  ],
  declarations: [
    EiUsersComponent,
  ],
})
export class EiUsersModule {}
