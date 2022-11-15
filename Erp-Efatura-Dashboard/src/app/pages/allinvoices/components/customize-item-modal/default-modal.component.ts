import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { CustomValidators } from 'ng2-validation';

import { Tenant } from './../../../../models/tenant';
import { FetchService } from './../../../fetch.service';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Category } from 'app/models/category';
import { InvoiceLine } from 'app/models/invoiceLine';
import { InvoiceLineTaxes } from 'app/models/invoiceLineTaxes';
import { Item } from 'app/models/item';
import { TaxesType } from 'app/models/taxesType';
import { Invoice } from 'app/models/invoice';
import { Taxes } from 'app/models/taxes';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCustomizeItem implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  isComingInvoice: boolean = false;
  invoice: Invoice;
  invoiceLine: InvoiceLine;
  defaultTaxes: TaxesType[];
  selectedTax: TaxesType;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _sanitizer: DomSanitizer, private _service: NotificationsService) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    this.getTaxes();
  }

  autocompleteTaxFormatter = (data: TaxesType) => {
    const html = `<span style=''>${data.description} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
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

  addTax() {
    if (this.selectedTax) {
      const taxes = new Taxes(0, 0, '', 0, this.selectedTax);
      this.invoiceLine.item.itemTaxes.push(taxes);
      this.calculateItemSum();
    }
    
  }

  calculateItemSum() {
    if (!this.isComingInvoice && this.invoiceLine.item.stock < this.invoiceLine.quantity) {
      this._service.warn('Uyarı', 'Ürün adedi stoktan fazla olamaz');
      this.invoiceLine.quantity = this.invoiceLine.item.stock;
    }
    let sumTaxes = 0;
    let mainMoney = 0;
    mainMoney = this.invoiceLine.item.price * this.invoiceLine.quantity;
        mainMoney -= mainMoney * this.invoiceLine.discountAmount / 100;

    for (const tax of this.invoiceLine.item.itemTaxes) {
      
      
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
    this.invoiceLine.price = mainMoney * this.invoiceLine.currencyMultiplier;
    
  }
  deleteTax(index2) {
    this.invoiceLine.item.itemTaxes.splice(index2, 1);
  }

  closeModal() {
    this.activeModal.close();
    
  }
}
