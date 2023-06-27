import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBlockUnblockComponent } from './admin-block-unblock.component';

describe('AdminBlockUnblockComponent', () => {
  let component: AdminBlockUnblockComponent;
  let fixture: ComponentFixture<AdminBlockUnblockComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminBlockUnblockComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBlockUnblockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
