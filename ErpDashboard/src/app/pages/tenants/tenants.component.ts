import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalTenants } from './default-modal/default-modal.component';
import { IMySelector, IMyDpOptions } from 'mydatepicker';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';
import { GlobalTexts } from './../../../globals/globaltexts';

import { User } from 'app/models/user';
import { Tenant } from './../../models/tenant';

@Component({
  selector: 'app-users',
  templateUrl: './tenants.component.html',
  styleUrls: ['./tenants.component.scss'],
})
export class TenantsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  tenants: Tenant;
  searchTenantId= '';
  searchTenant = '';
  searchEmail= '';
  searchFirstDate: Object = { date: { year: 2015, month: 12, day: 31 },
                              formatted: '2015-12-31' };
  searchLastDate: Object;
  orderBy= 'desc';
  limit= '20';
  orderSelect= 'id';
  offset= '0';
  myDatePickerOptions: IMyDpOptions = GlobalTexts.datepickerOptions;
  dateSelector1: IMySelector = {
        open: false,
  };
  dateSelector2: IMySelector = {
        open: false,
  };
  header: Headers = new Headers();
    
  options = new RequestOptions({ headers: this.header });    
  
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
    const date = new Date();
    this.searchLastDate = { date: { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() },
                              formatted: `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` };
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
    this.search();
  }


  search() {
    const body = new URLSearchParams();
    body.set('whereId', this.searchTenantId);
    body.set('whereName', this.searchTenant);
    body.set('whereDateAfter', `${this.searchFirstDate['formatted']} 00:00:00`);
    body.set('whereDateBefore', `${this.searchLastDate['formatted']} 23:59:59` );
    body.set('limit', this.limit);
    body.set('offset', this.offset);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getSubTenants`, body, this.options).
    subscribe(
    (data) => {
      this.tenants = data.data;
    },
    );
  }
  
  deleteUser(index) {
    /*const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    body.set('id', this.users[index].id);
    this.newService.fetchPost(GlobalTexts.rest_url + 'sessions/users/delete', body, options)
    .subscribe(
    (data) => this.search(),
    );*/
  }
  changeTenantStatus(index) {
    if (this.tenants[index].status === 'ENABLED') {
      this.tenants[index].status = 'DISABLED';
    } else {
      this.tenants[index].status = 'ENABLED';
    }
    const body = new URLSearchParams();
    body.set('tenant', JSON.stringify(this.tenants[index]));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/updateTenant`, body, this.options)
    .subscribe(
    (data) => this.search(),
    );
  }

  addTenant() {
    const activeModal = this.modalService.open(DefaultModalTenants, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateTenant(index) {
    const activeModal = this.modalService.open(DefaultModalTenants, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.tenants[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
