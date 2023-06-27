import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceTicketSelctionComponent } from './performance-ticket-selction.component';

describe('PerformanceTicketSelctionComponent', () => {
  let component: PerformanceTicketSelctionComponent;
  let fixture: ComponentFixture<PerformanceTicketSelctionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PerformanceTicketSelctionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PerformanceTicketSelctionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
