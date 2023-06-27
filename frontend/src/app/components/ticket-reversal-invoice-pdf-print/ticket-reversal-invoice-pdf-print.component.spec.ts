import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketReversalInvoicePdfPrintComponent } from './ticket-reversal-invoice-pdf-print.component';

describe('TicketReversalInvoicePdfPrintComponent', () => {
  let component: TicketReversalInvoicePdfPrintComponent;
  let fixture: ComponentFixture<TicketReversalInvoicePdfPrintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketReversalInvoicePdfPrintComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketReversalInvoicePdfPrintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
