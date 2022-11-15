import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EinvoiceRegisterComponent } from './einvoice-register.component';

describe('AddressesComponent', () => {
  let component: EinvoiceRegisterComponent;
  let fixture: ComponentFixture<EinvoiceRegisterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EinvoiceRegisterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EinvoiceRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
