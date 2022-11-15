import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { Tenant } from './../../../../../models/tenant';
import { FetchService } from './../../../../fetch.service';
import { GlobalTexts } from './../../../../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Category } from 'app/models/category';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCategories implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  category: Category;
  id: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.category = new Category();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
    this.getCategory();
    }
  }

  addCategory() {
    const body = new URLSearchParams();
    body.set('name', this.category.name);
    body.set('description', this.category.description);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/addCategory`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error.message) {
          this._service.success('Başarılı', 'Kategori Ekleme Başarılı');
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  getCategory() {
    const body = new URLSearchParams();
    body.set('categoryId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/getCategories`, body, this.options).
    subscribe(
    (data) => {
      this.category = data.data[0];
      
    },
    );
  }

  updateCategory() {
    const body = new URLSearchParams();
    body.set('categoryId', `${this.id}`);
    body.set('name', this.category.name);
    body.set('description', this.category.description);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/items/updateCategory`, body, this.options).
    subscribe(
    (data) => {
      // this.tenants = data.data;
      if (!data.error.message) {
          this._service.success('Başarılı', 'Kategori Güncelleme Başarılı');
          this.closeModal();
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
