import {Event} from './event';
import {SeatDetail} from './seatDetail';

export interface Reservation {
  reservationNr: number;
  event: Event;
  reservationDate: Date;
  reservedSeats: SeatDetail[];
}
