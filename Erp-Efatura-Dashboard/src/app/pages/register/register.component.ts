import { Component } from '@angular/core';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { EmailValidator, EqualPasswordsValidator } from '../../theme/validators';
import { FetchService } from 'app/pages/fetch.service';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { GlobalTexts } from 'globals/globaltext';
import { NotificationsService } from 'angular2-notifications';
@Component({
  selector: 'register',
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class Register {

  public form: FormGroup;
  public tc: AbstractControl;
  public name: AbstractControl;
  public email: AbstractControl;
  public surname: AbstractControl;
  public passwords: FormGroup;
  error: string;
  success: string;

  public submitted: boolean = false;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(fb: FormBuilder, private newService: FetchService, private _service: NotificationsService) {

    this.form = fb.group({
      'tc': ['', Validators.compose([Validators.required, Validators.minLength(10)])],
      'name': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      'email': ['', Validators.compose([Validators.required, EmailValidator.validate])],
      'surname': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
    });

    this.tc = this.form.controls['tc'];
    this.name = this.form.controls['name'];
    this.email = this.form.controls['email'];
    this.surname = this.form.controls['surname'];

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
      body.set('tc', this.tc.value);
      body.set('name', this.name.value);
      body.set('surname', this.surname.value);
      body.set('email', this.email.value);
      this.newService.fetchPost(`${GlobalTexts.rest_url}auth/register`, body, this.options)
      .subscribe(
        (data) => {
          if (data.data && !data.error) {
            this.error = '';
            // this.success = 'Kay??t ba??ar??l??. L??tfen onay i??in mailinizi kontrol ediniz.';
            this._service.success('Ba??ar??l??', data.success.message);
          } else {
            this.success = '';
            // this.error = data.error.message;
            this._service.error('Hata', data.error.message);
          }
        }, (err) => {
            this.success = '';
            // this.error = 'Hata olu??tu';
            this._service.error('Hata', 'Hata olu??tu');
        },
      );
    }
  }
}
