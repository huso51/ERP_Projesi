import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalSpendingTypes } from './default-modal/default-modal.component';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from './../../../globals/globaltext';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { SpendingType } from '../../models/spendingType';

@Component({
  selector: 'app-spendingtypes',
  templateUrl: './spendingtypes.component.html',
  styleUrls: ['./spendingtypes.component.scss'],
})
export class SpendingTypesComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  spendingtypes: SpendingType[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal,
  private _service: NotificationsService) {
    
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  public search() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/getSpendingTypes`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.spendingtypes = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  
  deleteSpendingType(index) {
    const body = new URLSearchParams();
    body.set('spendingType', JSON.stringify(this.spendingtypes[index]));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/deleteSpendingType`, body, this.options)
    .subscribe(
    (data) => {
      if (!data.error) {
        this.search();
      } else {
        this._service.error('Hata', data.error.message);
      }

    },
    );
  }


  addCategory() {
    const activeModal = this.modalService.open(DefaultModalSpendingTypes, {size: 'sm',
                                                              backdrop: 'static'});
    // activeModal.componentInstance.id = this.users[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateSpendingType(index) {
    const activeModal = this.modalService.open(DefaultModalSpendingTypes, {size: 'sm',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.spendingtypes[index].id;
    activeModal.componentInstance.spendingtype = this.spendingtypes[index];
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }


}
