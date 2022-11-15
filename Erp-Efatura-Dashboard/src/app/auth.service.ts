import { Injectable } from '@angular/core';
import { Http, Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import { GlobalTexts } from './../globals/globaltext';
import { Router } from '@angular/router';
import { NotificationsService } from 'angular2-notifications';

@Injectable()
export class AuthService {
    
    constructor(private http: Http, private router: Router, private _service: NotificationsService) { }

    login(email, password) {
      const header: Headers = new Headers();
      header.append('Content-Type', 'application/x-www-form-urlencoded');
      const options = new RequestOptions({ headers: header });
      const body = new URLSearchParams();
      
      body.set('email', email);
      body.set('password', password);
        return this.http.post(`${GlobalTexts.rest_url}auth/login`, body, options)
            .map((response: Response) => {
                // login successful if there's a jwt token in the response
               
                return response.json();
            }).subscribe((user)=>{
                if (user.data) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    localStorage.setItem('currentUser', JSON.stringify(user.data));
                    localStorage.setItem('token', user.data.rememberToken);
                    localStorage.setItem('userid', user.data.id);
                    localStorage.setItem('permissions', user.data.permissions);
                    this.router.navigate(['/']);
                }
                if (user.error.message) {
                    this._service.error('Hata', user.error.message);
                  }
            },
            (err)=>{
                let error = JSON.parse(err._body);
                this._service.error('Hata', error.error.message);
            }
        );
    }


    logout() {
        // remove user from local storage to log user out
        // localStorage.removeItem('currentUser');
        // localStorage.removeItem('userid');
        // localStorage.removeItem('token');
        // localStorage.removeItem('permissions');
        // localStorage.removeItem('items');
        localStorage.clear();
    }

     fetchUser(id) {
      const header: Headers = new Headers();
      // (localStorage.getItem('currentUser'));
      header.append('Authorization', localStorage.getItem('token'));
      header.append('Content-Type', 'application/x-www-form-urlencoded');
      const options = new RequestOptions({ headers: header });
      const body = new URLSearchParams();
      body.set('whereId', id);
    return this.http.post(`${GlobalTexts.rest_url}sessions/users/all`, body, options).map(
    (response: Response) => {
                const user = response.json();
                // (user.data["0"]);
                
                return response.json();
            },
    );
}
}
