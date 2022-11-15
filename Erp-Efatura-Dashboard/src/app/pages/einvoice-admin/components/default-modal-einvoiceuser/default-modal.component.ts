import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';
import { FetchService } from './../../../fetch.service';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { User } from './../../../../models/user';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalEinvoiceUser implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  id: string; 
  namex= ''; 
  emailx= '';
  tckn = '';
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
  sendInfo() {
    if (this.id === 'EinvoiceAdmin') {
      this.addAdmin();
    } else if (this.id === 'EinvoiceUser') {
      this.addUser();
    }
  }
  addAdmin() {
    const body = new URLSearchParams();
    body.set('name', `${this.namex}`);
    body.set('email', `${this.emailx}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/registerEinvoiceAdmin`, body, this.options).
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

  addUser() {
    const body = new URLSearchParams();
    body.set('vkn', `${this.tckn}`);
    body.set('email', `${this.emailx}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/registerEinvoiceUser`, body, this.options).
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
