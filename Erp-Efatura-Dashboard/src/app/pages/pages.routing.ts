
import { AddressesComponent } from './addresses/addresses.component';
import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
import { PagesComponent } from './pages.component';
import { AuthGuard } from 'app/pages/authguard';

export const routes: Routes = [

    {
        path: 'login',
        loadChildren: './login/login.module#LoginModule',
        canLoad: [AuthGuard],
    },
    {
        path: 'register',
        loadChildren: './register/register.module#RegisterModule',
        canLoad: [AuthGuard],
    },
    {
        path: 'validator',
        loadChildren: './validator/validator.module#ValidatorModule',
    },
    /*{
        path: 'add-user',
        loadChildren: './add-user/add-user.module#AddUserModule',
        canLoad: [AuthGuard],
    },*/
    {
        path: 'pages',
        component: PagesComponent,
        canActivate: [AuthGuard],
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule' , data: {breadcrumb: 'Anasayfa'} },
            { path: 'users', loadChildren: './users/users.module#UsersModule' },
            { path: 'tenants', loadChildren: './tenants/tenants.module#TenantsModule' },
            { path: 'customers', loadChildren: './customers/customers.module#CustomersModule' },
            { path: 'addresses', loadChildren: './addresses/addresses.module#AddressesModule' },
            { path: 'stocks', loadChildren: './stocks/stocks.module#StocksModule' },
            { path: 'allinvoices', loadChildren: './allinvoices/allinvoices.module#AllinvoicesModule' },
            { path: 'options', loadChildren: './options/options.module#OptionsModule' },
            { path: 'manage', loadChildren: './manage/manage.module#ManageModule' },
            { path: 'einvoice-register', loadChildren: './einvoice-register/einvoice-register.module#EinvoiceRegisterModule' },
            { path: 'einvoice-admin', loadChildren: './einvoice-admin/einvoice.module#EinvoiceModule' },
            { path: 'accounts', loadChildren: './accounts/accounts.module#AccountsModule' },
            { path: 'spending-types', loadChildren: './spendingtypes/spendingtypes.module#SpendingTypesModule' },
            /*{ path: 'invoiceOperations', loadChildren: 'app/pages/invoiceOperations/invoice-operations.module#InvoiceOperationsModule', data: { breadcrumb: 'Fatura İşlemleri' } },
            { path: 'einvoice', loadChildren: 'app/pages/einvoice/einvoice.module#EinvoiceModule', data: { breadcrumb: 'e-Fatura', url: '/pages/dashboard' } },
            { path: 'earchive', loadChildren: 'app/pages/earchive/earchive.module#EarchiveModule', data: { breadcrumb: 'e-Arşiv', url: '/pages/dashboard' } },
            { path: 'admin-panel', loadChildren: 'app/pages/admin-panel/admin-panel.module#AdminPanelModule', data: { breadcrumb: 'Yönetim Paneli' } },
            { path: 'addresses', component: AddressesComponent, data: { breadcrumb: 'Adresler' } },
            { path: 'customer-list', component: CustomerListComponent, data: { breadcrumb: 'Organizasyon' } },
            { path: 'members' , component: MembersComponent , data: { breadcrumb: 'Kullanıcılar' }},*/
            
        ]
    }

];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
