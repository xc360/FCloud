import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalInfoDetailsComponent } from './personal-info-details.component';

describe('PersonalInfoDetailsComponent', () => {
  let component: PersonalInfoDetailsComponent;
  let fixture: ComponentFixture<PersonalInfoDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PersonalInfoDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonalInfoDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
