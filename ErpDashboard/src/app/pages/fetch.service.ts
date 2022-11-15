import { Injectable } from '@angular/core';
import { Http, Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

@Injectable()
export class FetchService {
    constructor(private http: Http) { }

fetchPost(url, body, options) {

  return this.http.post(url, body, options).map(
      (response: Response) => {  
                return response.json();
            },
    );
}

fetchPostNoMap(url, body, options) {

  return this.http.post(url, body, options).map(
      (response: Response) => {  
                return response;
            },
    );
}

fetchPostNull(url, options) {

    return this.http.post(url, options).map(
        (response: Response) => {  
                  return response.json();
              },
      );
  }

fetchGet(url, options) {

  return this.http.get(url, options).map(
      (response: Response) => {  
          // (response.json());
                return response.json();
            },
    );
  }

fetchGetHtml(url, options) {

  return this.http.get(url, options).map(
      (response: Response) => {  
          // (response.json());
                return response;
            },
    );
  }


}
