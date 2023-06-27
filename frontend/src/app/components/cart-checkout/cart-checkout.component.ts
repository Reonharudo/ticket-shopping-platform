import {Component, OnInit} from '@angular/core';
import {CartItem} from '../../dtos/cartItem';
import {CartService} from '../../services/cart.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {CreatePurchase} from '../../dtos/purchases';
import {SeatDetail} from '../../dtos/seatDetail';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-cart-checkout',
  templateUrl: './cart-checkout.component.html',
  styleUrls: ['./cart-checkout.component.scss']
})
export class CartCheckoutComponent implements OnInit {
  registerForm: FormGroup;
  items: CartItem[] = [];
  creationItem: CreatePurchase = {} as CreatePurchase;
  total = 0;
  withoutTaxes = 0;
  taxes = 0;
  user: User;
  error = false;

  constructor(private service: CartService,
              private userService: UserService,
              private notification: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.userService.getSelf().subscribe(data => {
      this.user = data;
    });
    this.getItems();
    this.creationItem.useUserAddress = false;
    this.creationItem.address = '';
    this.creationItem.city = '';
  }

  getItems() {
    const observable: Observable<CartItem[]> = this.service.getCart();
    observable.subscribe({
      next: data => {
        this.items = data;
      }, error: error => {
        this.router.navigate(['']);
      }
    });
  }

  formatTime(time: string): Date {
    const parts = time.split(':');
    const hours = Number(parts[0]);
    const minutes = Number(parts[1]);

    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    return date;
  }

  sumOfItems(): number {
    let sum = 0;
    this.items.forEach((element) => {
      sum += element.seat.price;
    });
    this.total = sum;
    this.taxes = sum * 0.2;
    this.withoutTaxes = this.total - this.taxes;
    return this.total;
  }

  canPurchase(): boolean {
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
    this.items.forEach((element) => {
      const seat: SeatDetail = {} as SeatDetail;
      seat.seatNr = element.seat.seatNr;
      seat.price = element.seat.price;
      seat.type = element.seat.type;
      seat.seatRowNr = element.seat.seatRowNr;
      seat.id = element.seat.id;
      this.creationItem.seats.push(seat);
    });
    this.service.purchaseCart(this.creationItem).subscribe(
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
