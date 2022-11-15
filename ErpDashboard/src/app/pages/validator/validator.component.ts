import { Component, OnInit } from '@angular/core';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EqualPasswordsValidator } from 'app/theme/validators';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FetchService } from 'app/pages/fetch.service';
import { GlobalTexts } from "globals/globaltexts";

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
  public submitted: boolean = false;
  public passwords: FormGroup;
  header: Headers = new Headers();
  
  options = new RequestOptions({ headers: this.header }); 
  
  constructor(fb: FormBuilder, private newService: FetchService, private router: Router,
    private route: ActivatedRoute) {
    this.form = fb.group({
      'passwords': fb.group({
        'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
        'repeatPassword': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      }, { validator: EqualPasswordsValidator.validate('password', 'repeatPassword') }),
    });

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
    if (this.form.valid) {
      // your code goes here
      const body = new URLSearchParams();
      body.set('verifyCode', this.code);
      body.set('password', this.password.value);
      body.set('action', this.action);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/verifyCode`, body, this.options)
      .subscribe(
        (data) => {
          if (data.data && !data.error.message) {
            this.error = '';
            this.success = 'Şifre belirleme başarılı. 5 sn sonra login ekranına yönlendirileceksiniz...';
            setTimeout((router: Router) => {
              this.router.navigate(['login']);
          }, 5000);
          } else {
            this.success = '';
            this.error = data.error.message;
          }
        }, (err) => {
          this.success = '';
          this.error = 'Hata oluştu';
      },
      );
    }
      
  }
}

