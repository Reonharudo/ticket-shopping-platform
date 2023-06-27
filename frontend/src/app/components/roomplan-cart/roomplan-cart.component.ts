import {Component, Input} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {ReservationService} from '../../services/reservation.service';
import {PurchaseSeat} from '../../dtos/purchases';

@Component({
  selector: 'app-roomplan-cart',
  templateUrl: './roomplan-cart.component.html',
  styleUrls: ['./roomplan-cart.component.scss']
})

export class RoomplanCartComponent {
  @Input() items?: PurchaseSeat[] = [];
  @Input() event?: number;

  constructor(private cartService: CartService,
              private reservationService: ReservationService,
              private notification: ToastrService,
              private router: Router) {
  }

  selectedSeats(): string {
    return this.items.length === 0
      ? 'no'
      : this.items.length.toString();
  }

  addToCart(seatList: PurchaseSeat[]) {
    this.expandList();

    this.cartService.addToCart(seatList).subscribe(
      (response) => {
        console.log('Status:', response.status);
        this.notification.success(`Ticket successfully removed from cart.`);
      },
      (error) => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Error:');
        console.error(error);
      }, () => {
        this.router.navigate(['/cart']);
      }
    );
  }

  maxAmount(item: PurchaseSeat): number {
    return item.capacity - item.boughtNr - item.reservedNr;
  }
  clipValue(item: PurchaseSeat){
    const max = this.maxAmount(item);
    if (item.amount < 1) {
      item.amount = 1;
    } else if ( item.amount > max ) {
      item.amount = max;
    }
  }

  reserveSeats(seatList: PurchaseSeat[]) {
    this.expandList();

    this.reservationService.createReservation(seatList).subscribe(
      (response) => {
        console.log('Status:', response.status);
        this.notification.success(`Purchase successful.`);
      },
      (error) => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Error:');
        console.error(error);
      }, () => {
        this.router.navigate(['/reservations']);
      }
    );
  }

  expandList(){
    this.items.forEach((currItem: PurchaseSeat) => {
      if (currItem.type === 'STANDING_SEAT'){
        let amount = currItem.amount;
        currItem.amount = 1;
        while (amount > 1){
          amount--;
          this.items.push(currItem);
        }
      }
    });
  }

}
