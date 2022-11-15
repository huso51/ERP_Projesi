import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FormGroup, FormControl, NgForm } from '@angular/forms';

import { FetchService } from './../../fetch.service';
import { User } from 'app/models/user';
import { NotificationsService } from 'angular2-notifications';
import { GlobalTexts } from 'globals/globaltext';
import { SpendingType } from '../../../models/spendingType';
import { AccountActivity } from '../../../models/accountActivity';
import { CustomerPayment } from '../../../models/customerPayment';
import * as jspdf from 'jspdf';  
import html2canvas from 'html2canvas';

@Component({
  selector: 'add-service-modal',
  styleUrls: [('./default-modal.component.scss')],
  templateUrl: './default-modal.component.html',
})

export class DefaultModalCustomerActivities implements OnInit {
  user: User = JSON.parse(localStorage.getItem('currentUser'));
  accountActivities: CustomerPayment[];
  id: string;
  limit= 20;
  offset= 0;
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  constructor(private activeModal: NgbActiveModal, private newService: FetchService,
  private _service: NotificationsService) {
    this.accountActivities = new Array<CustomerPayment>();
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
  }

  ngOnInit() {
    if (this.id) {
      this.getCustomerPayments();
    }
  }

  getCustomerPayments() {
    const body = new URLSearchParams();
    body.set('customerId', `${this.id}`);
    body.set('limit', `${this.limit}`);
    body.set('offset', `${this.offset * this.limit}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/account/getCustomerPayments`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.accountActivities = data.data;
        // console.log(this.accountActivities);
      } else {
        this._service.error('Hata', data.error.message);
      }
    },
    );
  }

  closeModal() {
    this.activeModal.close();
    
  }

  public captureScreen() {  
    const data = document.getElementById('contentToConvert');  
    html2canvas(data).then(canvas => {  
      // Few necessary setting options  
      const imgWidth = 208;   
      const pageHeight = 295;    
      const imgHeight = canvas.height * imgWidth / canvas.width;  
      const heightLeft = imgHeight;  
  
      const contentDataURL = canvas.toDataURL('image/png')  
      const pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF  
      const position = 0;  
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight)  
      pdf.save(this.user.tenantUsers.tenant.name + '-' + Date.now() + '-Pdf.pdf'); // Generated PDF   
    });  
  }

}
