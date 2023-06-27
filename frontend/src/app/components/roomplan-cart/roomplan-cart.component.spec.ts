import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomplanCartComponent } from './roomplan-cart.component';

describe('RoomplanCartComponent', () => {
  let component: RoomplanCartComponent;
  let fixture: ComponentFixture<RoomplanCartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RoomplanCartComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomplanCartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
