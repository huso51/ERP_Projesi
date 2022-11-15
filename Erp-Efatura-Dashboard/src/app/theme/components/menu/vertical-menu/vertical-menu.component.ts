import { Title } from '@angular/platform-browser';
import { verticalMenuItems } from './../menu';
import { Component, OnInit, ViewEncapsulation, ElementRef, Input } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { MenuService } from '../menu.service';
import { AppSettings } from '../../../../app.settings';
import { Settings } from '../../../../app.settings.model';
import { Menu } from 'app/theme/components/menu/menu.model';
import { DefaultModalInvoices } from '../../../../pages/allinvoices/components/invoicesSend/default-modal/default-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalInvoicesCame } from '../../../../pages/allinvoices/components/invoicesCame/default-modal/default-modal.component';

@Component({
  selector: 'app-vertical-menu',
  templateUrl: './vertical-menu.component.html',
  styleUrls: ['./vertical-menu.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [MenuService]
})
export class VerticalMenuComponent implements OnInit {
  item = JSON.parse(localStorage.getItem('currentUser'));
  menu: Array<Menu>;
  @Input('menuItems') menuItems;
  public settings: Settings;
  constructor(public appSettings: AppSettings,
    private menuService: MenuService,
    private router: Router,
    private elementRef: ElementRef,
    private modalService: NgbModal) {
    this.settings = this.appSettings.settings;
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        window.scrollTo(0, 0);
        let activeLink = this.menuService.getActiveLink(this.menuItems);
        this.menuService.setActiveLink(this.menuItems, activeLink);
        jQuery('.tooltip').tooltip('hide');
        if (window.innerWidth <= 768) {
          this.settings.theme.showMenu = false;
        }
      }
    });
  }

  ngOnInit() {

    /*if (verticalMenuItems.findIndex(x => x.id == 4) == -1) {
      if (this.item["tenantType"] !== "CUSTOMER" && verticalMenuItems.findIndex(x => x.id == (4 || 53)) == -1) {

        verticalMenuItems.push(new Menu(4, 'Organizasyon', '/pages/customer-list', null, 'group', null, false, 0));
        verticalMenuItems.push(new Menu(53, 'Gelen İstekler', '/pages/admin-panel/incoming-request', null, 'globe', null, false, 52));
        verticalMenuItems.sort((a, b) => {
          if (a.id < b.id) return -1;
          else if (a.id > b.id) return 1;
          else return 0;
        });
      }
      if (this.item["tenantType"] !== "SUPERADMIN" && verticalMenuItems.findIndex(x => x.id == 54) == -1) {
        verticalMenuItems.push(new Menu(54, 'Yapılan İstekler', '/pages/admin-panel/sended-request', null, 'globe', null, false, 52));
        verticalMenuItems.sort((a, b) => {
          if (a.id < b.id) return -1;
          else if (a.id > b.id) return 1;
          else return 0;
        });
      }
    }*/


    let menu_wrapper = this.elementRef.nativeElement.children[0];
    this.menuService.createVerticalMenuItem
    this.menuService.createMenu(this.menuItems, menu_wrapper, 'vertical');
    if (this.settings.theme.menuType == 'mini')
      jQuery('.menu-item-link').tooltip();
  }

  ngAfterViewInit() {
    this.menuService.showActiveSubMenu(this.menuItems);
    let activeLink = this.menuService.getActiveLink(this.menuItems);
    this.menuService.setActiveLink(this.menuItems, activeLink);
  }

  addInvoice() {
    const activeModal = this.modalService.open(DefaultModalInvoices, {
      size: 'lg',
      backdrop: 'static'
    });
    activeModal.result.then((data) => {
      // on close
    }, (reason) => {
      // on dismiss
    });
  }

  addInvoiceCame() {
    const activeModal = this.modalService.open(DefaultModalInvoicesCame, {
      size: 'lg',
      backdrop: 'static',
    });
    activeModal.result.then((data) => {
      // on close
    }, (reason) => {
      // on dismiss
    });
  }
}