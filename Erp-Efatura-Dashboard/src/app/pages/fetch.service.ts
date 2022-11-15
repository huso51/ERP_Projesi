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
    dovizGet(url) {

        return this.http.get(url).map(
            (response: Response) => {
                // (response.json());
                return response.json();
            },
        );
    }

    getLogin(url, options) {
        return this.http.get(url, options).map(
            (response: Response) => {
                if (response.status !== 200) {
                    throw new Error(response.statusText);
                }
                return response.json();

            }
        )
    }

    fetchDelete(url,options){
        return this.http.delete(url,options)
    }

    fetchPut(url,body,options){
        return this.http.put(url,body,options).map(
            (response:Response)=>{
               return response.json();
            }
        )
    }
    fetchPutNoMap(url,body,options){
        return this.http.put(url,body,options);     
    }
    getRequest(url){
        return this.http.get(url).map(
            (response: Response) => {
                // (response.json());
                return response.json();
            },
        );
    }
    


}
