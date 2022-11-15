import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicesCameComponent } from './invoicesCame.component';

describe('CustomersComponent', () => {
  let component: InvoicesCameComponent;
  let fixture: ComponentFixture<InvoicesCameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InvoicesCameComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoicesCameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
