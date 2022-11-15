import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from './default-modal/default-modal.component';
import { IMyDpOptions, IMySelector } from 'mydatepicker';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';
import { GlobalTexts } from './../../../globals/globaltexts';

import { User } from './../../models/user';
import { NotificationsService } from "angular2-notifications";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
})
export class UsersComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  users: User;
  searchUserId: string;
  searchName: string;
  searchEmail: string;
  status: string;
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

  constructor(private newService: FetchService, private modalService: NgbModal, 
    private _service: NotificationsService) {
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
    let body = new URLSearchParams();
    body.set('whereId', this.searchUserId);
    body.set('whereName', this.searchName);
    body.set('whereEmail', this.searchEmail);
    body.set('whereDateAfter', `${this.searchFirstDate['formatted']} 00:00:00`);
    body.set('whereDateBefore', `${this.searchLastDate['formatted']} 23:59:59` );
    body.set('whereStatus', this.status);
    body.set('limit', this.limit);
    body.set('offset', this.offset);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);

    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getUsers`, body, this.options).
    subscribe(
    (data) => {
      this.users = data.data;
    }
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
  changeUserStatus(index) {
    const body = new URLSearchParams();
    if (this.users[index].status === 'ENABLED') {
      this.users[index].status = 'DISABLED';
    } else {
      this.users[index].status = 'ENABLED';
    }
    body.set('user', JSON.stringify(this.users[index]));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/updateTenantUsers`, body, this.options)
    .subscribe(
    (data) => this.search(),
    );
  }

  addUser() {
    const activeModal = this.modalService.open(DefaultModal, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      
      this.search();
    }, (reason) => {
      // on dismiss
    });
    // activeModal.componentInstance.id = this.users[index].id;
  }
  updateUser(index) {
    const activeModal = this.modalService.open(DefaultModal, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.users[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
