import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { Tenant } from './../../../models/tenant';
import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Category } from 'app/models/category';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { TenantAccount } from '../../../models/tenantAccount';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalAccounts implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  account: TenantAccount;
  accounts: TenantAccount[];
  id: string;
  type = 'account';
  receiverAccount: TenantAccount;
  senderAccount: TenantAccount;
  tempAccount: TenantAccount;
  amount = 0;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.account = new TenantAccount();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id && this.type === 'account') {
      this.getAccount(this.id);
    } else {
      this.getAccount(null);
    }
  }

  addAccount() {
    const body = new URLSearchParams();
    body.set('tenantAccount', JSON.stringify(this.account));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/addAccount`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  getAccount(id) {
    const body = new URLSearchParams();
    if (id) {
      body.set('id', `${id}`);
    }
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/getAccounts`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        if (id) { this.account = data.data[0]; } else { this.accounts = data.data; }
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  updateAccount() {
    const body = new URLSearchParams();
    body.set('tenantAccount', JSON.stringify(this.account));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/updateAccount`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  transferMoney() {
    const body = new URLSearchParams();
    body.set('fromAccount', JSON.stringify(this.senderAccount));
    body.set('toAccount', JSON.stringify(this.receiverAccount));
    body.set('amount', `${this.amount}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/addAccountTransfer`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }
  addMoney() {
    const body = new URLSearchParams();
    body.set('tenantAccount', JSON.stringify(this.senderAccount));
    body.set('amount', `${this.amount}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/addAccountAmount`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }
  customCompare(o1: TenantAccount, o2: TenantAccount) {
    return o1 && o2 ?  o1.id === o2.id : o1 === o2;
  }

  closeModal() {
    this.activeModal.close();
    
  }
}
