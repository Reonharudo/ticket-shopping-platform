import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { PersistedHallplan, PersistedSeat, PersistedSeatWithRowNr } from 'src/app/dtos/hallplan/hallplan';
import { HallplanService } from 'src/app/services/hallplan/hallplan.service';
import { SeatSelectionPayload } from '../roomplaneditor/seatrow/immutableseat/immutableseat.component';

@Component({
  selector: 'app-customerhallplandisplay',
  templateUrl: './customerhallplandisplay.component.html',
  styleUrls: ['./customerhallplandisplay.component.scss']
})

export class CustomerhallplandisplayComponent implements OnInit {
  @Input() hallplanId: number;
  @Output() selectionChangeEvent = new EventEmitter<PersistedSeatWithRowNr[]>();

  roomplan: PersistedHallplan = {
    id: 0,
    seatRows: [],
    name: '',
    description: '',
    isTemplate: true
  };
  selectedSeats: Map<PersistedSeat['id'], PersistedSeatWithRowNr> = new Map();

  constructor(
    private service: HallplanService,
    private notification: ToastrService,
  ) {
  }

  /**
   * Given SeatSelectionPayload is inserted or removed from internal selectedSeats map.
   * Emits an seatSelectionChangeEvent with a list of all currently selected seats
   *
   * @param payload
   */
  handleSeatSelectionEvent(payload: SeatSelectionPayload) {
    console.log(payload);
    const clonedSelectedSeats = new Map(this.selectedSeats);
    const { id } = payload.seat;

    if (payload.isSelected) {
      clonedSelectedSeats.set(id, {
        ...payload.seat,
        rowNr: payload.rowNr
      });
    } else {
      clonedSelectedSeats.delete(id);
    }
    this.selectedSeats = clonedSelectedSeats;
    this.selectionChangeEvent.emit(Array.from(clonedSelectedSeats.values()));
  }

  ngOnInit(): void {
    this.fetchHallplanWithId(this.hallplanId);
  }

  fetchHallplanWithId(id: number) {
    this.service.getHallplanById(id).subscribe({
      next: data => {
        this.roomplan = data;
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Requested Hallplan does not exist');
        console.error(error);
      }
    });
  }
}
