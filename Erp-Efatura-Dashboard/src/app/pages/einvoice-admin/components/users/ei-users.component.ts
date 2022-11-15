import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Tenant } from './../../../../models/tenant';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { User } from 'app/models/user';
import { Item } from 'app/models/item';
import { GlobalTexts } from 'globals/globaltext';
import { EInvoiceMukellef } from './../../../../models/einvoiceMukellef';
import { DefaultModalEinvoiceUser } from './../default-modal-einvoiceuser/default-modal.component';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'app-eiusers',
  templateUrl: './ei-users.component.html',
  styleUrls: ['./ei-users.component.scss'],
})
export class EiUsersComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  users: EInvoiceMukellef;
  name= '';
  tckn= '';
  limit= 20;
  orderSelect= 'created_at';
  offset= 0;
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

  public search() {
    const body = new URLSearchParams();
    body.set('vkn', `${this.tckn}`);
    body.set('name', `${this.name}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.limit * this.offset}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getEinvoiceUsers`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.users = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }

    },
    );
  }

  addEinvoiceUser(callType: string) {
    const activeModal = this.modalService.open(DefaultModalEinvoiceUser, {size: 'lg',
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
