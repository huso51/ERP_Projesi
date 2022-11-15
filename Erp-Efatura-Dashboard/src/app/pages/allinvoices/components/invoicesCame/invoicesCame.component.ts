import { Component, OnInit } from '@angular/core';
import 'rxjs/Rx';
import { Headers, Response, RequestOptions, URLSearchParams, ResponseContentType } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalInvoicesCame } from './default-modal/default-modal.component';
import { IMyDpOptions, IMySelector } from 'mydatepicker';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { Tenant } from './../../../../models/tenant';
import { GibInbox } from '../../../../models/gibInbox';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Invoice } from 'app/models/invoice';
import { saveAs } from 'file-saver';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { InvoiceType } from 'app/models/invoiceType';
import { InvoiceScenario } from 'app/models/invoiceScenario';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-invoicesCame',
  templateUrl: './invoicesCame.component.html',
  styleUrls: ['./invoicesCame.component.scss'],
})
export class InvoicesCameComponent implements OnInit {
  isEfatura = 'ERP';
  gibInbox = new Array<GibInbox>();
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  invoices: Invoice[];
  searchInvoiceId = '';
  searchCustomer = '';
  searchFirstDate: Object = {
    date: { year: 2015, month: 12, day: 31 },
    formatted: '2015-12-31',
  };
  selectedProfile: InvoiceScenario;
  scenarios: Array<InvoiceScenario>;
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
    private _service: NotificationsService, private route: ActivatedRoute) {
    const date = new Date();
    this.searchLastDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' + (date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
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
      this.getInbox();
      this.isEfatura = 'EFatura';
    } else {
      this.isEfatura = 'ERP';
      this.search();
    }
  }

  search() {
    const body = new URLSearchParams();
    body.set('invoiceId', this.searchInvoiceId);
    body.set('customer', this.searchCustomer);
    body.set('whereDateAfter', `${this.searchFirstDate['formatted']} 00:00:00`);
    body.set('whereDateBefore', `${this.searchLastDate['formatted']} 23:59:59`);

    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.offset * this.limit}`);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
    body.set('isComingInvoice', `true`);
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

  getInbox() {
    const body = new URLSearchParams();
    if  (this.selectedProfile) {
      body.set('profile', `${this.selectedProfile.code}`);
    }
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.offset * this.limit}`);
    body.set('startDate', `${this.searchFirstDate['formatted']}`);
    body.set('endDate', `${this.searchLastDate['formatted']}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getInbox`, body, this.options).
      subscribe(
        (data) => {
          if (data.data && !data.error) {
            this.gibInbox = data.data;
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
        this.scenarios = data.data;
      });
  }

  sendResponseGib(actionString: string, index) {
    const body = new URLSearchParams();
    body.set('uuid', `${this.gibInbox[index].uuid}`);
    body.set('action', `${actionString}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/performAction`, body, this.options).
      subscribe(
      (data) => {
        if (actionString.includes('READ')) {
          this._service.success('Başarılı', data.success.message);
          this.gibInbox[index].isReaded = !this.gibInbox[index].isReaded;
        } else {
          this._service.success('Başarılı', 'Yanıt verme işlemi başarılı');
        }
      });

  }

  isAllChecked(invoices): boolean {
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

  changeGibInvoiceRead(index) {
    if (this.gibInbox[index].isReaded) {
      this.sendResponseGib('UNREAD', index);
    } else { this.sendResponseGib('READED', index); }
  }

  addCustomer() {
    const activeModal = this.modalService.open(DefaultModalInvoicesCame, {
      size: 'lg',
      backdrop: 'static',
    });
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  showInvoice(index) {
    const activeModal = this.modalService.open(DefaultModalInvoicesCame, {
      size: 'lg',
      backdrop: 'static',
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
    const activeModal = this.modalService.open(DefaultModalInvoicesCame, {
      size: 'lg',
      backdrop: 'static',
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
    const activeModal = this.modalService.open(DefaultModalInvoicesCame, {
      size: 'lg',
      backdrop: 'static',
    });
    activeModal.componentInstance.uuid = this.gibInbox[index].uuid;
    activeModal.componentInstance.isPdf = false;
    activeModal.result.then((data) => {
      // on close
      this.getInbox();
    }, (reason) => {
      // on dismiss
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

  downloadGibPdf(index, type: string) {
    let invoices = '';
    let typeStr: string;
    if (type === 'xlsx') { typeStr = 'excel'; } else { typeStr = type; }
    if (index === 'liste') {
      for (const item of this.gibInbox) {
        if (item.isChecked) {
          invoices += item.uuid +  ',';
        }
      }
      if (invoices.length > 1) { invoices = invoices.slice(0, invoices.length - 1); }
    } else {
      invoices = this.gibInbox[index].uuid;
    }
    if (invoices.length > 0) {
      const body = new URLSearchParams();
      body.set('uuid', invoices);
      body.set('isOutbox', `${false}`);
      body.set('fileType', typeStr.toUpperCase());
      this.options = new RequestOptions({ headers: this.header, responseType: ResponseContentType.Blob });
      this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/gib/getInvoiceFile`, body, this.options).
        subscribe(
        (data) => {
          if (data.ok && data.status === 200 && data.blob().size > 0) {
            const blob = new Blob([data.blob()], { 'type': 'application/octet-stream' });
            const url = window.URL.createObjectURL(blob);
            if (index === 'liste' && type !== 'xlsx') { 
              saveAs(blob, 'faturalar.zip');
            } else if (index === 'liste' && type === 'xlsx') {
              saveAs(blob, `${type.toUpperCase()}_faturalar.${type}`);
            } else { saveAs(blob, `${type.toUpperCase()}_${this.gibInbox[index].invoiceId}.${type}`);
            }
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
      this.options = new RequestOptions({ headers: this.header });
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
      this.options = new RequestOptions({ headers: this.header });
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/deleteInvoice`, body, this.options).
        subscribe(
        (data) => {
          if (!data.error) {
            this._service.success('Başarılı', data.success.message);
            this.search();
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

}
