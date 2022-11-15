import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EArchiveComponent } from './earchive.component';

describe('CustomersComponent', () => {
  let component: EArchiveComponent;
  let fixture: ComponentFixture<EArchiveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EArchiveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EArchiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
