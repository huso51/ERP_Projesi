import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Tenant } from './../../../models/tenant';

import { FetchService } from './../../fetch.service';
import { GlobalTexts } from './../../../../globals/globaltexts';
import { User } from 'app/models/user';
import { Address } from 'app/models/address';
import { Customer } from 'app/models/customer';
import { Neighborhood } from 'app/models/neighborhood';
import { District } from 'app/models/district';
import { City } from 'app/models/city';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalAddresses implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  address: Address;
  id: string;
  customerId: string;
  postCode: string;
  cities: City;
  districts: District;
  neighborhoods: Neighborhood[];
  owner= '';
  name= '';
  description= '';
  tenantType= '';
  status= '';
  email= '';
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    
    this.address = new Address(0, '', 0, 0, 0, '', '', '' , '', 0, 
    new Neighborhood(0, '', 0), new City(0, ''), new District(0, ''), false);
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
    this.getAddress();
    } else {
      this.getCities();
    }
    
    /*if (this.address.city.id) {
      this.getDistricts();
    }
    if (this.address.district.id) {
      this.getNeighborhoods();
    }*/
  }

  addAddress() {
    const body = new URLSearchParams();
    this.address.customerId = parseInt(this.customerId);
    /*this.address.phoneNumber = `+90${this.address.phoneNumber}`;
    this.address.fax = `+90${this.address.fax}`;*/
    body.set('address', JSON.stringify(this.address));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/addAddress`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this._service.success('Başarılı', 'Adres Ekleme Başarılı');
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  getAddress() {
    const body = new URLSearchParams();
    body.set('addressId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getAddress`, body, this.options).
    subscribe(
    (data) => {
      this.address = data.data[0];
      this.getCities();
    },
    );
  }
  getCities() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCities`, body, this.options).
    subscribe(
    (data) => {
      this.cities = data.data;
      if (this.address.city.id) {
      this.getDistricts();
    }
    },
    );
  }
  getDistricts() {
    this.neighborhoods = new Array<Neighborhood>(); 
    const body = new URLSearchParams();
    body.set('cityId', `${this.address.city.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getDistricts`, body, this.options).
    subscribe(
    (data) => {
      this.districts = data.data;
      if (this.address.district.id) {
        this.getNeighborhoods();
      }

    },
    );
  }
  getNeighborhoods() {
    const body = new URLSearchParams();
    body.set('districtId', `${this.address.district.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getNeighborhoods`, body, this.options).
    subscribe(
    (data) => {
      this.neighborhoods = data.data;
    },
    );
  }

  getPostCode() {
    
  }

  updateAddress() {
    const body = new URLSearchParams();
    this.address.id = parseInt(this.id);
    this.address.customerId = parseInt(this.customerId);
    body.set('address', JSON.stringify(this.address));
    // body.set('isDefaultAddress', this.isDefaultAddress);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/updateAddress`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error.message) {
          this._service.success('Başarılı', 'Adres Güncelleme Başarılı');
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
