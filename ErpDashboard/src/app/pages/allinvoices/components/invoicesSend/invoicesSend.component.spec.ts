import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicesSendComponent } from './invoicesSend.component';

describe('CustomersComponent', () => {
  let component: InvoicesSendComponent;
  let fixture: ComponentFixture<InvoicesSendComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InvoicesSendComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoicesSendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
