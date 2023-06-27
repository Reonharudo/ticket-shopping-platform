import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatrowComponent } from './seatrow.component';

describe('SeatrowComponent', () => {
  let component: SeatrowComponent;
  let fixture: ComponentFixture<SeatrowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeatrowComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatrowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
