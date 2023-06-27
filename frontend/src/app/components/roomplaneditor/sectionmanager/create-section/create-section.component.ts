import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { PersistedHallplan, PersistedSeat, SeatRow } from 'src/app/dtos/hallplan/hallplan';
import { Section } from 'src/app/dtos/hallplan/section';

interface ColorItem {
  backgroundColor: string;
}

const defaultColorOptions: ColorItem[] = [
  {
    backgroundColor: '#DC965A',
  },
  {
    backgroundColor: '#6f6778',
  },
  {
    backgroundColor: '#BC69AA',
  },
  {
    backgroundColor: '#F05D23',
  },
  {
    backgroundColor: '#6DB1BF',
  },
  {
    backgroundColor: '#3F6C51'
  },
  {
    backgroundColor: '#3D348B'
  },
  //new
  {
    backgroundColor: '#d17df0'
  },
  {
    backgroundColor: '#bf8aa7'
  },
  {
    backgroundColor: '#b5835c'
  },
];

export interface CreateSectionPayload {
  section: Section;
  affectedSeatIds: PersistedSeat[];
}

@Component({
  selector: 'app-create-section',
  templateUrl: './create-section.component.html',
  styleUrls: ['./create-section.component.scss']
})
export class CreateSectionComponent implements OnChanges {
  @Input() roomplan: PersistedHallplan;
  @Output() createSectionEvent = new EventEmitter<CreateSectionPayload>();

  defaultColorOptions = defaultColorOptions;

  name = '';
  fromSeatNr = 0;
  toSeatNr = 0;
  selectedColor = '';
  price = 0;
  selectedRows: SeatRow['rowNr'][] = [];

  nameErrMessage = '';
  fromSeatNrErrMessage = '';
  toSeatNrErrMessage = '';
  priceErrMessage = '';

