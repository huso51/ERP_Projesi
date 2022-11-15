import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule, NgModelGroup } from '@angular/forms';
import { CustomFormsModule } from 'ng2-validation';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { NgbModule, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { SimpleNotificationsModule } from 'angular2-notifications';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthService } from './auth.service';
import { ChartsModule } from 'ng2-charts';

/*
 * Platform and Environment providers/directives/pipes
 */
import { routing } from './app.routing';

// App is our top level component
import { App } from './app.component';
import { AppState, InternalStateType } from './app.service';
import { GlobalState } from './global.state';
import { NgaModule } from './theme/nga.module';
import { PagesModule } from './pages/pages.module';
import { DefaultModal } from './pages/users/default-modal/default-modal.component';
import { DefaultModalTenants } from './pages/tenants/default-modal/default-modal.component';
import { DefaultModalCustomers } from './pages/customers/default-modal/default-modal.component';
import { DefaultModalAddresses } from './pages/addresses/default-modal/default-modal.component';
import { DefaultModalCategories } from './pages/stocks/components/categories/default-modal/default-modal.component';
import { DefaultModalItems } from './pages/stocks/components/items/default-modal/default-modal.component';
import { DefaultModalInvoices } from 
                  './pages/allinvoices/components/invoicesSend/default-modal/default-modal.component';
import { NguiAutoCompleteModule } from '@ngui/auto-complete';
import { MyDatePickerModule } from 'mydatepicker';
import { DefaultModalInvoicesCame } from 
            './pages/allinvoices/components/invoicesCame/default-modal/default-modal.component';
import { DefaultModalCustomizeItem } from './pages/allinvoices/components/customize-item-modal/default-modal.component';
import { JWBootstrapSwitchModule } from 'jw-bootstrap-switch-ng2';


// Application wide providers
const APP_PROVIDERS = [
  AppState,
  GlobalState,
];

export type StoreType = {
  state: InternalStateType,
  restoreInputValues: () => void,
  disposeOldHosts: () => void,
};

/**
 * `AppModule` is the main entry point into Angular2's bootstraping process
 */
@NgModule({
  bootstrap: [App],
  declarations: [
    App,
    DefaultModal,
    DefaultModalTenants,
    DefaultModalCustomers,
    DefaultModalAddresses,
    DefaultModalCategories,
    DefaultModalItems,
    DefaultModalInvoices,
    DefaultModalInvoicesCame,
    DefaultModalCustomizeItem,
  ],
  imports: [ // import Angular's modules
    BrowserModule,
    HttpModule,
    RouterModule,
    FormsModule,
    CustomFormsModule,
    ReactiveFormsModule,
    NgaModule.forRoot(),
    NgbModule.forRoot(),
    PagesModule,
    routing,
    NguiAutoCompleteModule,
    MyDatePickerModule,
    NgbDropdownModule,
    BrowserAnimationsModule, 
    SimpleNotificationsModule.forRoot(),
    ChartsModule,
    JWBootstrapSwitchModule,
    
  ],
  providers: [ // expose our Services and Providers into Angular's dependency injection
    APP_PROVIDERS,
    AuthService,
  ],
  entryComponents: [DefaultModal, DefaultModalTenants, DefaultModalCustomers, DefaultModalAddresses, 
    DefaultModalCategories, DefaultModalItems, DefaultModalInvoices, DefaultModalInvoicesCame, 
    DefaultModalCustomizeItem],
})

export class AppModule {

  constructor(public appState: AppState) {
  }
}
