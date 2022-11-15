import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalStorage } from './default-modal/default-modal.component';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from 'globals/globaltext';
import { User } from 'app/models/user';
import { Storage } from 'app/models/storage';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'app-storage',
  templateUrl: './storage.component.html',
  styleUrls: ['./storage.component.scss'],
})
export class StorageComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  storages: Storage[];
  
  // (localStorage.getItem('currentUser'))
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

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
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getStorage`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.storages = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  
  deleteStorage(index) {
    const body = new URLSearchParams();
    body.set('storageId', `${this.storages[index].id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/deleteStorage`, body, this.options)
    .subscribe(
    (data) => {
      if (!data.error) {
        this.search();
        this._service.success('Başarılı', data.success.message);
      } else {
        this._service.error('Hata', data.error.message);
      }
    }
    );
  }


  addStorage() {
    const activeModal = this.modalService.open(DefaultModalStorage, {size: 'sm',
                                                              backdrop: 'static'});
    // activeModal.componentInstance.id = this.users[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateStorage(index) {
    const activeModal = this.modalService.open(DefaultModalStorage, {size: 'sm',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.storages[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }


}
