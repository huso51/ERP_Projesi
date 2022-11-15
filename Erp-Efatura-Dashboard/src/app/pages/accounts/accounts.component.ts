import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalAccounts } from './default-modal/default-modal.component';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from 'globals/globaltext';
import { Category } from 'app/models/category';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { Customer } from '../../models/customer';
import { TenantAccount } from '../../models/tenantAccount';
import { DefaultModalAccountActivities } from './account-activities-modal/default-modal.component';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss'],
})
export class AccountsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  accounts: TenantAccount[];
  searchAccountNo= '';
  searchAccount = '';
  orderBy= 'desc';
  limit= '20';
  orderSelect= 'created_at';
  offset= '0';
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal, private _service: NotificationsService) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  search() {
    const body = new URLSearchParams();
    /*body.set('whereId', this.searchCustomerId);
    body.set('whereName', this.searchCustomer);
    body.set('whereAppellation', this.searchAppellation);*/
    body.set('limit', this.limit);
    body.set('offset', this.offset);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
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
  
  deleteCustomer(index) {
    const array = new Array<TenantAccount>();
    array.push(this.accounts[index]);
    const body = new URLSearchParams();
    body.set('tenantAccount', JSON.stringify(this.accounts[index]));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/deleteAccount`, body, this.options)
    .subscribe(
    (data) => {
      if (!data.error) {
        this.search();
        this._service.success('Başarılı', data.success.message);
      } else {
        this._service.error('Hata', data.error.message);
      }

    },
    );
  }

  accountTransaction(type) {
    const activeModal = this.modalService.open(DefaultModalAccounts, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.type = type;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateCustomer(index) {
    const activeModal = this.modalService.open(DefaultModalAccounts, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.accounts[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  accountActivities(index) {
    const activeModal = this.modalService.open(DefaultModalAccountActivities, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.accounts[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
