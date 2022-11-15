import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormControl, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { CustomValidators } from 'ng2-validation';
import { GlobalTexts } from 'globals/globaltext';
import { RequestOptions, Headers, Response, URLSearchParams } from '@angular/http';
import { AuthService } from 'app/auth.service';
import { ToastrService } from 'ngx-toastr';
import { FetchService } from 'app/pages/fetch.service';
declare var jQuery: any;
import { BlockUI, NgBlockUI } from 'ng-block-ui';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class LoginComponent {
  @BlockUI() blockUI: NgBlockUI;
  error = '';
  success: string;
  loading = false;
  public router: Router;
  public form: FormGroup;
  
  public email: AbstractControl;
  public password: AbstractControl;
  emailForgot: string = '';
  base64: string;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

  constructor(router: Router, private newService: FetchService, fb: FormBuilder, private authService: AuthService,
  private _service:ToastrService) {
    this.router = router;
    this.form = fb.group({
      'email': ['', Validators.compose([Validators.required, CustomValidators.email])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(5)])]
    });

    this.email = this.form.controls['email'];
    this.password = this.form.controls['password'];

    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  public onSubmit(values: Object): void {
    if (this.form.valid) {
      this.base64 = window.btoa(this.email.value + ':' + this.password.value);
      const header = new Headers();
      header.append('Authorization', 'Basic ' + this.base64);
      header.append('Content-Type', 'application/x-www-form-urlencoded');
      const option = new RequestOptions({ headers: header });
      this.authService.login(this.email.value, this.password.value);
    }
  }
  forgotPassword() {
    
      this.blockUI.start();
      const body = new URLSearchParams();
      body.set('email', this.emailForgot);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/sendPasswordCode`, body, this.options)
        .subscribe(
        (data) => {
          if (data.data && !data.error) {
            this.error = '';
            this.blockUI.stop();
            this._service.success('Başarılı', data.success.message);
            this.emailForgot = '';
            jQuery('#myModal').modal('hide');
          } else {
            this._service.error(data.error.message);
            this.success = '';
            this.error = data.error.message;
            jQuery('#myModal').modal('hide');
          }
        }, (err) => {
          this.success = '';
          this._service.error('Hata oluştu');
          this.error = 'Hata oluştu';
          jQuery('#myModal').modal('hide');
        },
      );

  }

  ngAfterViewInit() {
    document.getElementById('preloader').classList.add('hide');
  }

}
