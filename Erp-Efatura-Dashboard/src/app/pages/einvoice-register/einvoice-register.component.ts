import { Component, OnInit } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, NgForm } from '@angular/forms';
import { FetchService } from './../fetch.service';
import { LocalDataSource } from 'ng2-smart-table';

import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'app/models/user';
import { Category } from 'app/models/category';
import { TenantPrefix } from 'app/models/tenantPrefix';
import { NotificationsService } from 'angular2-notifications';
import { DefaultModalTenants } from 'app/pages/tenants/default-modal/default-modal.component';
import { EmailConnection } from 'app/models/emailConnection';
import { GlobalTexts } from 'globals/globaltext';
import { Integration } from 'app/models/integration';

@Component({
  selector: 'app-options',
  templateUrl: './einvoice-register.component.html',
  styleUrls: ['./einvoice-register.component.scss'],
})
export class EinvoiceRegisterComponent implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  integrationInfo: Integration;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  isEfatura = false; isEarsiv = false; isEsaklama = false; isEarsivSaklama = false;
  buttonDisabled = false;
  hoverText = 'Kullanıcının e-fatura sistemine entegre olmasını sağlar. Bu işlem entegre olacak mükellefin mali mührü ile yapılır.' +
  'Mali Mührün çalışabilmesi için aşağıda bulunan linkteki imza programını entegre olacak olan kişinin bilgisarına indirin.' +
  'İndirmek için tıklayın.' +
  'İndirdilen programı yükleyin ve ardından yüklenen programı çalıştırın. Bilgisayarın sağ tarafında bir pencere açılacaktır.' +
  'Daha sonra mükellefin mali mührünü takın, bu işlem sonrası pencerede mühür bilgisinin görünmesi gerekmektedir.' +
  'Bu servisi kullanarak istekte bulunduğunuzda uygulamadan imzalamanın başarılı olduğunun mesajını alabileceksiniz.';
  // (localStorage.getItem('currentUser'))

  constructor(private newService: FetchService, private modalService: NgbModal,
    private _service: NotificationsService) {
    this.integrationInfo = new Integration();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
  }
  sendRegisterInfo() {
    this.buttonDisabled = true;
    const body = new URLSearchParams();
    let a = '';
    if (this.isEfatura) {
      a = '2'; if (this.isEsaklama) { a += ',12'} if (this.isEarsiv) { a += ',22'} if (this.isEarsivSaklama) { a += ',32'}
    } else if (this.isEsaklama) {
      a = '12'; if (this.isEarsiv) { a += ',22'} if (this.isEarsivSaklama) { a += ',32'}
    } else if (this.isEarsiv) {
      a = '22'; if (this.isEarsivSaklama) { a += ',32'}
    } else if (this.isEarsivSaklama) {
      a = '32';
    } else { this._service.error('Hata', 'Hizmet Seçimi yapmadınız.'); }
    this.integrationInfo.accountCodeList = a;
    body.set('integration', JSON.stringify(this.integrationInfo));
    if (a !== '') {
      this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/integrate`, body, this.options).
        subscribe(
        (data) => {
          if (!data.error) {
            this._service.success('Başarılı', data.success.message);
          } else {
            this._service.error('Hata', data.error.message);
          }
          this.buttonDisabled = false;
        }, (err) => {
          this._service.error('Hata', 'Beklenmedik bir hata oluştu.');
          this.buttonDisabled = false;
        }
        );
    }
  }

}
