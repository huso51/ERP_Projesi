import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import { trigger,  state,  style, transition, animate } from '@angular/animations';
import { AppSettings } from '../../../app.settings';
import { Settings } from '../../../app.settings.model';
import { MenuService } from '../menu/menu.service';
import { Router } from '@angular/router';
import { AuthService } from 'app/auth.service';
import { Menu } from 'app/theme/components/menu/menu.model';
import { User } from 'app/models/user';
import { FetchService } from 'app/pages/fetch.service';
import { GlobalTexts } from 'globals/globaltext';
import { Tenant } from 'app/models/tenant';
import { TenantUsers } from 'app/models/tenantUsers';
import { RequestOptions, URLSearchParams, Http, Headers } from '@angular/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModal } from 'app/pages/users/default-modal/default-modal.component';
import { NotificationsService } from 'angular2-notifications';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [ MenuService ],
  animations: [
    trigger('showInfo', [
      state('1' , style({ transform: 'rotate(180deg)' })),
      state('0', style({ transform: 'rotate(0deg)' })),
      transition('1 => 0', animate('400ms')),
      transition('0 => 1', animate('400ms'))
    ])
  ]
})
export class HeaderComponent implements OnInit {
  currentUser: User = JSON.parse(localStorage.getItem('currentUser'));
  tenants: TenantUsers[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });
  public showHorizontalMenu = false; 
  public showInfoContent = false;
  public settings: Settings;
  public menuItems: Array<Menu>;
  constructor(public appSettings:AppSettings, public menuService:MenuService,private router: Router,private authService:AuthService,
      private newService: FetchService, private modalService: NgbModal, private _service: NotificationsService) {
      this.settings = this.appSettings.settings;
      this.menuItems = this.menuService.getHorizontalMenuItems();
      this.tenants = new Array<TenantUsers>();
      this.header.append('Authorization', localStorage.getItem('token'));
      this.header.append('Content-Type', 'application/x-www-form-urlencoded');
      this.options = new RequestOptions({ headers: this.header });

  }
  
  ngOnInit() {
    /*if(window.innerWidth <= 768) 
      this.showHorizontalMenu = false;*/
      this.getUserTenants();
      this.showHorizontalMenu = true;
  }

  logout(){
    this.authService.logout();
    window.location.reload();
  }
  updateDefaultTenant(ind) {
    const body = new URLSearchParams();
    body.set('defaultTenantId', `${this.tenants[ind].tenant.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/updateUser`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('userid');
        localStorage.removeItem('token');
        localStorage.removeItem('permissions');
        localStorage.setItem('currentUser', JSON.stringify(data.data));
        localStorage.setItem('token', data.data.rememberToken);
        // this.router.navigate(['/']);
        window.location.reload();
      } else {
        this._service.error('Hata', data.error.message);
      }
    });
  }

  getUserTenants() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/getUserTenants`, body, this.options).
    subscribe(
    (data) => {
      if (!data.error) {
        this.tenants = data.data;
      } else {
        this._service.error('Hata', data.error.message);
      }
    });
  }

  openUserModal() {
    const activeModal = this.modalService.open(DefaultModal, {size: 'lg',
                                                              backdrop: 'static'});
    activeModal.componentInstance.id = this.currentUser.id;
    activeModal.result.then((data) => {
      // on close
    }, (reason) => {
      // on dismiss
    });
  }


  public closeSubMenus(){
    let menu = document.querySelector("#menu0"); 
    if(menu){
      for (let i = 0; i < menu.children.length; i++) {
          let child = menu.children[i].children[1];
          if(child){          
              if(child.classList.contains('show')){            
                child.classList.remove('show');
                menu.children[i].children[0].classList.add('collapsed'); 
              }             
          }
      }
    }
  }

  @HostListener('window:resize')
  public onWindowResize():void {
     if(window.innerWidth <= 768){
        this.showHorizontalMenu = false;
     }      
      else{
        this.showHorizontalMenu = true;
      }
  }
  
}
