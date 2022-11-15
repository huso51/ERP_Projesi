import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'app/models/user';
import { Category } from 'app/models/category';
import { TenantPrefix } from 'app/models/tenantPrefix';
import { NotificationsService } from 'angular2-notifications';
import { DefaultModalTenants } from 'app/pages/tenants/default-modal/default-modal.component';
import { EmailConnection } from 'app/models/emailConnection';
import { GlobalTexts } from 'globals/globaltext';

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
      if (!data.error) {
        this.prefixes = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  addPrefix() {
    const body = new URLSearchParams();
    body.set('prefix', JSON.stringify(this.prefix));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/addPrefix`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
          this.getPrefixes();
          this._service.success('Başarılı', data.success.message);
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
          if (!data.error) {
            this.prefixes[index].isEditing = false;
            this.getPrefixes();
            this._service.success('Başarılı', data.success.message);
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
        if (!data.error) {
            this.getPrefixes();
            this._service.success('Başarılı', data.success.message);
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
        if (!data.error) {
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
        if (!data.error) {
          this.getEmailConnection();
          this._service.success('Başarılı', data.success.message);
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
          if (!data.error) {
            this._service.success('Başarılı', data.success.message);
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
      // this.updateDefaultTenant();
      
    }, (reason) => {
      // on dismiss
    });
  }

  sendEinvoiceUserRequest() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/activateEinvoice`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this._service.success('Başarılı', data.success.message);
      } else {
        this._service.error('Hata', data.error.message);
      }
    });
  }

}
