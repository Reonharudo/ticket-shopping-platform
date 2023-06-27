import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SeatType } from 'src/app/dtos/hallplan/hallplan';
import { Section } from 'src/app/dtos/hallplan/section';

interface Size{
  width: number;
  height: number;
}

@Component({
  selector: 'app-seat',
  templateUrl: './seat.component.html',
  styleUrls: ['./seat.component.scss']
})
export class SeatComponent {
  @Input() showSeatNr = false;

  @Input() type: SeatType;
  @Input() id: number;
  @Input() section: Section;
  @Input() capacity: number;
  @Input() seatNr: number;

  @Output() seatRemoval = new EventEmitter<number>();

  seatTypeEnum = SeatType;

  calcWidthAndHeightAssCSSProperties(): Size{
    const width = this.capacity * 0.05;
    const height = this.capacity * 0.03;
    return {
      width,
      height
    };
  }
}
