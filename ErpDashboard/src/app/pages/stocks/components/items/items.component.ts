import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalItems } from './default-modal/default-modal.component';
import { Tenant } from './../../../../models/tenant';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { GlobalTexts } from './../../../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Item } from 'app/models/item';

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.scss'],
})
export class ItemsComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  items: Item;
  searchItem = '';
  searchDescription= '';
  searchFirstDate = '2015-12-31 00:00:00';
  searchLastDate= 'now()';
  orderBy= 'desc';
  limit= '20';
  orderSelect= 'created_at';
  offset= '0';
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    this.search();
}

  public search() {
    const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    body.set('whereName', this.searchItem);
    body.set('whereDescription', this.searchDescription);
    body.set('whereDateAfter', `${this.searchFirstDate} 00:00:00`);
    if (this.searchLastDate !== 'now()') {
      body.set('whereDateBefore', `${this.searchLastDate} 00:00:00` );
    } else {
    body.set('whereDateBefore', this.searchLastDate);
  }
    body.set('limit', this.limit);
    body.set('offset', this.offset);
    body.set('orderBy', `${this.orderSelect} ${this.orderBy}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getItems`, body, options).
    subscribe(
    (data) => {
      this.items = data.data;
    },
    );
  }
  
  deleteItem(index) {
    const array = new Array<Item>();
    array.push(this.items[index]);
    const body = new URLSearchParams();
    body.set('items', JSON.stringify(array));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/deleteItems`, body, this.options)
    .subscribe(
    (data) => {
      this.search();
    },
    );
  }

  addItem() {
    const activeModal = this.modalService.open(DefaultModalItems, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateItem(index) {
    const activeModal = this.modalService.open(DefaultModalItems, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.items[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
