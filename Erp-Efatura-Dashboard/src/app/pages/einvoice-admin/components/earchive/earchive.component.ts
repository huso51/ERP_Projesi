import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams, ResponseContentType } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Tenant } from './../../../../models/tenant';
import { saveAs } from 'file-saver';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { User } from 'app/models/user';
import { Item } from 'app/models/item';
import { GlobalTexts } from 'globals/globaltext';
import { EInvoiceMukellef } from './../../../../models/einvoiceMukellef';
import { NotificationsService } from 'angular2-notifications';
import { EInvoiceReport } from '../../../../models/einvoiceReport';
import { IMyDpOptions, IMySelector } from 'mydatepicker';
import { DefaultModalEarchive } from './default-modal/default-modal.component';

@Component({
  selector: 'app-eiusers',
  templateUrl: './earchive.component.html',
  styleUrls: ['./earchive.component.scss'],
})
export class EArchiveComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  users: EInvoiceMukellef;
  earchives: EInvoiceReport[];
  name = '';
  tckn = '';
  limit = 20;
  orderSelect = 'created_at';
  offset = 0;
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
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal, private _service: NotificationsService) {
    const date = new Date();
    this.searchLastDate = {
      date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
      formatted: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
    };
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
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
    this.searchFirstDate = event;
    this.search();
  }
  onDateChangedx(event: Object) {
    this.searchLastDate = event;
    this.search();
  }


  public search() {
    const body = new URLSearchParams();
    body.set('vkn', `${this.tckn}`);
    body.set('startDate', `${this.searchFirstDate['formatted']}`);
    body.set('endDate', `${this.searchLastDate['formatted']}`);
    /*body.set('name', `${this.name}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.limit * this.offset}`);*/
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getReport`, body, this.options).
      subscribe(
        (data) => {
          if (!data.error) {
            this.earchives = data.data;
          } else {
            this._service.error('Hata', data.error.message);
          }

        },
    );
  }

  sendReport(index) {
    const body = new URLSearchParams();
    body.set('id', `${this.earchives[index].uuid}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/sendReport`, body, this.options).
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

  downloadFile(index) {
    const body = new URLSearchParams();
    body.set('uuid', `${this.earchives[index].uuid}`);
    body.set('fileType', `ARCHIVE`);
    this.options = new RequestOptions({ headers: this.header, responseType: ResponseContentType.Blob });
    this.newService.fetchPostNoMap(`${GlobalTexts.rest_url}sessions/gib/getInvoiceFile`, body, this.options).
      subscribe(
        (data) => {
          if (data.ok && data.status === 200 && data.blob().size > 0) {
            const blob = new Blob([data.blob()], { 'type': 'application/octet-stream' });
            const url = window.URL.createObjectURL(blob);
            saveAs(blob, `${this.earchives[index].uuid}.zip`);
            this._service.success('Başarılı', 'E-Arşiv raporu indirildi');
          } else {
            this._service.error('Hata', 'Hata Oluştu');
          }
          this.options = new RequestOptions({ headers: this.header });
        },
    );
  }

  addEinvoiceReport() {
    const activeModal = this.modalService.open(DefaultModalEarchive, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
