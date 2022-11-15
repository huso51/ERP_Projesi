import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { GlobalTexts } from './../../../globals/globaltexts';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'app/models/user';
import { Category } from 'app/models/category';
import { ExpireRequest } from 'app/models/expireRequest';

@Component({
  selector: 'app-categories',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss'],
})
export class ManageComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  expireRequests: ExpireRequest[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  limit= '20';
  isAccepted= false;
  offset= '0';
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService) {
    this.expireRequests = new Array<ExpireRequest>();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  search() {
    const body = new URLSearchParams();
    body.set('confirmed', `${this.isAccepted}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/showExpireRequests`, body, this.options).
    subscribe(
      (data) => {
        this.expireRequests = data.data;
        // (this.users);
      },
    );
  }
  
  changeRequestStatus(index) {
    const body = new URLSearchParams();
    body.set('id', this.expireRequests[index].id.toString());
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/acceptExpireRequest`, body, this.options)
    .subscribe(
      (data) => this.search(),
    );
  }


}
