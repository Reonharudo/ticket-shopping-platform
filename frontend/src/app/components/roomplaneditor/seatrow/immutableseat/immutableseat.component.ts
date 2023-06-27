import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PersistedSeat, SeatStatus, SeatType } from 'src/app/dtos/hallplan/hallplan';

interface Size {
  width: number;
  height: number;
}

export interface SeatSelectionPayload {
  seat: PersistedSeat;
  rowNr: number;
  isSelected: boolean;
}

@Component({
  selector: 'app-immutableseat',
  templateUrl: './immutableseat.component.html',
  styleUrls: ['./immutableseat.component.scss']
})
export class ImmutableseatComponent {
  @Input() rowNr: number;
  @Input() seat: PersistedSeat;
  @Output() seatSelectionChangeEvent = new EventEmitter<SeatSelectionPayload>();

  isSelected = false;
  seatTypeEnum = SeatType;

  /**
   * If this seat is of type "seat" or "standingSeat" then it toggles its selected status
   */
  handleSeatClick() {
    const newSelectStatus = !this.isSelected;

    //update state
    this.isSelected = newSelectStatus;

    //emit event
    this.seatSelectionChangeEvent.emit({
      seat: this.seat,
      rowNr: this.rowNr,
      isSelected: newSelectStatus
    });
  }

  /**
   * Returns true if this seat is available for buy/reservation process
   *
   * @returns false if this seat has status free otherwise true
   */
  isAvailable() {
    switch (this.seat.type) {
      case SeatType.seat:
      case SeatType.vacantSeat:
        return this.seat.boughtNr === 0 && this.seat.reservedNr === 0;
      case SeatType.standingSeat:
        return this.seat.capacity !== (this.seat.boughtNr + this.seat.reservedNr);
    }
  }

  getAvailableCapacity() {
    return this.seat.capacity - (this.seat.boughtNr + this.seat.reservedNr);
  }

  calcWidthAndHeightAssCSSProperties(): Size {
    const width = this.seat.capacity * 0.07;
    const height = this.seat.capacity * 0.05;
    return {
      width,
      height
    };
  }
}

