import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { GlobalTexts } from './../../../globals/globaltexts';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'app/models/user';
import { Category } from 'app/models/category';
import { TenantPrefix } from 'app/models/tenantPrefix';
import { NotificationsService } from 'angular2-notifications';
import { DefaultModalTenants } from 'app/pages/tenants/default-modal/default-modal.component';
import { EmailConnection } from 'app/models/emailConnection';

@Component({
  selector: 'app-options',
  templateUrl: './options.component.html',
  styleUrls: ['./options.component.scss'],
})
export class OptionsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  prefixes: TenantPrefix[];
  prefix: TenantPrefix;
  emailConn: EmailConnection;
  emailTo: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal,
  private _service: NotificationsService) {
    this.prefixes = new Array<TenantPrefix>();
    this.emailConn = new EmailConnection();
    this.prefix = new TenantPrefix();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.getPrefixes();
    this.getEmailConnection();
}

  getPrefixes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getPrefix`, body, this.options).
    subscribe(
    (data) => {
      this.prefixes = data.data;
      // (this.users);
    },
    );
  }
  addPrefix() {
    const body = new URLSearchParams();
    body.set('prefix', JSON.stringify(this.prefix));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/addPrefix`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this.getPrefixes();
          this._service.success('Başarılı', 'Ekleme Başarılı');
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }
  updatePrefix(index) {
    if (this.prefixes[index].name.length === 3) {
      const body = new URLSearchParams();
      body.set('prefix', JSON.stringify(this.prefixes[index]));
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/updatePrefix`, body, this.options).
      subscribe(
        (data) => {
          if (!data.error.message) {
            this.prefixes[index].isEditing = false;
            this.getPrefixes();
            this._service.success('Başarılı', 'Güncelleme Başarılı');
          } else {
            this._service.error('Hata', data.error.message);
          }
          
        },
      );
    } else {
      this._service.error('Hata', 'Seri 3 haneli olmalıdır');
    }
  }
  deletePrefix(index) {
    const body = new URLSearchParams();
    body.set('prefix', JSON.stringify(this.prefixes[index]));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/deletePrefix`, body, this.options).
    subscribe(
      (data) => {
        if (!data.error.message) {
            this.getPrefixes();
            this._service.success('Başarılı', 'Silme Başarılı');
          } else {
            this._service.error('Hata', data.error.message);
          }
      },
    );
  }
  getEmailConnection() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/emailSetting`, body, this.options).
    subscribe(
      (data) => {
        if (!data.error.message) {
          this.emailConn = data.data;
        } else {
          this._service.error('Hata', data.error.message);
        }
        
      },
    );
  }
  updateEmailConnection() {
    const body = new URLSearchParams();
    body.set('emailConnection', JSON.stringify(this.emailConn));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/emailSetup`, body, this.options).
    subscribe(
      (data) => {
        if (!data.error.message) {
          this.getEmailConnection();
          this._service.success('Başarılı', 'Güncelleme Başarılı');
        } else {
          this._service.error('Hata', data.error.message);
        }
        
      },
    );
  }
  sendTestMail() {
    if (this.emailTo && this.emailTo.length > 5) {
      const body = new URLSearchParams();
      body.set('emailTo', this.emailTo);
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/emailTest`, body, this.options).
      subscribe(
        (data) => {
          if (!data.error.message) {
            this._service.success('Başarılı', 'Test Maili Gönderildi');
          } else {
            this._service.error('Hata', data.error.message);
          }
          
        },
      );
    }
  }
  tenantSettings() {
    const activeModal = this.modalService.open(DefaultModalTenants, {size: 'lg',
                                                              backdrop: 'static'});
                                                        
    activeModal.componentInstance.id = this.user.tenantUsers.tenant.id;
    activeModal.componentInstance.isOwnTenant = true;
    activeModal.result.then((data) => {
      this.updateDefaultTenant();
      
    }, (reason) => {
      // on dismiss
    });
  }
  updateDefaultTenant() {
    const body = new URLSearchParams();
    body.set('defaultTenantId', `${this.user.tenantUsers.tenant.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/updateUser`, body, this.options).
    subscribe(
    (data) => {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('userid');
      localStorage.removeItem('token');
      localStorage.removeItem('permissions');
      localStorage.setItem('currentUser', JSON.stringify(data.data));
      localStorage.setItem('token', data.data.rememberToken);
      // this.router.navigate(['/']);
      window.location.reload();
    });
  }

}
