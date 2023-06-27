import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HallplanCreateComponent } from './hallplan-create.component';

describe('HallplanCreateComponent', () => {
  let component: HallplanCreateComponent;
  let fixture: ComponentFixture<HallplanCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HallplanCreateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HallplanCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
