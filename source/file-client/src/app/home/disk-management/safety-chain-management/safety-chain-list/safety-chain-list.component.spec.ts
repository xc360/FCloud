import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SafetyChainListComponent } from './safety-chain-list.component';

describe('SafetyChainComponent', () => {
  let component: SafetyChainListComponent;
  let fixture: ComponentFixture<SafetyChainListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SafetyChainListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SafetyChainListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
