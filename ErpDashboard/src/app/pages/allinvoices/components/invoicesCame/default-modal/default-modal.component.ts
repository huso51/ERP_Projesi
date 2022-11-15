import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { IMyDpOptions, IMySelector } from 'mydatepicker';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { DefaultModalCustomers } from './../../../../customers/default-modal/default-modal.component';
import { DefaultModalItems } from './../../../../stocks/components/items/default-modal/default-modal.component';


import { Tenant } from './../../../../../models/tenant';
import { FetchService } from './../../../../fetch.service';
import { GlobalTexts } from './../../../../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Address } from 'app/models/address';
import { Customer } from 'app/models/customer';
import { Invoice } from 'app/models/invoice';
import { Item } from 'app/models/item';
import { InvoiceLine } from 'app/models/invoiceLine';
import { InvoiceLineTaxes } from 'app/models/invoiceLineTaxes';
import { Taxes } from 'app/models/taxes';
import { TaxesType } from 'app/models/taxesType';
import { InvoiceType } from 'app/models/invoiceType';
import { InvoiceWithholding } from 'app/models/invoiceWithholding';
import { Currency } from 'app/models/currency';
import { InvoiceException } from 'app/models/invoiceException';
import { DefaultModalCustomizeItem } from './../../../components/customize-item-modal/default-modal.component';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalInvoicesCame implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  isPdf = false;
  id: string;
  uuid: string;
  customers: Customer[];
  currencies: Currency[];
  invoice: Invoice;
  invoiceWithholdings: InvoiceWithholding[];
  invoiceExceptions: InvoiceException[];
  invoiceTypes: InvoiceType[];
  invoiceLine: InvoiceLine;
  selectedTax: TaxesType;
  defaultTaxes: TaxesType[];
  selectedItem: Item;
  name: string;
  items: Item;
  invoiceItems= [];
  quantity: number;
  itemSumPrice: number;
  neighborhood: Address;
  cities: Address;
  districts: Address;
  neighborhoods: Address;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  private myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  private dateSelector: IMySelector = {
        open: false,
    };
  waybillDate: Object;
  orderDate: Object;
  createdAt: Object;
  invoiceHtml: Object;
  constructor(private activeModal: NgbActiveModal, private newService: FetchService, 
              private _sanitizer: DomSanitizer, private modalService: NgbModal, 
              private _service: NotificationsService) {
    const date = new Date();
    this.waybillDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.orderDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.createdAt = { date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() }, 
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`};
    this.invoice = new Invoice();
    this.customers = new Array<Customer>();
    this.selectedItem = null;
    this.selectedTax = new TaxesType('', '', '');
    this.defaultTaxes = new Array<TaxesType>();
    this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, 0, 0, 0, new Array<InvoiceLineTaxes>(), '', 1);
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });

    // completerService.local()
  }

  ngOnInit() {
    this.getCustomers();
    this.getItems();
    this.getTaxes();
    this.getInvoiceTypes();
    this.getCurrencies();
    if (this.id && !this.isPdf) {
      this.getInvoice();
    } else if (!this.id && !this.isPdf && this.uuid) {
      this.getGibHtml();
    }
    if (this.isPdf) {
      this.getInvoiceHtml();
    }
  }

  autocompleteListFormatter = (data: Customer) => {
    let html = `<span style=''>${data.name} </span>`;
    if (!data.name || data.name === '') {
      html = `<span style=''>${data.appellation} </span>`;
    }
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  autocompleteItemFormatter = (data: Item) => {
    let html = `<span style=''>${data.name} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  autocompleteTaxFormatter = (data: TaxesType) => {
    let html = `<span style=''>${data.description} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  customerChanged(newVal) {
    this.invoice.customer = newVal;
    if (this.invoice.customer.appellation) {
      this.invoice.customer.name = this.invoice.customer.appellation;
    } else {
      this.invoice.customer.appellation = this.invoice.customer.name;
    }
  }
  openCalendar() {
    this.dateSelector = { open: true };
  }

  getInvoiceHtml() {
    // const body = new URLSearchParams();
    // body.set('invoiceId', this.id);
    this.newService.fetchGet
              (`${GlobalTexts.rest_url}sessions/invoices/getInvoiceHtml?invoiceId=${this.id}`, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this.invoiceHtml = this._sanitizer.bypassSecurityTrustHtml(data.data);
        } else {
          this._service.error('Hata', data.error.message);
          this.closeModal();
      }
      
    });
  }
  getGibHtml() {
    const body = new URLSearchParams();
    body.set('uuid', this.uuid);
    this.newService.fetchPost
              (`${GlobalTexts.rest_url}sessions/gib/getInboxHtml`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this.invoiceHtml = this._sanitizer.bypassSecurityTrustHtml(data.data);
        } else {
          this._service.error('Hata', data.error.message);
          this.closeModal();
      }
      
    });
  }

  getInvoice() {
    const body = new URLSearchParams();
    body.set('invoiceId', this.id);
    body.set('isComingInvoice', `true`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoices`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this.invoice = data.data[0];
        } else {
          this._service.error('Hata', data.error.message);
          this.closeModal();
      }
      
    });
  }


  addInvoice() {
    const body = new URLSearchParams();
    if (this.invoice.waybillNumber) {
      this.invoice.waybillDate = `${this.waybillDate['formatted']} 00:00:00`;

    } else {
      this.invoice.waybillDate = null;
    }
    this.invoice.createdAt = `${this.createdAt['formatted']} 00:00:00`;
    this.invoice.orderDate = `${this.orderDate['formatted']} 00:00:00`;

    this.invoice.isComingInvoice = true;
    body.set('invoice', JSON.stringify(this.invoice));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/addInvoice`, body, this.options).
    subscribe(
    (data) => {
       if (!data.error.message) {
          this._service.success('Başarılı', 'Fatura Ekleme Başarılı');
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
      this.customers = data.data;
    });
  }
  getItems() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getItems`, body, this.options).
    subscribe(
    (data) => {
      this.items = data.data;
      
    });
  }

  getTaxes() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getTaxesType`, body, this.options).
    subscribe(
    (data) => {
      this.defaultTaxes = data.data;
    });
  }

  getInvoiceTypes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoiceType`, body, this.options).
    subscribe(
    (data) => {
      this.invoiceTypes = data.data;
      this.invoice.invoiceType = this.invoiceTypes[0];
    });
    
  }

  checkWithholding() {
    if (this.invoice.invoiceType.code === 'TEVKIFAT') {
      this.getInvoiceWithholdings();
      this.invoiceExceptions = new Array<InvoiceException>();
    } else if (this.invoice.invoiceType.code === 'ISTISNA') {
      this.getInvoiceExceptions();
      this.invoiceWithholdings = new Array<InvoiceWithholding>();
    } else {
      this.invoiceExceptions = new Array<InvoiceException>();
      this.invoiceWithholdings = new Array<InvoiceWithholding>();
    }
  }

  getInvoiceWithholdings() {
    
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getWithholdings`, body, this.options).
    subscribe(
    (data) => {
      this.invoiceWithholdings = data.data;
    });
  }

  getInvoiceExceptions() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getExceptions`, body, this.options).
    subscribe(
    (data) => {
      this.invoiceExceptions = data.data;
    });
  }

  getCurrencies() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCurrency`, body, this.options).
    subscribe(
    (data) => {
      this.currencies = data.data;
      this.invoice.currency = this.currencies[0];
    },
    );

  }

  calculateItemSum(index) {
    let sumTaxes = 0;
    let mainMoney = 0;
    mainMoney = this.invoice.invoiceLine[index].item.price * this.invoice.invoiceLine[index].quantity;
    if (this.invoice.customer) {
      if (this.invoice.customer.basicDiscount !== 0) {
        mainMoney -= mainMoney * this.invoice.invoiceLine[index].discountAmount / 100;
      }
    }
    for (let tax of this.invoice.invoiceLine[index].item.itemTaxes) {
      
      
      if (this.invoice.invoiceType.code === 'TEVKIFAT' && tax.taxesType.code === 'kdv') {
        let temp = 0;
        temp = tax.value * (100 - this.invoice.invoiceWithholding.value) / 100;
        sumTaxes += temp;
      } 
      if (this.invoice.invoiceType.code === 'TEVKIFAT' && tax.taxesType.code !== 'kdv') {
        sumTaxes += tax.value;
      }
      if (this.invoice.invoiceType.code !== 'TEVKIFAT') {
        if (tax.taxesType.code.indexOf('ÖTV')) {
          mainMoney += mainMoney * tax.value / 100;
        } else {
          sumTaxes += tax.value;
        }
      }
    }
    if (sumTaxes !== 0) {
      mainMoney += mainMoney * sumTaxes / 100;
      
    }
    this.invoice.invoiceLine[index].price = mainMoney * this.invoice.invoiceLine[index].currencyMultiplier;
    this.calculateAllSum();
  }

  calculateAllSum() {
    let sum = 0;
    this.invoice.discountTotal = 0;
    this.invoice.subTotal = 0;
    this.invoice.grossTotal = 0;
    this.invoice.taxesTotal = 0;
    for (const prices of this.invoice.invoiceLine) {
      let taxesTemp = 0;
      sum += prices.price;
      this.invoice.subTotal += prices.item.price * prices.quantity * prices.currencyMultiplier;
    // this.invoice.grossTotal = 0;
      for (const taxess of prices.item.itemTaxes) {
       taxesTemp += taxess.value; 
      }
      this.invoice.discountTotal += 
          (prices.item.price * prices.quantity * prices.currencyMultiplier) * prices.discountAmount / 100;
      
      taxesTemp = 0;
    }
    this.invoice.grossTotal = (this.invoice.subTotal - this.invoice.discountTotal);
    this.invoice.priceTotal = sum;
    this.invoice.taxesTotal = (this.invoice.priceTotal - this.invoice.grossTotal);
  }

  addTax(index) {
    const taxes = new Taxes(0, 0, '', 0, this.selectedTax) ;
    this.invoice.invoiceLine[index].item.itemTaxes.push(taxes);
    this.calculateItemSum(index);
  }
  deleteTax(index1, index2) {
    this.invoice.invoiceLine[index1].item.itemTaxes.splice(index2, 1);
    this.calculateItemSum(index1);
  }
  addInvoiceItems() {
    this.invoiceItems.push(this.selectedItem);
  }
  addItemtoLine() {
    if (this.selectedItem) {
      if (this.invoice.customer) {
      this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, this.invoice.customer.basicDiscount, 0,
                                        0, new Array<InvoiceLineTaxes>(), '', 1);
      } else {
        this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, 0, 0, 0, new Array<InvoiceLineTaxes>(), '', 1);
        this.invoiceLine.discountAmount = 0;
      }
      this.invoiceLine.item = this.selectedItem;
      if (this.quantity) {
        this.invoiceLine.quantity = this.quantity;
      } else {
        this.invoiceLine.quantity = 1;
      }
      this.invoiceLine.discountAmount = this.invoice.customer.basicDiscount;
      this.invoice.invoiceLine.push(this.invoiceLine);
      this.calculateItemSum(this.invoice.invoiceLine.length - 1);
      this.selectedItem = null;
    }
  }
  deleteItemFromLine(index) {
    this.invoice.invoiceLine.splice(index, 1);
    this.calculateAllSum();
  }

  customizeItemFromLine(index) {
    const activeModalx = this.modalService.open(DefaultModalCustomizeItem, {
      size: 'sm',
      backdrop: 'static',
    });
    activeModalx.componentInstance.invoice = this.invoice;
    activeModalx.componentInstance.invoiceLine = this.invoice.invoiceLine[index];
    activeModalx.componentInstance.isComingInvoice = true;
    activeModalx.result.then((data) => {
      // on close
      // this.invoice.invoiceLine[index] = activeModalx.componentInstance.invoiceLine;
      this.calculateAllSum();
      activeModalx.dismiss();
    }, (reason) => {
      // on dismiss
    });
  }

  closeModal() {
    this.activeModal.close();
  }
  addCustomerModal() {
    const activeModal = this.modalService.open(DefaultModalCustomers, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.getCustomers();
    }, (reason) => {
      // on dismiss
    });
  }

  addItemModal() {
    const activeModal = this.modalService.open(DefaultModalItems, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.getItems();
    }, (reason) => {
      // on dismiss
    });
  }


}
