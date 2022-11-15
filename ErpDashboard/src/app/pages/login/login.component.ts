import { Component } from '@angular/core';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from './../../auth.service';
import { Router } from '@angular/router';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FetchService } from 'app/pages/fetch.service';
import { GlobalTexts } from 'globals/globaltexts';

@Component({
  selector: 'login',
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class Login {
  error = '';
  success: string;
  loading= false;
  public form: FormGroup;
  public email: AbstractControl;
  public password: AbstractControl;
  public submitted: boolean = false;

  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

  constructor(fb: FormBuilder, private authService: AuthService, private router: Router,
     private newService: FetchService) {
    this.form = fb.group({
      'email': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
    });

    this.email = this.form.controls['email'];
    this.password = this.form.controls['password'];

    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  public onSubmit(values: Object): void {
    this.submitted = true;
    if (this.form.valid) {
      // your code goes here
      // (values);
      const body = new URLSearchParams();
      body.set('email', this.email.value);
      body.set('password', this.password.value);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/login`, body, this.options)
      .subscribe((result) => {
            console.log(result);
                if (!result.error.status) {
                    // login successful
                    localStorage.setItem('currentUser', JSON.stringify(result.data));
                    localStorage.setItem('userid', result.data.id);
                    localStorage.setItem('token', result.data.rememberToken);
                    localStorage.setItem('permissions', result.data.permissions);
                   this.router.navigate(['/']);
                } else {
                    // login failed
                    this.error = result.error.message;
                    this.loading = false;
                }
            }, (err) => {
              console.log(err);
              this.error = err;
              this.loading = false;
            },
          );
    }
  }

  forgotPassword() {
    if (this.email.valid) {
      // your code goes here
      // (values);
      const body = new URLSearchParams();
      body.set('email', this.email.value);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/sendPasswordCode`, body, this.options)
      .subscribe(
        (data) => {
          if (data.data && !data.error.message) {
            this.error = '';
            this.success = 'İstek başarılı. Lütfen şifre değiştirme bağlantısı için mailinizi kontrol ediniz.';
          } else {
            this.success = '';
            this.error = data.error.message;
          }
        }, (err) => {
          this.success = '';
            this.error = 'Hata oluştu';
        },
      );
    } else {
      this.success = '';
      this.error = 'Şifre değiştirme isteği için uygun bir mail adresi giriniz';
    }
  }

}
