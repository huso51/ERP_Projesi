import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';
import { User } from './../../../models/user';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DefaultModalCredits } from './../../../pages/einvoice-admin/components/credits/default-modal/default-modal.component';

@Component({
  selector: 'app-invoice-panels',
  templateUrl: './invoice-panels.component.html',
  styleUrls: ['./invoice-panels.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class InvoicePanelsComponent implements OnInit {
  currentUser: User = JSON.parse(localStorage.getItem('currentUser'));
  constructor(private modalService: NgbModal) { }

  ngOnInit() {
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

}
