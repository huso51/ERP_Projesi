import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Headers, Response, RequestOptions, URLSearchParams } from '@angular/http';
import { AuthService } from '../../../auth.service';
import { FetchService } from '../../../pages/fetch.service';
import { GlobalTexts } from './../../../../globals/globaltexts';

import { GlobalState } from '../../../global.state';
import { TenantUser } from 'app/models/tenantUser';
import { User } from 'app/models/user';
import { TenantUsers } from 'app/models/tenantUsers';
import { DefaultModal } from 'app/pages/users/default-modal/default-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'ba-page-top',
  templateUrl: './baPageTop.html',
  styleUrls: ['./baPageTop.scss'],
})
export class BaPageTop implements OnInit {
  currentUser: User = JSON.parse(localStorage.getItem('currentUser'));
  user= [];
  public isScrolled: boolean = false;
  public isMenuCollapsed: boolean = true;
  tenants: TenantUsers[];
  header: Headers = new Headers();
  options = new RequestOptions({ headers: this.header });

  constructor(private _state: GlobalState, private authService: AuthService, private newService: FetchService,
                private router: Router, private modalService: NgbModal) {
    this._state.subscribe('menu.isCollapsed', (isCollapsed) => {
      this.isMenuCollapsed = isCollapsed;
      
    });
    this.tenants = new Array<TenantUsers>();
      this.header.append('Authorization', localStorage.getItem('token'));
      this.header.append('Content-Type', 'application/x-www-form-urlencoded');
      this.options = new RequestOptions({ headers: this.header });
  }
  ngOnInit() {
    //localStorage.getItem('currentUser')
    /*this.authService.fetchUser(localStorage.getItem('userid')).
    subscribe(
    (data) => this.user = data.data['0'],
    );*/
    this.getUserTenants();
  }

  getUserTenants() {
    const body = new URLSearchParams();
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/getUserTenants`, body, this.options).
    subscribe(
    (data) => {
      this.tenants = data.data;
    });
  }

  updateDefaultTenant(ind) {
    const body = new URLSearchParams();
    body.set('defaultTenantId', `${this.tenants[ind].tenant.id}`);
    this.newService.fetchPost(`${GlobalTexts.rest_url}sessions/users/updateUser`, body, this.options).
    subscribe(
    (data) => {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('userid');
      localStorage.removeItem('token');
      localStorage.removeItem('permissions');
      localStorage.setItem('currentUser', JSON.stringify(data.data));
      localStorage.setItem('token', data.data.rememberToken);
      // this.router.navigate(['/']);
      window.location.reload();
    });
  }

  public toggleMenu() {
    this.isMenuCollapsed = !this.isMenuCollapsed;
    this._state.notifyDataChanged('menu.isCollapsed', this.isMenuCollapsed);
    return false;
  }

  public scrolledChanged(isScrolled) {
    this.isScrolled = isScrolled;
  }
  public logout() {
    this.authService.logout();
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
}
