import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationCheckoutComponent } from './reservation-checkout.component';

describe('ReservationCheckoutComponent', () => {
  let component: ReservationCheckoutComponent;
  let fixture: ComponentFixture<ReservationCheckoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReservationCheckoutComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservationCheckoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
