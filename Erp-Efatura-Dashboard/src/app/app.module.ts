import { routes } from './pages/validator/validator.routing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { AgmCoreModule } from '@agm/core';
import { CalendarModule } from 'angular-calendar';
import { routing } from './app.routing';
import { AppSettings } from './app.settings';
import { AppComponent } from './app.component';
import { HttpModule } from '@angular/http';
import { PagesModule } from 'app/pages/pages.module';
import { AuthService } from 'app/auth.service';
import { ToastrModule } from 'ngx-toastr';
import { RouterModule } from '@angular/router';
import { DefaultModal } from 'app/pages/users/default-modal/default-modal.component';
import { DefaultModalTenants } from 'app/pages/tenants/default-modal/default-modal.component';
import { DefaultModalCustomers } from 'app/pages/customers/default-modal/default-modal.component';
import { DefaultModalAddresses } from 'app/pages/addresses/default-modal/default-modal.component';
import { DefaultModalCategories } from './pages/stocks/components/categories/default-modal/default-modal.component';
import { DefaultModalItems } from './pages/stocks/components/items/default-modal/default-modal.component';
import { DefaultModalInvoices } from './pages/allinvoices/components/invoicesSend/default-modal/default-modal.component';
import { DefaultModalInvoicesCame } from './pages/allinvoices/components/invoicesCame/default-modal/default-modal.component';
import { DefaultModalCustomizeItem } from './pages/allinvoices/components/customize-item-modal/default-modal.component';
import { CustomFormsModule } from 'ng2-validation';
import { SimpleNotificationsModule } from 'angular2-notifications';
import { MyDatePickerModule } from 'mydatepicker';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { JWBootstrapSwitchModule } from 'jw-bootstrap-switch-ng2';
import { NguiAutoCompleteModule } from '@ngui/auto-complete';
import { PopoverModule } from 'ngx-popover';
import { DefaultModalStorage } from './pages/stocks/components/storage/default-modal/default-modal.component';
import { DefaultModalEinvoiceUser } from './pages/einvoice-admin/components/default-modal-einvoiceuser/default-modal.component';
import { DefaultModalCredits } from './pages/einvoice-admin/components/credits/default-modal/default-modal.component';
import { DefaultModalEarchive } from './pages/einvoice-admin/components/earchive/default-modal/default-modal.component';
import { DefaultModalAccounts } from './pages/accounts/default-modal/default-modal.component';
import { DefaultModalSpendingTypes } from './pages/spendingtypes/default-modal/default-modal.component';
import { DefaultModalCustomerPay } from './pages/customers/customerpay-modal/default-modal.component';
import { DefaultModalAccountActivities } from './pages/accounts/account-activities-modal/default-modal.component';
import { DefaultModalCustomerActivities } from './pages/customers/customer-activities-modal/default-modal.component';
@NgModule({
  declarations: [
    AppComponent,
    DefaultModal,
    DefaultModalTenants,
    DefaultModalCustomers,
    DefaultModalAddresses,
    DefaultModalCategories,
    DefaultModalItems,
    DefaultModalInvoices,
    DefaultModalInvoicesCame,
    DefaultModalCustomizeItem,
    DefaultModalStorage,
    DefaultModalEinvoiceUser,
    DefaultModalCredits,
    DefaultModalEarchive,
    DefaultModalAccounts,
    DefaultModalSpendingTypes,
    DefaultModalCustomerPay,
    DefaultModalAccountActivities,
    DefaultModalCustomerActivities
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpModule,
    FormsModule,
    PagesModule,
    CalendarModule.forRoot(),
    CustomFormsModule,
    ReactiveFormsModule,
    NguiAutoCompleteModule,
    MyDatePickerModule,
    NgbDropdownModule,
    SimpleNotificationsModule.forRoot(),
    JWBootstrapSwitchModule,
    PopoverModule,
    routing,
    ToastrModule.forRoot({
      timeOut: 4000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
      progressBar: true
    })
  ],
  providers: [ AppSettings, AuthService],
  bootstrap: [ AppComponent ],
  entryComponents: [DefaultModal, DefaultModalTenants, DefaultModalCustomers, DefaultModalAddresses, 
    DefaultModalCategories, DefaultModalItems, DefaultModalInvoices, DefaultModalInvoicesCame, 
    DefaultModalCustomizeItem, DefaultModalStorage, DefaultModalEinvoiceUser, DefaultModalCredits, DefaultModalEarchive, DefaultModalAccounts, DefaultModalSpendingTypes,
    DefaultModalCustomerPay, DefaultModalAccountActivities, DefaultModalCustomerActivities],
})
export class AppModule { }
