import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StorageComponent } from './storage.component';

describe('AddressesComponent', () => {
  let component: StorageComponent;
  let fixture: ComponentFixture<StorageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StorageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StorageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
