import {Event} from './event';
import {SeatDetail} from './seatDetail';

export interface CartItem {
  seat: SeatDetail;
  event: Event;
}
