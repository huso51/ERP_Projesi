import { Component, ViewEncapsulation } from '@angular/core';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { GlobalTexts } from 'globals/globaltext';
import { FetchService } from 'app/pages/fetch.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  encapsulation: ViewEncapsulation.None
})
export class DashboardComponent  {
  

  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  

  constructor(private newService: FetchService) {

    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }


}
