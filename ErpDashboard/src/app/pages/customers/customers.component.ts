import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalCustomers } from './default-modal/default-modal.component';
import { Tenant } from './../../models/tenant';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { GlobalTexts } from './../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.scss'],
})
export class CustomersComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customers: Customer;
  searchCustomerId= '';
  searchCustomer = '';
  searchAppellation= '';
  searchFirstDate = '2015-12-31 00:00:00';
  searchLastDate= 'now()';
  orderBy= 'desc';
  limit= '20';
  orderSelect= 'created_at';
  offset= '0';
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  search() {
    const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    body.set('whereId', this.searchCustomerId);
    body.set('whereName', this.searchCustomer);
    body.set('whereAppellation', this.searchAppellation);
    body.set('whereDateAfter', `${this.searchFirstDate} 00:00:00`);
    if (this.searchLastDate !== 'now()') {
      body.set('whereDateBefore', `${this.searchLastDate} 00:00:00` );
    } else {
    body.set('whereDateBefore', this.searchLastDate);
  }
    body.set('limit', this.limit);
    body.set('offset', this.offset);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCustomers`, body, options).
    subscribe(
    (data) => {
      this.customers = data.data;
      // (this.users);
    },
    );
  }
  
  deleteCustomer(index) {
    const array = new Array<Customer>();
    array.push(this.customers[index]);
    const body = new URLSearchParams();
    body.set('customer', JSON.stringify(array));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/deleteCustomers`, body, this.options)
    .subscribe(
    (data) => {
      this.search();
    },
    );
  }

  addCustomer() {
    const activeModal = this.modalService.open(DefaultModalCustomers, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateCustomer(index) {
    const activeModal = this.modalService.open(DefaultModalCustomers, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.customers[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
