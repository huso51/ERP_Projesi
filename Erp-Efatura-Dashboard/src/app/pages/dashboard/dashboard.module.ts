import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { DirectivesModule } from '../../theme/directives/directives.module';
import { DashboardComponent } from './dashboard.component';
import { InfoPanelsComponent } from './info-panels/info-panels.component';
import { InfoCardsComponent } from './info-cards/info-cards.component';
import { InvoicePanelsComponent } from './invoice-panels/invoice-panels.component';
import { BlockUIModule } from 'ng-block-ui';


export const routes = [
  { path: '', component: DashboardComponent, pathMatch: 'full' }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    PerfectScrollbarModule,
    NgxChartsModule,
    DirectivesModule,
    HttpModule,
    BlockUIModule
  ],
  declarations: [
    DashboardComponent,
    InfoPanelsComponent,
    InfoCardsComponent,
    InvoicePanelsComponent
  ],
})

export class DashboardModule { }
