import { NgModule }      from '@angular/core';
import { CommonModule }  from '@angular/common';

import { routing }       from './pages.routing';
import { NgaModule } from '../theme/nga.module';
import { AppTranslationModule } from '../app.translation.module';
import { FetchService } from './fetch.service';
import { AuthGuard } from './authguard';
import { Pages } from './pages.component';




@NgModule({
  imports: [CommonModule, 
    AppTranslationModule, 
    NgaModule, 
    routing,
  ],
  declarations: [Pages], providers: [FetchService, AuthGuard]})
export class PagesModule {
}
