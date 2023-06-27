import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketValidatorComponent } from './ticket-validator.component';

describe('TicketValidatorComponent', () => {
  let component: TicketValidatorComponent;
  let fixture: ComponentFixture<TicketValidatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketValidatorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketValidatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
