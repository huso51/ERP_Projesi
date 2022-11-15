import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { IMyDpOptions, IMySelector } from 'mydatepicker';
import { FormGroup, FormControl, NgForm } from '@angular/forms';
import { CustomValidators } from 'ng2-validation';

import { DefaultModalCustomers } from './../../../../customers/default-modal/default-modal.component';
import { DefaultModalItems } from './../../../../stocks/components/items/default-modal/default-modal.component';


import { Tenant } from './../../../../../models/tenant';
import { FetchService } from './../../../../fetch.service';
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
import { GibUser } from 'app/models/gibUser';
import { InvoiceScenario } from 'app/models/invoiceScenario';
import { DefaultModalCustomizeItem } from './../../../components/customize-item-modal/default-modal.component';
import { NotificationsService } from 'angular2-notifications';
import { TenantPrefix } from 'app/models/tenantPrefix';
import { GlobalTexts } from 'globals/globaltext';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalInvoices implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  isPdf = false;
  id: string;
  uuid: string;
  scenarios = new Array<InvoiceScenario>();
  gibUsersPk = new Array<GibUser>();
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
  invoiceItems = [];
  quantity: number;
  itemSumPrice: number;
  neighborhood: Address;
  cities: Address;
  districts: Address;
  neighborhoods: Address;
  header: Headers = new Headers();
  prefixes: TenantPrefix[];
  options = new RequestOptions({ headers: this.header });
  private myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  private dateSelector: IMySelector = {
    open: false,
  };
  date: Date = new Date();
  waybillDate: Object;
  orderDate: Object;
  invoiceHtml: Object;
  // *********** CONSTRUCTOR ***************** 
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
    private _sanitizer: DomSanitizer, private modalService: NgbModal, private _service: NotificationsService) {
    const date = new Date();
    this.waybillDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.orderDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
    this.invoice = new Invoice();
    this.invoice.sn2 = `${date.getFullYear()}`;
    this.customers = new Array<Customer>();
    this.selectedItem = null;
    this.selectedTax = new TaxesType('', '', '');
    this.defaultTaxes = new Array<TaxesType>();
    this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, 0, 0, 0, new Array<InvoiceLineTaxes>(), '', 1);
    this.prefixes = new Array<TenantPrefix>();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });

  }

  ngOnInit() {
    this.invoice.note = this.user.tenantUsers.tenant.invoiceNote;
    this.getCustomers();
    this.getItems();
    this.getTaxes();
    this.getInvoiceTypes();
    this.getCurrencies();
    if (this.id && !this.isPdf) {
      this.getInvoice();
    } else if (!this.id && !this.isPdf && !this.uuid) {
      if (this.user.tenantUsers.tenant.tenantInfo.isEfaturaUser) {
        this.getScenarios();
        this.getPrefixes();
      }
    } else if (!this.id && !this.isPdf && this.uuid) {
      this.getGibHtml();
    }
    if (this.isPdf) {
      this.getInvoiceHtml();
    }
  }

  autocompleteListFormatter = (data: Customer) => {
    let html = `<span style=''>${data.name} </span>`;
    if (!data.name) {
      html = `<span style=''>${data.appellation} </span>`;
    }
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  autocompleteItemFormatter = (data: Item) => {
    const html = `<span style=''>${data.name} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  autocompleteTaxFormatter = (data: TaxesType) => {
    const html = `<span style=''>${data.description} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }
  customerChanged(newVal) {
    this.invoice.customer = newVal;
    if (this.invoice.customer.appellation) {
      this.invoice.customer.name = this.invoice.customer.appellation;
    } else {
      this.invoice.customer.appellation = this.invoice.customer.name;
    }
    if (this.user.tenantUsers.tenant.tenantInfo.isEfaturaUser) {
      this.getGibUser(this.invoice.customer.tc);
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
        if (!data.error) {
          this.invoiceHtml = this._sanitizer.bypassSecurityTrustHtml(data.data);
        } else {
          this._service.error('Hata', data.error.message);
          this.closeModal();
        }

      });
  }

  getGibHtml() {
    const body = new URLSearchParams();
    // body.set('uuid', this.uuid);
    body.set('uuid', this.uuid);
    body.set('isOutbox', `${true}`);
    body.set('fileType', 'HTML');
    this.newService.fetchPost
      (`${GlobalTexts.rest_url}sessions/gib/getInvoiceFile`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
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
    body.set('isComingInvoice', `false`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoices`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
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
    this.invoice.orderDate = `${this.orderDate['formatted']} 00:00:00`;

    this.invoice.isComingInvoice = false;
    body.set('invoice', JSON.stringify(this.invoice));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/addInvoice`, body, this.options).
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
  getItems() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getItems`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.items = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }
      });
  }

  getPrefixes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getPrefix`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          if (data.data.length > 0) {
            this.prefixes = data.data;
            this.invoice.sn1 = this.prefixes[0].name;
            this.getLastPrefixNumber();
          }
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );
  }

  getLastPrefixNumber() {
    const body = new URLSearchParams();
    body.set('sn1', this.invoice.sn1);
    body.set('sn2', this.invoice.sn2);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getIncrementSn3`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          if (!data.error) {
            this.invoice.sn3 = data.data;
          } else {
            this._service.error('Hata', data.error.message);
          }
        }

        // (this.users);
      },
    );
  }

  getTaxes() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getTaxesType`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.defaultTaxes = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }

      });
  }

  getInvoiceTypes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoiceType`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.invoiceTypes = data.data;
          this.invoice.invoiceType = this.invoiceTypes[0];
        } else {
          this._service.error('Hata', data.error.message);
        }

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
        if (!data.error) {
          this.invoiceWithholdings = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }

      });
  }

  getInvoiceExceptions() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getExceptions`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.invoiceExceptions = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }

      });
  }

  getCurrencies() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCurrency`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.currencies = data.data;
          this.invoice.currency = this.currencies[0];
        } else {
          this._service.error('Hata', data.error.message);
        }

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
    for (const tax of this.invoice.invoiceLine[index].item.itemTaxes) {


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
    const taxes = new Taxes(0, 0, '', 0, this.selectedTax);
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
      if (!this.quantity) {
        this.quantity = 1;
      }
      if (this.selectedItem.stock < this.quantity) {
        this.quantity = this.selectedItem.stock;
        this._service.error('Uyarı', `Ürün adedi stoktan fazla olamaz! Stok: ${this.selectedItem.stock}`);
      } else {
        if (this.invoice.customer) {
          this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, this.invoice.customer.basicDiscount, 0,
            0, new Array<InvoiceLineTaxes>(), '', 1);
          this.invoiceLine.discountAmount = this.invoice.customer.basicDiscount;
        } else {
          this.invoiceLine = new InvoiceLine(0, 0, 0, new Item(), 0, 0, 0, 0, new Array<InvoiceLineTaxes>(), '', 1);
          this.invoiceLine.discountAmount = 0;
        }
        this.invoiceLine.item = this.selectedItem;

        this.invoiceLine.quantity = this.quantity;

        this.invoice.invoiceLine.push(this.invoiceLine);
        this.calculateItemSum(this.invoice.invoiceLine.length - 1);
        this.selectedItem = null;
      }


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
    const activeModalx = this.modalService.open(DefaultModalCustomers, {
      size: 'lg',
      backdrop: 'static',
    });
    activeModalx.result.then((data) => {
      // on close
      this.getCustomers();
      activeModalx.dismiss();
    }, (reason) => {
      // on dismiss
    });
  }

  addItemModal() {
    const activeModal = this.modalService.open(DefaultModalItems, {
      size: 'lg',
      backdrop: 'static',
    });
    activeModal.result.then((data) => {
      // on close
      this.getItems();
    }, (reason) => {
      // on dismiss
    });
  }


  getGibUser(vkn: string) {
    const body = new URLSearchParams();
    if (vkn) {
      body.set('vkn', vkn);
    }
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/getGibUsers`, body, this.options).
      subscribe(
      (data) => {
        if (data.data && !data.error) {
          if (vkn) {
            this.gibUsersPk = data.data;
            if (this.gibUsersPk.length > 0) {
              this.invoice.receiverIdentifier = this.gibUsersPk[0].alias;
            }
          }
        } else {
          this._service.warn('Uyarı', data.error.message);
        }
        
      });
  }
  getScenarios() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/invoices/getInvoiceScenario`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.scenarios = data.data;
          this.invoice.invoiceScenario = this.scenarios[0];
        } else {
          this._service.error('Hata', data.error.message);
        }

      });
  }

}
