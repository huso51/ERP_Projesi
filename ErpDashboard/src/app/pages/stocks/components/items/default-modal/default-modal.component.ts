import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { DefaultModalCategories } from './../../categories/default-modal/default-modal.component';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../../../fetch.service';
import { GlobalTexts } from './../../../../../../globals/globaltexts';
import { User } from 'app/models/user';

import { Tenant } from './../../../../../models/tenant';
import { Item } from 'app/models/item';
import { Category } from 'app/models/category';
import { UomCode } from 'app/models/uomCode';
import { TaxesType } from 'app/models/taxesType';
import { Currency } from 'app/models/currency';
import { Taxes } from 'app/models/taxes';
import { ItemsComponent } from './../items.component';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalItems implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  item: Item;
  selectedCategory: Category;
  id: string;
  tax: Taxes;
  taxValue: number;
  categories: Category;
  uomCodes: UomCode;
  taxesTypes: TaxesType[];
  currencies: Currency;

  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  
  constructor(private activeModal: NgbActiveModal, private newService: FetchService, 
              private _sanitizer: DomSanitizer, private modalService: NgbModal,
            private _service: NotificationsService) {
    this.item = new Item();
    this.tax = new Taxes(0, 0, '', 0, new TaxesType('', '', '') );
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
    this.getItem();
    }
    this.getTaxesType();
    this.getUomCodes();
    this.getCurrencies();
    this.getCategories();
  }

  autocompleteCategoryFormatter = (data: Category) => {
    let html = `<span style=''>${data.description} </span>`;
    return this._sanitizer.bypassSecurityTrustHtml(html);
  }

  categoryChanged(newVal) {
    this.item.category = newVal;
  }

  addItem() {
    const body = new URLSearchParams();
    body.set('item', JSON.stringify(this.item));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/addItem`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error.message) {
        this.activeModal.close();
        this._service.success('Ba??ar??l??', '??r??n ekleme ba??ar??l??');
      } else {
        this._service.error('Hata', data.error.message);
      }
      
    },
    );
  }

  getItem() {
    const body = new URLSearchParams();
    body.set('whereId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getItems`, body, this.options).
    subscribe(
    (data) => {
      this.item = data.data[0];
      this.selectedCategory = this.item.category;
    },
    );
  }

  updateItem() {
    const body = new URLSearchParams();
    // body.set('customerId', `${this.id}`);
    body.set('item', JSON.stringify(this.item));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/updateItem`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
        this._service.success('Ba??ar??l??', '??r??n g??ncelleme ba??ar??l??');
        this.activeModal.close();
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  getTaxesType() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getTaxesType`, body, this.options).
    subscribe(
    (data) => {
      this.taxesTypes = data.data;
      this.tax.taxesType = this.taxesTypes[0];
    },
    );
  }
  getUomCodes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getUomCodes`, body, this.options).
    subscribe(
    (data) => {
      this.uomCodes = data.data;
      if (!this.id) {
      this.item.uomCode.id = this.uomCodes[0].id;
    }
    },
    );
  }
  getCurrencies() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCurrency`, body, this.options).
    subscribe(
    (data) => {
      this.currencies = data.data;
      if (!this.id) {
        this.item.currency.id = this.currencies[0].id;
      }
    },
    );

  }
  getCategories() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCategories`, body, this.options).
    subscribe(
    (data) => {
      this.categories = data.data;
    },
    );
  }
  
  addTax() {
    if (this.tax.taxesType.code.length > 0) {
      let taxes: Taxes;
      taxes = this.tax;
      this.item.itemTaxes.push(this.tax);
      this.tax = new Taxes(0, 0, '', 0, new TaxesType('', '', '') );
    }
    
  }
  deleteTax(index) {
      if (this.id) {
      const body = new URLSearchParams();
      // body.set('customerId', `${this.id}`);
      body.set('itemTaxes', JSON.stringify( this.item.itemTaxes[index]));
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/deleteItemTaxes`, body, this.options).
      subscribe(
      (data) => {
        this.item.itemTaxes.splice(index, 1);
      },
      );
    } else {
      this.item.itemTaxes.splice(index, 1);
    }
  }

  addCategoryModal() {
    const activeModal = this.modalService.open(DefaultModalCategories, {size: 'sm',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.getCategories();
    }, (reason) => {
      // on dismiss
    });
  }


  closeModal() {
    this.activeModal.close();
    
  }


}
