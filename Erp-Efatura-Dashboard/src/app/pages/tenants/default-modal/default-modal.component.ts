import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Tenant } from './../../../models/tenant';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { Address } from 'app/models/address';
import { Neighborhood } from 'app/models/neighborhood';
import { District } from 'app/models/district';
import { City } from 'app/models/city';
import { CustomerType } from 'app/models/customerType';
import { GibUser } from 'app/models/gibUser';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalTenants implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  tenant: Tenant;
  tenants: Tenant;
  gibUsersPk = new Array<GibUser>();
  tenantAddress: Address;
  id: string;
  isOwnTenant: boolean = false;
  expireCount: number = 0;
  owner = '';
  name = '';
  description = '';
  tenantType = '';
  status = '';
  email = '';
  customerTypes: CustomerType[];
  cities: City[];
  districts: District[];
  neighborhoods: Neighborhood[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  tempNeighborhood: Neighborhood = new Neighborhood();
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
    private _service: NotificationsService) {
    this.tenant = new Tenant();
    this.neighborhoods = new Array<Neighborhood>();
    this.districts = new Array<District>();
    this.cities = new Array<City>();
    this.tenantAddress = new Address();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if ((this.user.tenantUsers.userRole.tenant === 'w' ||
      this.user.tenantUsers.userRole.tenant === 'a') &&
      this.user.tenantUsers.userRole.isSuperAdmin) {

      this.getSubTenants();
    }
    if (!this.id) {
      this.tenant.status = 'ENABLED';
      this.tenant.tenantType = 'CUSTOMER';
    } else {
      this.getTenants();
    }
    this.getCities();
    this.getCustomerTypes();

  }

  checkGibUser() {
    if (this.tenant.tenantInfo.tc.length === 11 || this.tenant.tenantInfo.tc.length === 10) {
      this.getGibUser(this.tenant.tenantInfo.tc);
    } else {
      this.tenant.tenantInfo.senderIdentifier = '';
      this.tenant.tenantInfo.appellation = '';
      this.tenant.tenantInfo.fullAppellation = '';
      this.tenant.name = '';
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
        if (!data.error && data.data && vkn) {
          this.gibUsersPk = data.data;
          if (this.gibUsersPk.length > 0) {
            this.tenant.tenantInfo.appellation = this.gibUsersPk[0].title;
            this.tenant.tenantInfo.fullAppellation = this.gibUsersPk[0].title;
            this.tenant.tenantInfo.name = this.gibUsersPk[0].title;
            this.tenant.tenantInfo.surname = this.gibUsersPk[0].title;             
            this.tenant.name = this.gibUsersPk[0].title;
          }

        }
      });
  }

  getCustomerTypes() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCustomerTypes`, body, this.options).
      subscribe(
      (data) => {
        this.customerTypes = data.data;

        if (this.id) {
        } else {
          this.tenant.tenantInfo.customerType = this.customerTypes[1];
        }
      },
    );
  }
  getSubTenants() {
    const body = new URLSearchParams();
    body.set('limit', '99999');
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/getSubTenants`, body, this.options).
      subscribe(
      (data) => {
        this.tenants = data.data;
      },
    );
  }
  addTenant() {
    const body = new URLSearchParams();
    this.tenantAddress.isDefaultAddress = true;
    this.tenant.tenantInfo.address.push(this.tenantAddress);
    body.set('tenant', JSON.stringify(this.tenant));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/addSubTenant`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this._service.success('Başarılı', 'Organizasyon Ekleme Başarılı');
          this.closeModal();
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );
  }

  getTenants() {
    const body = new URLSearchParams();
    let url = '';
    if (!this.isOwnTenant) {
      body.set('whereId', `${this.id}`);
      url = `${GlobalTexts.rest_url}sessions/tenants/getSubTenants`;
    } else {
      url = `${GlobalTexts.rest_url}sessions/tenants/getUserTenant`;
    }

    this.newService.fetchPost(url, body, this.options).
      subscribe(
      (data) => {
        if (!this.isOwnTenant) {
          this.tenant = data.data[0];
        } else {
          this.tenant = data.data;
        }
        if (this.tenant) {
          this.owner = this.tenant.owner;
        }
        this.tenantAddress = this.tenant.tenantInfo.address[0];
        this.description = this.tenant.description;
        this.tenantType = this.tenant.tenantType;
        this.email = this.tenant.email;
        this.getCities();
        // this.getCustomerTypes();
      },
    );
  }

  updateTenant() {
    const body = new URLSearchParams();
    this.tenant.expireCount = this.expireCount;
    this.tenant.tenantInfo.address.push(this.tenantAddress);
    body.set('tenant', JSON.stringify(this.tenant));
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/tenants/updateTenant`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this._service.success('Başarılı', data.success.message);
          if (this.isOwnTenant) {
            this.updateDefaultTenant()
          } else {
            this.closeModal();
          }
          
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );

  }
  updateDefaultTenant() {
    const body = new URLSearchParams();
    body.set('defaultTenantId', `${this.user.tenantUsers.tenant.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/updateUser`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this._service.success('Başarılı', data.success.message);
        localStorage.removeItem('currentUser');
        localStorage.removeItem('userid');
        localStorage.removeItem('token');
        localStorage.removeItem('permissions');
        localStorage.setItem('currentUser', JSON.stringify(data.data));
        localStorage.setItem('token', data.data.rememberToken);
      } else {
        this._service.error('Hata', data.error.message);
      }
      // this.router.navigate(['/']);
      // window.location.reload();
      this.closeModal();
    });
  }

  getCities() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getCities`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.cities = data.data;
          this.getDistricts();
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );
  }
  getDistricts() {
    this.neighborhoods = new Array<Neighborhood>();
    const body = new URLSearchParams();
    body.set('cityId', `${this.tenantAddress.city.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/customers/getDistricts`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.districts = data.data;
          this.getNeighborhoods();
        } else {
          this._service.error('Hata', data.error.message);
        }
      },
    );
  }
  getNeighborhoods() {
    const body = new URLSearchParams();
    body.set('districtId', `${this.tenantAddress.district.id}`);
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
    this.tenantAddress.fullAddress = this.tenantAddress.neighborhood.name;
  }
  customCompare(o1: Neighborhood, o2: Neighborhood) {
    return o1 && o2 ?  o1.id === o2.id : o1 === o2;
  }

  fileChange(event, isLogo) {
    const fileList: FileList = event.target.files;
    if (fileList[0]) {
      if (fileList[0].size > 2048000) {
        this._service.warn('Hata', 'Dosya boyutu 2 Mb dan büyük olamaz');

      } else {
        this.getBase64(fileList[0], isLogo);
      }
    } else {
      if (isLogo) {
        this.tenant.logo = null;
      } else {
        this.tenant.signature = null;
      }
    }

  }
  getBase64(file, isLogo) {
    const reader = new FileReader();
    reader.readAsBinaryString(file);
    reader.onload = () => {
      if (isLogo) {
        this.tenant.logo = btoa(reader.result);
      } else {
        this.tenant.signature = btoa(reader.result);
      }

    };

    reader.onloadend = () => {
      this._service.info('Tamamlandı', 'Resim yüklemesi başarılı');
    };

    reader.onerror = (error) => {
      this._service.warn('Hata', 'Resim yüklenirken hata oluştu');
    };

  }

  closeModal() {
    this.activeModal.close();

  }
}
