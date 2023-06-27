import { Component, ElementRef, EventEmitter, HostListener, Output } from '@angular/core';
import { SeatType } from 'src/app/dtos/hallplan/hallplan';

export interface SeatCreationEventPayload {
  type: SeatType;
  amount: number;
}

@Component({
  selector: 'app-contextmenu',
  templateUrl: './contextmenu.component.html',
  styleUrls: ['./contextmenu.component.scss']
})
export class ContextmenuComponent {
  @Output() seatCreationEvent = new EventEmitter<SeatCreationEventPayload>();
  @Output() outsideCreationClickEvent = new EventEmitter<void>();

  seatTypeEnum = SeatType;
  seatAmount = 1;
  private isComponentShown = false;

  constructor(private elementRef: ElementRef) { }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent) {
    if (this.isComponentShown && !this.elementRef.nativeElement.contains(event.target)) {
      this.outsideCreationClickEvent.emit();
    } else {
      this.isComponentShown = true;
    }
  }

  handleSeatAmountChange(amount: number) {
    if (amount >= 1) {
      this.seatAmount = amount;
    }
  }

  /**
   * Emits seatCreationEvent
   *
   * @param direction where to add seat
   * @param type of seat
   */
  onAdd(type: SeatType) {
    this.seatCreationEvent.emit({
      type,
      amount: this.seatAmount
    });
  }
}
