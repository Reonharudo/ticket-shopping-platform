import {SeatDetail} from './seatDetail';
import {Event} from './event';

export interface TicketSeat {
  ticketNr: number;
  seat: SeatDetail;
  event: Event;
}
