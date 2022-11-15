import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../fetch.service';
import { GlobalTexts } from './../../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Tenant } from 'app/models/tenant';
import { ToasterService, ToasterConfig } from 'angular2-toaster/angular2-toaster';
import { NotificationsService } from "angular2-notifications";

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
  providers: [ToasterService],
})

export class DefaultModal implements OnInit {
  currentUser: User = JSON.parse(localStorage.getItem('currentUser'));
  tenants: Tenant[] = new Array<Tenant>();
  user: User = new User();
  id: string;

  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if ((this.currentUser.tenantUsers.userRole.user === 'w' || 
    this.currentUser.tenantUsers.userRole.user === 'a') && this.id) {
      this.getUser();
    }
  }
  getUser() {
    const body = new URLSearchParams();
    body.set('whereId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getUsers`, body, this.options).
    subscribe(
    (data) => {
      this.user = data.data[0];
      this.getTenants();
    },
    );
  }
  getTenants() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getSubTenants`, body, this.options).
    subscribe(
    (data) => {
      this.tenants = data.data;
      // (this.users);
    },
    );
  }
  updateUser() {
    // this.user.tenantUsers.userRole.
    const body = new URLSearchParams();
    body.set('user', JSON.stringify(this.user));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/updateTenantUsers`, body, this.options)
    .subscribe(
      (data) => {
        if (!data.error.message) {
          this._service.success('Başarılı', 'Güncelleme Başarılı');
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
        
      },
    );  
  }
  addUser() {
    const body = new URLSearchParams();
    body.set('user', JSON.stringify(this.user));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/addUser`, body, this.options)
    .subscribe(
      (data) => {
        if (!data.error.message) {
          this._service.success('Başarılı', 'Kullanıcı Ekleme Başarılı');
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
