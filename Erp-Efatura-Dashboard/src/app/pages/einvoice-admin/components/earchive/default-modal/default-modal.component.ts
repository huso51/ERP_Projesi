import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { Tenant } from './../../../../../models/tenant';
import { FetchService } from './../../../../fetch.service';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { Storage } from 'app/models/storage';
import { IMyDpOptions, IMySelector } from 'mydatepicker';
import { EInvoiceMukellef } from '../../../../../models/einvoiceMukellef';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalEarchive implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  users: EInvoiceMukellef[];
  vkn = '';
  name: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  searchFirstDate: any = {
    date: { year: 2015, month: 12, day: 31 },
    formatted: '2015-12-31'
  };
  searchLastDate: any;
  myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  dateSelector1: IMySelector = {
    open: false,
  };
  dateSelector2: IMySelector = {
    open: false,
  };
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService, private _sanitizer: DomSanitizer) {
    this.users = new Array<EInvoiceMukellef>();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
    const date = new Date();
    this.searchLastDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${('0' +(date.getMonth() + 1)).slice(-2)}-${('0' + date.getDate()).slice(-2)}`,
    };
  }

  ngOnInit() {
    this.search();
  }
  openCalendar1() {
    this.dateSelector1 = { open: true };
  }
  openCalendar2() {
    this.dateSelector2 = { open: true };
  }
  onDateChanged(event: Object) {
    // this.searchFirstDate = event;
  }
  create() {
    const body = new URLSearchParams();
    body.set('vkn', `${this.vkn}`);
    body.set('startDate', `${this.searchFirstDate['formatted']}`);
    body.set('endDate', `${this.searchLastDate['formatted']}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/createReport`, body, this.options).
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

  search() {
    const body = new URLSearchParams();
    // body.set('vkn', `${vkn}`);
    // body.set('name', `${this.name}`);
    body.set('limit', `99999999`);
    body.set('offset', `0`);
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

  closeModal() {
    this.activeModal.close();
    
  }
}
