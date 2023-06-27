import {Component, OnInit} from '@angular/core';
import {CreatePurchase} from '../../dtos/purchases';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {SeatDetail} from '../../dtos/seatDetail';
import {ReservationService} from '../../services/reservation.service';
import {Reservation} from '../../dtos/reservation';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-reservation-checkout',
  templateUrl: './reservation-checkout.component.html',
  styleUrls: ['./reservation-checkout.component.scss']
})
export class ReservationCheckoutComponent implements OnInit {
  checkboxList: boolean[] = [];
  item: Reservation;
  creationItem: CreatePurchase = {} as CreatePurchase;
  total = 0;
  withoutTaxes = 0;
  taxes = 0;
  user: User;
  error = false;

  constructor(private route: ActivatedRoute,
              private service: ReservationService,
              private userService: UserService,
              private notification: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
    let reservationNr;
    this.route.params.subscribe(params => {
      reservationNr = params['id'];
    });

    this.userService.getSelf().subscribe(data => {
      this.user = data;
    });

    this.getItem(reservationNr);
    this.creationItem.useUserAddress = false;
    this.creationItem.address = '';
    this.creationItem.city = '';
  }

  getItem(reservationNr: number) {
    const observable: Observable<Reservation> = this.service.getReservation(reservationNr);
    observable.subscribe({
      next: data => {
        this.item = data;
        this.checkboxList = Array(this.item.reservedSeats.length).fill(true);
      }, error: error => {
        this.router.navigate(['']);
      }
    });
  }

  sumOfCheckedItems(): number {
    if (this.item === undefined) {
      return 0;
    }
    let sum = 0;
    this.item.reservedSeats.forEach((element, index) => {
      if (this.checkboxList[index] === true) {
        sum += element.price;
      }
    });
    this.total = sum;
    this.taxes = sum * 0.2;
    this.withoutTaxes = this.total - this.taxes;
    return this.total;
  }

  noItemsChecked(): boolean {
    const allFalse: boolean = this.checkboxList.every((value: boolean) => value === false);
    return allFalse;
  }

  canPurchase(): boolean {
    if (this.noItemsChecked()) {
      return false;
    }

    if (this.creationItem.useUserAddress === true){
      if (this.creationItem.address === ''){
        return false;
      }
      if (this.creationItem.city === ''){
        return false;
      }
      if (this.creationItem.areaCode === 0 || this.creationItem.areaCode === undefined){
        return false;
      }
    }

    if (this.creationItem.creditCardNr === 0 || this.creationItem.securityCode === undefined){
      return false;
    }
    const pattern = /^\d{2}\/\d{2}$/;
    if (pattern.test(this.creationItem.expiration) === false ){
      return false;
    }
    if (this.creationItem.securityCode === 0 || this.creationItem.securityCode === undefined){
      return false;
    }

    return true;
  }

  purchase(): void {
    this.creationItem.seats = [];
    this.item.reservedSeats.forEach((element, index) => {
      if (this.checkboxList[index] === true) {
        const seat: SeatDetail = {} as SeatDetail;
        seat.seatNr = element.seatNr;
        seat.price = element.price;
        seat.type = element.type;
        seat.seatRowNr = element.seatRowNr;
        seat.id = element.id;
        this.creationItem.seats.push(seat);
      }
    });

    this.service.purchaseReservation(this.creationItem, this.item.reservationNr).subscribe(
      (response) => {
        console.log('Status:', response.status);
        this.notification.success(`Ticket successfully removed from cart.`);
      },
      (error) => {
        console.error('Error:', error);
        this.notification.error(`Something went wrong... please try again!`);
      }, () => {
        this.router.navigate(['/purchases']);
      }
    );

  }
}


