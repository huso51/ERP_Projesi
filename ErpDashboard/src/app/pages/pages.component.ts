import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { BaMenuService } from '../theme';
import { PAGES_MENU } from './pages.menu';
import { AuthService } from './../auth.service';

@Component({
  selector: 'pages',
  template: `
    <ba-sidebar></ba-sidebar>
    <ba-page-top></ba-page-top>
    <div class="al-main">
      <div class="al-content">
        <ba-content-top></ba-content-top>
        <router-outlet></router-outlet>
      </div>
    </div>
    <footer class="al-footer clearfix">
    </footer>
    <ba-back-top position="200"></ba-back-top>
    `,
})
export class Pages {
  
  constructor(private _menuService: BaMenuService, private authService: AuthService) {
  }

  ngOnInit() {
    let item = JSON.parse(localStorage.getItem('currentUser'));
    if (item['tenantUsers']['userRole']['user'] !== 'x') {
      PAGES_MENU[0]['children'].push({
        path: 'users',
        data: {
          menu: {
            title: 'general.menu.users',
            icon: 'fa fa-address-book',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
    }
    if (item['tenantUsers']['userRole']['tenant'] !== 'x' && 
        item['tenantUsers']['tenant']['tenantType'] !== 'CUSTOMER' ) {
      PAGES_MENU[0]['children'].push({
        path: 'tenants',
        data: {
          menu: {
            title: 'general.menu.tenants',
            icon: 'fa fa-industry',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
    }
    if (item['tenantUsers']['userRole']['customer'] !== 'x') {
      PAGES_MENU[0]['children'].push({
        path: 'customers',
        data: {
          menu: {
            title: 'general.menu.customers',
            icon: 'fa fa-id-card-o',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
    }
    /*if (item['tenantUsers']['userRole']['item'] !== 'x') {
      PAGES_MENU[0]['children'].push({
        path: 'categories',
        data: {
          menu: {
            title: 'general.menu.categories',
            icon: 'fa fa-list',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
    }*/
    /*if (item['tenantUsers']['userRole']['item'] !== 'x') {
      PAGES_MENU[0]['children'].push({
        path: 'items',
        data: {
          menu: {
            title: 'general.menu.items',
            icon: 'fa fa-cubes',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
    }*/
    if (item['tenantUsers']['userRole']['item'] !== 'x' &&
          item['tenantUsers']['userRole']['category'] !== 'x') {
      let object = {
        path: 'stocks',
          data: {
            menu: {
              title: 'general.menu.stocks',
              icon: 'fa fa-cubes',
              selected: false,
              expanded: false,
              order: 0,
            },
          },
          children: [],
      };
      if (item['tenantUsers']['userRole']['category'] !== 'x') {
        object.children.push({
        path: 'categories',
        data: {
          menu: {
            title: 'general.menu.categories',
            icon: 'fa fa-list',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
      }
      if (item['tenantUsers']['userRole']['item'] !== 'x') {
        object.children.push({path: 'items',
          data: {
            menu: {
              title: 'general.menu.items',
              icon: 'fa fa-archive',
              selected: false,
              expanded: false,
              order: 0,
            },
        },
        });
      }
      
      PAGES_MENU[0]['children'].push(object);
    }
    if (item['tenantUsers']['userRole']['invoice'] !== 'x') {
      let object = {
        path: 'allinvoices',
        data: {
          menu: {
            title: 'general.menu.allinvoices',
            icon: 'fa fa-calculator',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
        children: [],
      };
      object.children.push({
        path: 'invoices',
        data: {
          menu: {
            title: 'general.menu.invoices',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
      object.children.push({
        path: 'invoicesCame',
        data: {
          menu: {
            title: 'general.menu.invoicesCame',
            selected: false,
            expanded: false,
            order: 0,
          },
        },
      });
      PAGES_MENU[0]['children'].push(object);

      if (item['tenantUsers']['tenant']['tenantType'] === 'SUPERADMIN') {
        PAGES_MENU[0]['children'].push({
          path: 'manage',
          data: {
            menu: {
              title: 'general.menu.manage',
              icon: 'fa fa-tasks',
              selected: false,
              expanded: false,
              order: 0,
            },
          },
        });
      }

      if (item['tenantUsers']['userRole']['isTenantAdmin']) {
        PAGES_MENU[0]['children'].push({
          path: 'options',
          data: {
            menu: {
              title: 'general.menu.options',
              icon: 'fa fa-cogs',
              selected: false,
              expanded: false,
              order: 0,
            },
          },
        });
      }

    }
    this._menuService.updateMenuByRoutes(<Routes>PAGES_MENU);
  }
}
