import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { Tenant } from './../../../../../models/tenant';
import { FetchService } from './../../../../fetch.service';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { Storage } from 'app/models/storage';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCredits implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  id: string;
  vkn: string;
  amount: string;
  description: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
  }

  send() {
    if (this.id === 'AdminToSuperAdmin') {
      this.adminToSuperAdmin();
    } else if (this.id === 'UserToAdmin') {
      this.userToAdmin();
    } else if (this.id === 'AdminToUser') {
      this.adminToUser();
    }
  }

  userToAdmin() {
    const body = new URLSearchParams();
    body.set('amount', this.amount);
    body.set('description', this.description);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/sendUserCreditRequest`, body, this.options).
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

  adminToUser() {
    const body = new URLSearchParams();
    body.set('vkn', this.vkn);
    body.set('amount', this.amount);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/sendUserCredit`, body, this.options).
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

  adminToSuperAdmin() {
    const body = new URLSearchParams();
    body.set('amount', this.amount);
    body.set('description', this.description);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/sendAdminCreditRequest`, body, this.options).
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

  closeModal() {
    this.activeModal.close();
    
  }
}
