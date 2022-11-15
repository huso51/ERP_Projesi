import { Component, ViewEncapsulation, OnInit } from '@angular/core';
import { AppSettings } from '../../../app.settings';
import { Settings } from '../../../app.settings.model';
import { GlobalTexts } from 'globals/globaltext';
import { MetricsModel } from 'app/models/metricsModel';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { FetchService } from 'app/pages/fetch.service';
import { User } from 'app/models/user';
import { EInvoiceStatistic } from '../../../models/einvoiceStatistic';
import { DefaultModalCredits } from '../../einvoice-admin/components/credits/default-modal/default-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NotificationsService } from 'angular2-notifications';


@Component({
  selector: 'app-info-panels',
  templateUrl: './info-panels.component.html',
  styleUrls: ['./info-panels.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class InfoPanelsComponent implements OnInit {
  
  public previousShowMenuOption:boolean;
  public previousMenuOption:string;
  public previousMenuTypeOption:string;
  public settings: Settings;
  
  currentUser: User = JSON.parse(localStorage.getItem('currentUser'));
  custom: MetricsModel;
  customer: any;
  credit = 0;
  customerCount = 0;
  incomingInvoices = 0;
  sendedInvoice = 0;
  statistic: EInvoiceStatistic;
  ngOnInit(): void {
    this.initPreviousSettings();
    this.getDashboardValues();
    // this.getMetrics();
  }
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

  constructor(public appSettings: AppSettings, private newService: FetchService, private modalService: NgbModal,
  private _service: NotificationsService) {
    this.header.append('Authorization', localStorage.getItem('token'));
    this.header.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = new RequestOptions({ headers: this.header });
    this.settings = this.appSettings.settings;
    this.statistic = new EInvoiceStatistic();
  }

  public getDashboardValues() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/gib/getDashboard`, body, this.options).
      subscribe(
      (data) => {
        if (!data.error) {
          this.customerCount = data.data.customerCount;
          this.incomingInvoices =  data.data.inbox.length;
          this.sendedInvoice = data.data.outbox.length;
          this.credit = data.data.remainingExpireDate;
          this.statistic = data.data.statistic;
        } else {
          this._service.error('Hata', data.error.message);
        }

      },
    );
  }
  public getMetrics() {
    this.newService.fetchGet(`${GlobalTexts.rest_apiUrl}metrics`, this.options)
      .subscribe(data => {
        this.custom = data;
        this.credit = this.custom.credit;
        this.incomingInvoices = this.custom.inboxInvoiceCount;
        this.sendedInvoice = this.custom.outboxInvoiceCount;
      });
  }

  sendCreditRequest() {
    const activeModal = this.modalService.open(DefaultModalCredits, {size: 'lg', backdrop: 'static'});
    if (this.currentUser.tenantUsers.userRole.isEinvoiceAdmin) {
      activeModal.componentInstance.id = 'AdminToSuperAdmin';
    } else if (!this.currentUser.tenantUsers.userRole.isSuperAdmin) {
      activeModal.componentInstance.id = 'UserToAdmin';
    }
    
    activeModal.result.then((data) => {
      // on close
    }, (reason) => {
      // on dismiss
    });
  }

  public infoLabelFormat(c): string {
    switch(c.data.name) {
      case 'Müşteri Sayısı':
        return `<i class="fa fa-shopping-cart mr-2"></i>${c.label}`;
      case 'Gelen Fatura':
        return `<i class="fa fa-thumbs-o-up mr-2"></i>${c.label}`;
      case 'Giden Fatura':
        return `<i class="fa fa-download mr-2"></i>${c.label}`;
      case 'Kontör Miktarı':
        return `<i class="fa fa-money mr-2"></i>${c.label}`;
     /* case 'messages':
        return `<i class="fa fa-comment-o mr-2"></i>${c.label}`;
      case 'members':
        return `<i class="fa fa-user mr-2"></i>${c.label}`;*/
      default:
        return c.label;
    }
  }

  public infoValueFormat(c): string {
    switch(c.data.extra ? c.data.extra.format : '') {
      case 'currency':
        return `\$${Math.round(c.value).toLocaleString()}`;
      case 'percent':
        return `${Math.round(c.value * 100)}%`;
      default:
        return c.value.toLocaleString();
    }
  }


  public onSelect(event) {
    
  }



  public ngDoCheck() {
    // if(this.checkAppSettingsChanges()) {
    //   setTimeout(() => this.sales = [...this.sales] );
    //   setTimeout(() => this.likes = [...this.likes] );
    //   setTimeout(() => this.downloads = [...this.downloads] );
    //   setTimeout(() => this.profit = [...this.profit] );
    //  /* setTimeout(() => this.messages = [...this.messages] );
    //   setTimeout(() => this.members = [...this.members] );*/
    //   this.initPreviousSettings();
    // }
  }

  public checkAppSettingsChanges(){
    if(this.previousShowMenuOption != this.settings.theme.showMenu ||
      this.previousMenuOption != this.settings.theme.menu ||
      this.previousMenuTypeOption != this.settings.theme.menuType){
      return true;
    }
    return false;
  }

  public initPreviousSettings(){
    this.previousShowMenuOption = this.settings.theme.showMenu;
    this.previousMenuOption = this.settings.theme.menu;
    this.previousMenuTypeOption = this.settings.theme.menuType;
  }

}
