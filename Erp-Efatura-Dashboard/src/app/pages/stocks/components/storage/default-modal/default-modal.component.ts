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

export class DefaultModalStorage implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  storage: Storage;
  id: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.storage = new Storage();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
    this.getStorage();
    }
  }

  addStorage() {
    const body = new URLSearchParams();
    body.set('storage', JSON.stringify(this.storage));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/addStorage`, body, this.options).
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

  getStorage() {
    const body = new URLSearchParams();
    body.set('storageId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getStorage`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
      this.storage = data.data[0];
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  updateStorage() {
    const body = new URLSearchParams();
    body.set('storage', JSON.stringify(this.storage));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/updateStorage`, body, this.options).
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
