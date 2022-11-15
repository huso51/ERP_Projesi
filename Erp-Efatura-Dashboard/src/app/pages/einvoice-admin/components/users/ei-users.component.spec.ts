import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EiUsersComponent } from './ei-users.component';

describe('CustomersComponent', () => {
  let component: EiUsersComponent;
  let fixture: ComponentFixture<EiUsersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EiUsersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EiUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
