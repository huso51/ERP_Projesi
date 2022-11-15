import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { Tenant } from './../../../models/tenant';
import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { SpendingType } from '../../../models/spendingType';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalSpendingTypes implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  spendingtype: SpendingType;
  id: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.spendingtype = new SpendingType();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
      // this.getSpendingType();
    }
  }

  addSpendingType() {
    const body = new URLSearchParams();
    body.set('spendingType', JSON.stringify(this.spendingtype));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/addSpendingType`, body, this.options).
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

  getSpendingType() {
    const body = new URLSearchParams();
    body.set('id', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/getSpendingTypes`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.spendingtype = data.data[0];
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  updateSpendingType() {
    const body = new URLSearchParams();
    body.set('spendingType', JSON.stringify(this.spendingtype));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/updateSpendingType`, body, this.options).
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
