import { Component, OnInit } from '@angular/core';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EqualPasswordsValidator } from 'app/theme/validators';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FetchService } from 'app/pages/fetch.service';
import { GlobalTexts } from 'globals/globaltext';
import { Integration } from 'app/models/integration';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'login',
  templateUrl: './validator.html',
  styleUrls: ['./validator.scss'],
})
export class Validator implements OnInit {
  error = '';
  success: string;
  loading= false;
  code: string;
  action: string;
  public form: FormGroup;
  public password: AbstractControl;
  public repeatPassword: AbstractControl;
  public submitted = false;
  public passwords: FormGroup;
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
  
  constructor(fb: FormBuilder, private newService: FetchService, private router: Router, private _service: NotificationsService,
    private route: ActivatedRoute) {
    this.form = fb.group({
      'passwords': fb.group({
        'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
        'repeatPassword': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      }, { validator: EqualPasswordsValidator.validate('password', 'repeatPassword') }),
    });
    this.integrationInfo = new Integration();
    this.passwords = <FormGroup> this.form.controls['passwords'];
    this.password = this.passwords.controls['password'];
    this.repeatPassword = this.passwords.controls['repeatPassword'];

    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    this.route
    .queryParams
    .subscribe(params => {
      // Defaults to 0 if no query param provided.
      this.code = params['code'];
      this.action = params['action'];
    });
    if (this.code) {
      if (this.code.length < 10) {
        this.router.navigate(['/']);
      }
    } else {
      this.router.navigate(['/']);
    }
  }

  public onSubmit(values: Object): void {
    this.submitted = true;
    if (this.action === 'EinvoiceAdmin') {
      this.erpSender();
    } else if (this.action === 'EinvoiceUser') {
      this.sendRegisterInfo();
    } else {
      this.erpSender();
    }
      
  }

  erpSender() {
    if (this.form.valid) {
      // your code goes here
      const body = new URLSearchParams();
      body.set('verifyCode', this.code);
      body.set('password', this.password.value);
      body.set('action', this.action);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/verifyCode`, body, this.options)
      .subscribe(
        (data) => {
          if (!data.error) {
            this.error = '';
            this.success = data.success.message + ' 5 sn sonra login ekranına yönlendirileceksiniz...';
            this._service.success('Başarılı', data.success.message + ' 5 sn sonra login ekranına yönlendirileceksiniz...');
            setTimeout((router: Router) => {
              this.router.navigate(['login']);
          }, 5000);
          } else {
            this.success = '';
            // this.error = data.error.message;
            this._service.error('Hata', data.error.message);
          }
        }, (err) => {
          this.success = '';
          // this.error = 'Hata oluştu';
          this._service.error('Hata', 'Beklenmedik bir hata oluştu.');
      },
      );
    }
  }

  sendRegisterInfo() {
    this.buttonDisabled = true;
    this.integrationInfo.code = this.code;
    if (!this.integrationInfo.pk.includes('urn:mail')) {
      this.integrationInfo.pk = 'urn:mail:' + this.integrationInfo.pk;
    }
    if (!this.integrationInfo.pk.includes('urn:mail')) {
      this.integrationInfo.gb = 'urn:mail:' + this.integrationInfo.gb;
    }
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

