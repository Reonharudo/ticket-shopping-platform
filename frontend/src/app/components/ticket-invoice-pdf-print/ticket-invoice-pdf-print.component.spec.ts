import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketInvoicePdfPrintComponent } from './ticket-invoice-pdf-print.component';

describe('TicketInvoicePdfPrintComponent', () => {
  let component: TicketInvoicePdfPrintComponent;
  let fixture: ComponentFixture<TicketInvoicePdfPrintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketInvoicePdfPrintComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketInvoicePdfPrintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
