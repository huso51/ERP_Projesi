import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from 'globals/globaltext';
import { Category } from 'app/models/category';
import { User } from 'app/models/user';
import { EInvoiceErp } from './../../../../models/einvoiceErp';
import { DefaultModalEinvoiceUser } from './../default-modal-einvoiceuser/default-modal.component';
import { NotificationsService } from 'angular2-notifications';
import { EInvoiceMukellef } from '../../../../models/einvoiceMukellef';

@Component({
  selector: 'app-admins',
  templateUrl: './admins.component.html',
  styleUrls: ['./admins.component.scss'],
})
export class AdminsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId= '';
  admins: EInvoiceMukellef[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  vkn= '';
  name= '';
  confirmed = true;
  limit= 20;
  offset= 0;

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
    body.set('id', `${this.vkn}`);
    body.set('name', `${this.name}`);
    body.set('confirmed', `${this.confirmed}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.limit * this.offset}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getEinvoiceAdmins`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.admins = data.data;
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
