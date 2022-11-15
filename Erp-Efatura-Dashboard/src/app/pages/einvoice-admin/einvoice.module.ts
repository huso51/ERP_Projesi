import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EinvoiceComponent } from './einvoice.component';
import { routing } from './einvoice.routing';
import { EiUsersModule } from './components/users/ei-users.module';
import { AdminsModule } from './components/admins/admins.module';
import { CustomFormsModule } from 'ng2-validation';
import { CreditsModule } from './components/credits/credits.module';
import { EArchiveModule } from './components/earchive/earchive.module';


@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    EiUsersModule,
    AdminsModule,
    CreditsModule,
    EArchiveModule,
    CustomFormsModule,
  ],
  declarations: [
    EinvoiceComponent,
  ],
})
export class EinvoiceModule {}
