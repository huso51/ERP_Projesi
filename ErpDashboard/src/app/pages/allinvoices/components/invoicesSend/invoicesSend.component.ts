import { Component, OnInit } from '@angular/core';
import 'rxjs/Rx';
import { Headers, Response, RequestOptions, URLSearchParams, ResponseContentType } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalInvoices } from './default-modal/default-modal.component';
import { IMyDpOptions, IMySelector } from 'mydatepicker';

import { GlobalTexts } from './../../../../../globals/globaltexts';
import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { Tenant } from './../../../../models/tenant';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Invoice } from 'app/models/invoice';

import { saveAs } from 'file-saver';
import { GibInbox } from 'app/models/gibInbox';
import { NotificationsService } from 'angular2-notifications';


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
    private _service: NotificationsService) {
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
    this.search();
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
    if (this.isEfatura === 'ERP') {
      this.getOutbox();
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
    body.set('isComingInvoice', `false`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoices`, body, this.options).
      subscribe(
      (data) => {
        this.invoices = data.data;
      },
    );
  }

  getOutbox() {
    const body = new URLSearchParams();
    body.set('startDate', `${this.searchFirstDate['formatted']}`);
    body.set('endDate', `${this.searchLastDate['formatted']}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getOutbox`, body, this.options).
      subscribe(
      (data) => {
        if (data.data && !data.error.message) {
          this.gibOutbox = data.data;
        }
      }, (err) => {
        this._service.error('Hata', 'Gelen Kutusuna erişirken hata oluştu.');
      },
    );
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

  downloadPdf(index) {
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
        }, (err) => {
          this._service.error('Hata', 'Hata Oluştu');
        },
      );
    } else {
      this._service.warn('Uyarı', 'Lütfen fatura seçiniz');
    }
  }

  /*sendResponseGib(actionString, index) {
    const body = new URLSearchParams();
    body.set('uuid', `${this.gibOutbox[index].uuid}`);
    body.set('action', `${actionString}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/performAction`, body, this.options).
      subscribe(
      (data) => {
      });

  }*/

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
      this.options = new RequestOptions({ headers: this.header });
      this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/invoices/sendToCustomers`, body, this.options).
        subscribe(
        (data) => {
          if (data.status === 200) {
            this._service.success('Başarılı', 'Email başarıyla gönderildi');
          } else {
            this._service.error('Hata', 'Hata Oluştu');
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
      this.options = new RequestOptions({ headers: this.header });
      this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/invoices/deleteInvoice`, body, this.options).
        subscribe(
        (data) => {
          if (data.status === 200) {
            this._service.success('Başarılı', 'Silme işlemi başarılı');
            this.search();
          } else {
            this._service.error('Hata', 'Hata Oluştu');
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
        if (!item.receiverIdentifier) {

          hasReceiver = false;
        } else {
          if (item.isChecked) {
            invoices.push(item);
          }
        }
      }
    } else {
      if (!this.invoices[index].receiverIdentifier) {
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
        this.options = new RequestOptions({ headers: this.header });
        this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/gib/sendInvoice`, body, this.options).
          subscribe(
          (data) => {
            if (data.status === 200) {
              this._service.success('Başarılı', 'Faturalar kesilmek üzere gönderildi');
            } else {
              this._service.error('Hata', 'Hata oluştu');
            }
          }, (err) => {
            this._service.error('Hata', 'Hata Oluştu');
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
