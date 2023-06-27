import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallplanManagerComponent } from './hallplan-manager.component';

describe('HallplanManagerComponent', () => {
  let component: HallplanManagerComponent;
  let fixture: ComponentFixture<HallplanManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HallplanManagerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallplanManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
