import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SpendingTypesComponent } from './spendingtypes.component';

describe('AddressesComponent', () => {
  let component: SpendingTypesComponent;
  let fixture: ComponentFixture<SpendingTypesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SpendingTypesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SpendingTypesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
