import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareFileListComponent } from './share-file-list.component';

describe('ShareFileComponent', () => {
  let component: ShareFileListComponent;
  let fixture: ComponentFixture<ShareFileListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShareFileListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareFileListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
