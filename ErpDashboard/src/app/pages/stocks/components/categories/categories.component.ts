import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalCategories } from './default-modal/default-modal.component';

import { FetchService } from './../../../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { GlobalTexts } from './../../../../../globals/globaltexts';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'app/models/user';
import { Category } from 'app/models/category';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss'],
})
export class CategoriesComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  categories: Category;
  
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal) {}
  ngOnInit() {
    this.search();
}

  public search() {
    const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCategories`, body, options).
    subscribe(
    (data) => {
      this.categories = data.data;
      // (this.users);
    },
    );
  }
  
  deleteCategory(index) {
    const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    body.set('categoryId', this.categories[index].id);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/deleteCategory`, body, options)
    .subscribe(
    (data) => this.search(),
    );
  }


  addCategory() {
    const activeModal = this.modalService.open(DefaultModalCategories, {size: 'sm',
                                                              backdrop: 'static'});
    // activeModal.componentInstance.id = this.users[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateCategory(index) {
    const activeModal = this.modalService.open(DefaultModalCategories, {size: 'sm',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.categories[index].id;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }


}
