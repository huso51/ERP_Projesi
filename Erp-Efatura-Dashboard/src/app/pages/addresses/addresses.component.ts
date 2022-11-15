import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalAddresses } from './default-modal/default-modal.component';
import { Tenant } from './../../models/tenant';

import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';
import { User } from 'app/models/user';
import { Customer } from 'app/models/customer';
import { Address } from 'app/models/address';
import { ActivatedRoute, Router } from '@angular/router';
import { GlobalTexts } from 'globals/globaltext';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'app-addresses',
  templateUrl: './addresses.component.html',
  styleUrls: ['./addresses.component.scss'],
})
export class AddressesComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customerId: string;
  addresses: Address;
  searchCustomerId= '';
  searchCustomer = '';
  searchAppellation= '';
  searchFirstDate = '2015-12-31 00:00:00';
  searchLastDate= 'now()';
  orderBy= 'desc';
  limit= '20';
  orderSelect= 'created_at';
  offset= '0';
  
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, 
              private modalService: NgbModal,
              private route: ActivatedRoute,
              private router: Router, private _service: NotificationsService) {}
  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {
        // Defaults to 0 if no query param provided.
        this.customerId = params['id'];
      });
    this.search();
}

  search() {
    const header: Headers = new Headers();
    header.append('Authorization', localStorage.getItem('token'));
    header.append('Content-Type', 'application/x-www-form-urlencoded');
    const options = new RequestOptions({ headers: header });
    const body = new URLSearchParams();
    body.set('customerId', this.customerId);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getAddress`, body, options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.addresses = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  addCustomer() {
    const activeModal = this.modalService.open(DefaultModalAddresses, {size: 'lg',
                                                              backdrop: 'static'});
    // activeModal.componentInstance.id = this.users[index].id;
    activeModal.componentInstance.customerId = this.customerId;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }
  updateCustomer(index) {
    const activeModal = this.modalService.open(DefaultModalAddresses, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.addresses[index].id;
    activeModal.componentInstance.customerId = this.customerId;
    activeModal.result.then((data) => {
      // on close
      this.search();
    }, (reason) => {
      // on dismiss
    });
  }

}
