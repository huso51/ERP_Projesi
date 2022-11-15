import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from 'globals/globaltext';
import { User } from 'app/models/user';
import { Storage } from 'app/models/storage';
import { NotificationsService } from 'angular2-notifications';
import { EInvoiceCreditRequest } from './../../../../models/einvoiceCreditRequest';
import { DefaultModalCredits } from './../../components/credits/default-modal/default-modal.component';

@Component({
  selector: 'app-credits',
  templateUrl: './credits.component.html',
  styleUrls: ['./credits.component.scss'],
})
export class CreditsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  creditRequests: EInvoiceCreditRequest[];
  vkn= '';
  name= '';
  confirmed = false;
  limit = 20;
  offset = 0;
  isSuperAdminRequest= false;
  // (localStorage.getItem('currentUser'))
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

  constructor(private newService: FetchService, private modalService: NgbModal, private _service: NotificationsService) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  public search() {
    if (this.isSuperAdminRequest) {
      this.getAdminCreditRequests();
    } else {
      this.getUserCreditRequests();
    }
  }

  getUserCreditRequests() {
    const body = new URLSearchParams();
    body.set('id', `${this.vkn}`);
    body.set('name', `${this.name}`);
    body.set('confirmed', `${this.confirmed}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.limit * this.offset}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getUserCreditRequest`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.creditRequests = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  getAdminCreditRequests() {
    const body = new URLSearchParams();
    body.set('id', `${this.vkn}`);
    body.set('name', `${this.name}`);
    body.set('confirmed', `${this.confirmed}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.limit * this.offset}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getAdminCreditRequest`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.creditRequests = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  acceptRequest(ind) {
    if (this.isSuperAdminRequest) {
      this.acceptAsSuperAdmin(ind);
    } else {
      this.acceptAsAdmin(ind);
    }
  }
  acceptAsAdmin(ind) {
    const body = new URLSearchParams();
    body.set('id', `${this.creditRequests[ind].id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/acceptUserCreditRequest`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this._service.success('Başarılı', data.success.message);
        this.search();
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  acceptAsSuperAdmin(ind) {
    const body = new URLSearchParams();
    body.set('id', `${this.creditRequests[ind].id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/acceptAdminCreditRequest`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this._service.success('Başarılı', data.success.message);
        this.search();
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  sendCreditRequest(callType: string) {
    const activeModal = this.modalService.open(DefaultModalCredits, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = callType;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }


}
