import {Component, Input} from '@angular/core';
import {SeatDetail} from '../../dtos/seatDetail';
import {Event} from '../../dtos/event';

@Component({
  selector: 'app-ticket-list-item',
  templateUrl: './ticket-list-item.component.html',
  styleUrls: ['./ticket-list-item.component.scss']
})
export class TicketListItemComponent {
  @Input() ticketnr?: number;
  @Input() isReservation?: boolean;
  @Input() seat: SeatDetail;
  @Input() event: Event;
  @Input() isCanceled?: boolean = false;
  formatTime(time: string): Date {
    const parts = time.split(':');
    const hours = Number(parts[0]);
    const minutes = Number(parts[1]);

    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    return date;
  }

  formatPrice(price: number): string {
    return price.toFixed(2).replace('.',',');
  }
}