  createSectionButton = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.fromSeatNrErrMessage.length > 0 || this.toSeatNrErrMessage.length > 0 || this.fromSeatNr > 0 || this.toSeatNr > 0) {
      this.handleFromSeatChange(String(this.fromSeatNr), changes.roomplan.currentValue as PersistedHallplan);
      this.handleToSeatChange(String(this.toSeatNr), changes.roomplan.currentValue as PersistedHallplan);
    }
    this.refreshSelectedRows();
  };

  handleToggleRowSelect(seatrowNr: number) {
    const selectedRowsMapCloned = this.selectedRows.slice();
    if (this.selectedRows.includes(seatrowNr)) {
      const elemAtIndex = this.selectedRows.indexOf(seatrowNr);
      selectedRowsMapCloned.splice(elemAtIndex, 1); //delete from selectedRow as it was previously already selected
    } else {
      selectedRowsMapCloned.push(seatrowNr); //add to selectedRow as it was previously not selected
    }
    this.selectedRows = selectedRowsMapCloned;


    //as a new selected row may have been added/or removed we must validate fromseatnr and toseatnr again
    this.handleFromSeatChange(this.fromSeatNr.toString());
    this.handleToSeatChange(this.toSeatNr.toString());
  }

  /**
   * Compare selected rows to the seatrows in roomplan. If row is found to be deleted,
   * then it is removed from selectedRow
   */
  refreshSelectedRows() {
    for (let i = 0; i < this.selectedRows.length; i++) {
      const found = this.roomplan.seatRows.find((seatrow) => seatrow.rowNr === this.selectedRows[i]);
      if (!found) {
        //update state as seatrow did not exist
        const clonedSelectedRows = this.selectedRows.slice();
        clonedSelectedRows.splice(i, 1);
        this.selectedRows = clonedSelectedRows;
      }
    }
  };

  /**
   * Emit an CreateSectionEvent
   */
  handleAddSectionBtnClick() {
    const payload: CreateSectionPayload = {
      section: {
        name: this.name,
        price: this.price,
        color: this.selectedColor,
      },
      affectedSeatIds: this.affectedSeats()
    };

    this.createSectionEvent.emit(payload);
  }
  /**
   * PRE-COND: smallest possible seatnr is 1
   */
  affectedSeats(): PersistedSeat[] {
    const affectedSeats: PersistedSeat[] = [];

    for (let selectedSeatNr = this.fromSeatNr; selectedSeatNr <= this.toSeatNr; selectedSeatNr++) {
      for (const seatrow of this.roomplan.seatRows) {
        //check if seatrow is selected
        if (!this.selectedRows.includes(seatrow.rowNr)) {
          continue;
        }

        //find affected seat
        const affectedSeat = seatrow.seats.find(seat => seat.seatNr === selectedSeatNr);
        affectedSeats.push(affectedSeat);
      }
    }
    return affectedSeats;
  }

  isDisabled() {
    const isOk = this.nameErrMessage.length === 0 && this.fromSeatNrErrMessage.length === 0 &&
      this.toSeatNrErrMessage.length === 0 && this.selectedColor.length > 0 &&
      this.name.length > 0 && this.fromSeatNr > 0 && this.toSeatNr > 0 &&
      this.selectedRows.length > 0 && this.price > 0 && this.priceErrMessage.length === 0;

    return isOk ? null : true;
  }

  handleNameInputChange(updatedName: string) {
    if (updatedName.length === 0) {
      this.nameErrMessage = 'Name is mandatory';
    } else {
      this.nameErrMessage = '';
    }
    this.name = updatedName;
  }

  handleFromSeatChange(fromSeatInput: string, updatedRoomplan: PersistedHallplan = this.roomplan) {
    if (Number(fromSeatInput)) {
      const updatedFromSeatNr = Number(fromSeatInput);

      //update state
      this.fromSeatNr = updatedFromSeatNr;
      this.fromSeatNrErrMessage = '';

      //validate
      for (const seatrow of updatedRoomplan.seatRows) {
        //skip seatrow if not selected...
        if (!this.selectedRows.includes(seatrow.rowNr)) {
          continue;
        }

        //...otherwise validate fromseatnr
        if (updatedFromSeatNr > 0 && updatedFromSeatNr <= seatrow.seats.length) { //seatnr is ensured to be within this interval
          this.fromSeatNrErrMessage = '';
        } else {
          this.fromSeatNrErrMessage = `SeatNr: ${updatedFromSeatNr} does not exist in seatrowNr: ${seatrow.rowNr}`;
          return;
        }
      }
    } else if (fromSeatInput.length === 0) {
      this.fromSeatNrErrMessage = 'From Seat Nr is mandatory';
    }
  }

  handlePriceChange(newPriceInput: string) {
    if (newPriceInput.endsWith('.')) {
      newPriceInput += '0';
    }

    if (this.isValidCurrencyNumber(newPriceInput)) {
      const newPrice = Number(newPriceInput);

      //update state
      this.price = newPrice;

      //validate price
      if (newPrice > 0) {
        this.priceErrMessage = '';
      } else if (newPrice <= 0) {
        this.priceErrMessage = 'Price must be larger than zero';
      }
    } else if (newPriceInput.length === 0) {
      this.priceErrMessage = 'Price is mandatory';
    } else {
      this.priceErrMessage = 'Invalid price, may only contain up to two decimal points or none';
    }
  }

  /**
   *   // Check if the input is a valid number with optional two decimal places
   *
   * @param input that may resemble a number
   * @returns if given input is valid currency number
   */
  isValidCurrencyNumber(input: string) {
    return /^\d+(\.\d{1,2})?$/.test(input);
  }

  handleToSeatChange(toSeatInput: string, updatedRoomplan: PersistedHallplan = this.roomplan) {
    if (Number(toSeatInput)) {
      const updatedToSeatNr = Number(toSeatInput);

      //update state
      this.toSeatNr = updatedToSeatNr;
      this.toSeatNrErrMessage = '';

      //validate
      for (const seatrow of updatedRoomplan.seatRows) {
        //skip seatrow if not selected...
        if (!this.selectedRows.includes(seatrow.rowNr)) {
          continue;
        }

        //...otherwise validate toSeatNr
        if (updatedToSeatNr < this.fromSeatNr) {
          this.toSeatNrErrMessage = `Must be larger or equal to From Seat Nr = ${this.fromSeatNr}`;
        } else if (!(updatedToSeatNr > 0 && updatedToSeatNr <= seatrow.seats.length)) {//...check if seatNr is within seatrow range
          this.toSeatNrErrMessage = `SeatNr: ${updatedToSeatNr} does not exist in seatrowNr: ${seatrow.rowNr}`;
        }
      }
    } else if (toSeatInput.length === 0) {
      this.toSeatNrErrMessage = 'To Seat Nr is mandatory';
    }
  }
}
