import { Component, OnInit } from '@angular/core';
import 'rxjs/Rx';
import { Headers, Response, RequestOptions, URLSearchParams, ResponseContentType } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalInvoices } from './default-modal/default-modal.component';
import { IMyDpOptions, IMySelector } from 'mydatepicker';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { Tenant } from './../../../../models/tenant';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Invoice } from 'app/models/invoice';

import { saveAs } from 'file-saver';
import { GibInbox } from 'app/models/gibInbox';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { InvoiceScenario } from 'app/models/invoiceScenario';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'app-invoicesSend',
  templateUrl: './invoicesSend.component.html',
  styleUrls: ['./invoicesSend.component.scss'],
})
export class InvoicesSendComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  header: Headers = new Headers();
  gibOutbox = new Array<GibInbox>();
  options = new RequestOptions({ headers: this.header });
  isEfatura = 'ERP';
  invoices: Invoice[];
  searchInvoiceId = '';
  searchCustomer = '';
  scenarios: InvoiceScenario[];
  selectedProfile: InvoiceScenario;
  searchFirstDate: Object = {
    date: { year: 2015, month: 12, day: 31 },
    formatted: '2015-12-31'
  };
  searchLastDate: Object;
  orderBy = 'desc';
  limit = 20;
  orderSelect = 'created_at';
  offset = 0;
  myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  dateSelector1: IMySelector = {
    open: false,
  };
  dateSelector2: IMySelector = {
    open: false,
  };

  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal,
    private _service: NotificationsService, private route: ActivatedRoute, private router: Router) {
    const date = new Date();
    this.searchLastDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
    };
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['add'] === 'add') {
        this.addCustomer();
      } else if (params['add'] === 'einvoice') {
        this.changeisEfatura();
      }
    });
    this.search();
    this.getScenarios();
  }
  refreshSearch() {
    if (this.isEfatura === 'ERP') {
      this.search();
    } else {
      this.getOutbox();
    }
  }
  openCalendar1() {
    this.dateSelector1 = { open: true };
  }
  openCalendar2() {
    this.dateSelector2 = { open: true };
  }
  onDateChanged(event: Object) {
    this.search();
  }

  changeisEfatura() {
    this.limit = 20;
    this.offset = 0;
    if (this.isEfatura === 'ERP') {
      this.getOutbox();
      this.isEfatura = 'EFatura';
    } else {
      this.isEfatura = 'ERP';
      this.search();
    }
  }

  search() {
    this.invoices = new Array<Invoice>();
    const body = new URLSearchParams();
    body.set('invoiceId', this.searchInvoiceId);
    body.set('customer', this.searchCustomer);
    body.set('whereDateAfter', `${this.searchFirstDate['formatted']} 00:00:00`);
    body.set('whereDateBefore', `${this.searchLastDate['formatted']} 23:59:59`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.offset * this.limit}`);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
    body.set('isComingInvoice', `false`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoices`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.invoices = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );
  }

  getOutbox() {
    const body = new URLSearchParams();
    if  (this.selectedProfile) {
      body.set('profile', `${this.selectedProfile.code}`);
    }
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.offset * this.limit}`);
    body.set('startDate', `${this.searchFirstDate['formatted']}`);
    body.set('endDate', `${this.searchLastDate['formatted']}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getOutbox`, body, this.options).
      subscribe(
      (data) => {
        if (data.data && !data.error) {
          this.gibOutbox = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }
      }, (err) => {
        this._service.error('Hata', 'Gelen Kutusuna erişirken hata oluştu.');
      },
    );
  }

  getScenarios() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoiceScenario`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.scenarios = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }

      });
  }
  isAllChecked(invoices) {
    let asd = true;
    if (invoices) {
      for (const item of invoices) {
        if (item.isChecked && asd) { asd = true; } else {asd = false; } 
      }
      return asd;
    } else {
      return false;
    }
    
    
  }
  selectAll(invoices) {
    const test = this.isAllChecked(invoices);
    for (const item of invoices) {
      item.isChecked = !test;
    }
  }
  addCustomer() {
    const activeModal = this.modalService.open(DefaultModalInvoices, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  showInvoice(index) {
    const activeModal = this.modalService.open(DefaultModalInvoices, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.componentInstance.id = this.invoices[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

  showInvoiceHtml(index) {
    const activeModal = this.modalService.open(DefaultModalInvoices, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.componentInstance.id = this.invoices[index].id;
    activeModal.componentInstance.isPdf = true;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

  showGibHtml(index) {
    const activeModal = this.modalService.open(DefaultModalInvoices, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.componentInstance.uuid = this.gibOutbox[index].uuid;
    activeModal.componentInstance.isPdf = false;
    activeModal.result.then((data) => {
      // on close
      this.getOutbox();
    }, (reason) => {
      // on dismiss
    });
  }

  sendResponseGib(actionString: string, index) {
    const body = new URLSearchParams();
    body.set('uuid', `${this.gibOutbox[index].uuid}`);
    body.set('action', `${actionString}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/performAction`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.refreshSearch();
        } else {
          this._service.error('Hata', data.error.message);
        }
        
        
      });

  }

  downloadPdf(index) {
    const invoices = new Array<Invoice>();
    if (index === 'liste') {
      for (const item of this.invoices) {
        if (item.isChecked) {
          invoices.push(item);
        }
      }
    } else {
      const invoice = new Invoice();
      invoice.id = this.invoices[index].id;
      invoices.push(invoice);
    }
    if (invoices.length > 0) {
      const body = new URLSearchParams();
      body.set('invoices', JSON.stringify(invoices));
      this.options = new RequestOptions({ headers: this.header, responseType: ResponseContentType.Blob });
      this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/invoices/getInvoicePdf`, body, this.options).
        subscribe(
        (data) => {
          if (data.ok && data.status === 200 && data.blob().size > 0) {
            const blob = new Blob([data.blob()], { 'type': 'application/octet-stream' });
            const url = window.URL.createObjectURL(blob);
            saveAs(blob, 'fatura.zip');
            this._service.success('Başarılı', 'Faturalar zip olarak indirildi');
          } else {
            this._service.error('Hata', 'Hata Oluştu');
          }
          // window.open(url);
          this.options = new RequestOptions({ headers: this.header });
        }, (err) => {
          this._service.error('Hata', 'Hata Oluştu');
          this.options = new RequestOptions({ headers: this.header });
        },
      );
    } else {
      this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
    }
  }

  downloadGibPdf(index, type: string) {
    let invoices = '';
    let typeStr: string;
    if (type === 'xlsx') { typeStr = 'excel'; } else { typeStr = type; }
    if (index === 'liste') {
      for (const item of this.gibOutbox) {
        if (item.isChecked) {
          invoices += item.uuid +  ',';
        }
      }
      if (invoices.length > 1) { invoices = invoices.slice(0, invoices.length - 1); }
    } else {
      invoices = this.gibOutbox[index].uuid;
    }
    if (invoices.length > 0) {
      const body = new URLSearchParams();
      body.set('uuid', invoices);
      body.set('isOutbox', `${true}`);
      body.set('fileType', typeStr.toUpperCase());
      this.options = new RequestOptions({ headers: this.header, responseType: ResponseContentType.Blob });
      this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/gib/getInvoiceFile`, body, this.options).
        subscribe(
        (data) => {
          if (data.ok && data.status === 200 && data.blob().size > 0) {
            const blob = new Blob([data.blob()], { 'type': 'application/octet-stream' });
            const url = window.URL.createObjectURL(blob);
            if (index === 'liste'  && type !== 'xlsx') { 
              saveAs(blob, 'faturalar.zip');
            } else if (index === 'liste' && type === 'xlsx') {
              saveAs(blob, `${type.toUpperCase()}_faturalar.${type}`);
            } else { saveAs(blob, `${type.toUpperCase()}_${this.gibOutbox[index].invoiceId}.${type}`); }
            this._service.success('Başarılı', 'Faturalar zip olarak indirildi');
          } else {
            this._service.error('Hata', 'Hata Oluştu');
          }
          this.options = new RequestOptions({ headers: this.header });
        }, (err) => {
          this.options = new RequestOptions({ headers: this.header });
          this._service.error('Hata', 'Hata Oluştu');
        },
      );
    } else {
      this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
    }
  }

  emailSelecteds(index) {
    let invoices = new Array<Invoice>();
    if (index === 'liste') {
      for (let item of this.invoices) {
        if (item.isChecked) {
          invoices.push(item);
        }
      }
    } else {
      let invoice = new Invoice();
      invoice.id = this.invoices[index].id;
      invoices.push(invoice);
    }
    if (invoices.length > 0) {
      const body = new URLSearchParams();
      body.set('invoices', JSON.stringify(invoices));
      // this.options = new RequestOptions({ headers: this.header });
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/sendToCustomers`, body, this.options).
        subscribe(
        (data) => {
          if (!data.error) {
            this._service.success('Başarılı', data.success.message);
          } else {
            this._service.error('Hata', data.error.message);
          }
        }, (err) => {
          this._service.error('Hata', 'Hata Oluştu');
        },
      );
    } else {
      this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
    }
  }

  deleteSelecteds(index) {
    let invoices = new Array<Invoice>();
    if (index === 'liste') {
      for (let item of this.invoices) {
        if (item.isChecked) {
          invoices.push(item);
        }
      }
    } else {
      let invoice = new Invoice();
      invoice.id = this.invoices[index].id;
      invoices.push(invoice);
    }
    if (invoices.length > 0) {
      const body = new URLSearchParams();
      body.set('invoices', JSON.stringify(invoices));
      // this.options = new RequestOptions({ headers: this.header });
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/deleteInvoice`, body, this.options).
        subscribe(
        (data) => {
          if (!data.error) {
            this._service.success('Başarılı', data.success.message);
            this.refreshSearch();
          } else {
            this._service.error('Hata', data.error.message);
          }
        }, (err) => {
          this._service.error('Hata', 'Hata Oluştu');
        },
      );
    } else {
      this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
    }
  }
  sendSelecteds(index) {
    let invoices = new Array<Invoice>();
    let hasReceiver: boolean = true;
    if (index === 'liste') {
      for (let item of this.invoices) {
        if (!item.receiverIdentifier && item.invoiceScenario.code !== 'EARSIVFATURA') {

          hasReceiver = false;
        } else {
          if (item.isChecked) {
            invoices.push(item);
          }
        }
      }
    } else {
      if (!this.invoices[index].receiverIdentifier && this.invoices[index].invoiceScenario.code !== 'EARSIVFATURA') {
        hasReceiver = false;
      } else {

        let invoice = new Invoice();
        invoice = this.invoices[index];
        invoices.push(invoice);
      }
    }
    if (hasReceiver) {
      if (this.invoices.length > 0) {
        const body = new URLSearchParams();
        body.set('invoices', JSON.stringify(invoices));
        // this.options = new RequestOptions({ headers: this.header });
        this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/sendInvoice`, body, this.options).
          subscribe(
          (data) => {
            if (!data.error) {
              this._service.success('Başarılı', data.success.message);
            } else {
              this._service.error('Hata', data.error.message);
            }
          }, (err) => {
            this._service.error('Hata', 'Hata oluştu');
          },
        );
      } else {
        this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
      }
      
    } else {
      this._service.error('Hata', 'Seçili faturalar arasında alıcı posta kutusu mevcut olmayanlar var.');
    }
  }
}
