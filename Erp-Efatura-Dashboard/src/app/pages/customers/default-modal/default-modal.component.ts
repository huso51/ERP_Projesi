import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Tenant } from './../../../models/tenant';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { Address } from 'app/models/address';
import { Customer } from 'app/models/customer';
import { Neighborhood } from 'app/models/neighborhood';
import { City } from 'app/models/city';
import { District } from 'app/models/district';
import { CustomerType } from 'app/models/customerType';
import { GibUser } from 'app/models/gibUser';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCustomers implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  customer: Customer;
  gibUsersPk = new Array<GibUser>();
  address: Address;
  id: string;
  isAssent= true;
  customerTypes: CustomerType[];
  neighborhood: Neighborhood;
  cities: City;
  districts: District;
  neighborhoods: Neighborhood[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  tempNeighborhood: Neighborhood = new Neighborhood();
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.customer = new Customer();
    this.address = new Address();
    this.address.neighborhood = new Neighborhood();
    this.tempNeighborhood = new Neighborhood();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    this.getCities();
    this.getCustomerTypes();
  }

  checkGibUser() {
    if (this.customer.tc.length === 11 || this.customer.tc.length === 10) {
      this.getGibUser(this.customer.tc);
    } else {
      this.customer.senderIdentifier = null;
      this.customer.appellation = '';
      this.customer.fullAppellation = '';
    }
  }

  getGibUser(vkn: string) {
    const body = new URLSearchParams();
    if (vkn) {
      body.set('vkn', vkn);
    }

    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/getGibUsers`, body, this.options).
      subscribe(
      (data) => {
        if (data.data && !data.error && vkn) {
          if (data.data.length > 0) {
            this.gibUsersPk = data.data;
            this.customer.senderIdentifier = this.gibUsersPk[0].alias;
            this.customer.appellation = this.customer.name = this.gibUsersPk[0].title;
            this.customer.fullAppellation = this.customer.surname = this.gibUsersPk[0].title;
          }
        } else {
          this._service.error('Hata', data.error.message);
        }
      });
  }

  addCustomer() {
    const body = new URLSearchParams();
    this.address.isDefaultAddress = true;
    this.customer.address.push(this.address);
    body.set('customer', JSON.stringify(this.customer));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/addCustomer`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.customer.address = new Array<Address>();
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
    },
    );
  }

  getCustomer() {
    const body = new URLSearchParams();
    body.set('whereId', `${this.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCustomers`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.customer = data.data[0];
        this.address = this.customer.address[0];
      } else {
        this._service.error('Hata', data.error.message);
      }

    },
    );
  }

  getCustomerTypes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCustomerTypes`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.customerTypes = data.data;
        if (this.id) {
          this.getCustomer();
        } else {
          this.customer.customerType = this.customerTypes[1];
        }
      } else {
        this._service.error('Hata', data.error.message);
      }

    },
    );
  }

  getCities() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCities`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.cities = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }
  getDistricts() {
    const body = new URLSearchParams();
    body.set('cityId', `${this.address.city.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getDistricts`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.districts = data.data;
        this.address.neighborhood = new Neighborhood();
        this.neighborhoods = new Array<Neighborhood>(); 
      } else {
        this._service.error('Hata', data.error.message);
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
      if (!data.error) {
        this.neighborhoods = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  neighborhoodSelected() {
    this.address.fullAddress = this.address.neighborhood.name;
  }

  customCompare(o1: Neighborhood, o2: Neighborhood) {
    return o1 && o2 ?  o1.id === o2.id : o1 === o2;
  }

  updateCustomer() {
    const body = new URLSearchParams();
    this.customer.address.push(this.address);
    
    body.set('customer', JSON.stringify(this.customer));

    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/updateCustomer`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          this.customer.address = new Array<Address>();
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
