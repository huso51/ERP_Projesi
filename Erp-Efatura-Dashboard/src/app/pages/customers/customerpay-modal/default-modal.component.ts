import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Tenant } from './../../../models/tenant';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { CustomerPayment } from '../../../models/customerPayment';
import { PaymentBill } from '../../../models/paymentBill';
import { PaymentCash } from '../../../models/paymentCash';
import { PaymentCheckbook } from '../../../models/paymentCheckbook';
import { IMyDpOptions, IMySelector } from 'mydatepicker';
import { TenantAccount } from '../../../models/tenantAccount';
import { Customer } from '../../../models/customer';
import { DomSanitizer } from '@angular/platform-browser';
import { PaymentCheckbookItem } from '../../../models/paymentCheckbookItem';
import { PaymentBillInstallment } from '../../../models/paymentBillInstallment';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCustomerPay implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  id: string;
  type = 'cash';
  billCount = 0;
  isPayingToCustomer = false;
  customerPayment: CustomerPayment;
  paymentBill: PaymentBill = new PaymentBill();
  paymentCash: PaymentCash = new PaymentCash();
  paymentCheckbook: PaymentCheckbook = new PaymentCheckbook();
  checkbookItem: PaymentCheckbookItem = new PaymentCheckbookItem();
  accounts: TenantAccount[];
  customers: Customer[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  private myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  private dateSelector: IMySelector;
  private dateSelector1: IMySelector;
  billDate: Object;
  paymentDatee: Object;
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService, private _sanitizer: DomSanitizer) {
    const date = new Date();
    this.billDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' + (date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.paymentDatee = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' + (date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.customerPayment = new CustomerPayment();
    this.dateSelector = {open: false};
    this.dateSelector1 = {open: false};
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    this.customerPayment.type = this.type;
    this.getCustomers();
    this.getAccounts();
    if (this.type === 'cash') {
      
    }
  }

  checkGibUser() {
  }
  autocompleteListFormatter = (data: Customer) => {
    let html = `<span style=''>${data.name} </span>`;
    if (!data.name || data.name === '') {
      html = `<span style=''>${data.appellation} </span>`;
    }
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  customerChanged(newVal) {
    /*this.invoice.customer = newVal;
    if (this.invoice.customer.appellation) {
      this.invoice.customer.name = this.invoice.customer.appellation;
    } else {
      this.invoice.customer.appellation = this.invoice.customer.name;
    }*/
    // console.log(this.customerPayment.customer);
  }
  getGibUser(vkn: string) {
    const body = new URLSearchParams();
    if (vkn) {
      body.set('vkn', vkn);
    }

    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/getGibUsers`, body, this.options).
      subscribe(
      (data) => {
        if (data.data && !data.error && vkn) {

        } else {
          this._service.error('Hata', data.error.message);
        }
      });
  }

  addCustomerPayment() {
    this.paymentCash.paymentDate = this.billDate['formatted'];
    this.customerPayment.paymentCash = this.paymentCash;
    this.customerPayment.paymentCheckbook = this.paymentCheckbook;
    const body = new URLSearchParams();
    body.set('customerPayment', JSON.stringify(this.customerPayment));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/addCustomerPayment`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  getCustomers() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCustomers`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.customers = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }

    });
  }

  getAccounts() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/getAccounts`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.accounts = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  addCheckbookItem() {
    this.checkbookItem.paymentDate = this.paymentDatee['formatted'];
    this.checkbookItem.checkbookDate = this.billDate['formatted'];    
    this.paymentCheckbook.checkbookItems.push(this.checkbookItem);
    for (const item of this.paymentCheckbook.checkbookItems) {
      item.isGivenToCustomer = !this.isPayingToCustomer;
    }
    this.checkbookItem = new PaymentCheckbookItem();
  }
  deleteItemFromLine(index) {
    this.paymentCheckbook.checkbookItems.splice(index, 1);
  }
  addBillInstallments() {
    for (let i = 0; i <= this.billCount; i++) {
      const billInstallment = new PaymentBillInstallment();
      billInstallment.amount = this.paymentBill.amount / this.billCount;
      const date = new Date(this.paymentDatee['formatted']);
      date.setMonth(date.getMonth() + i);
      billInstallment.expiryDate = date.getFullYear() + '-' + date.getMonth() + '-' + date.getDay();

      this.paymentBill.billInstallments.push();
    }
  }
  closeModal() {
    this.activeModal.close();
    
  }
}
