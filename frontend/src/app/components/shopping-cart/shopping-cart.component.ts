import { Component, OnInit } from '@angular/core';
import {CartItem} from '../../dtos/cartItem';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {CartService} from '../../services/cart.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.scss']
})
export class ShoppingCartComponent implements OnInit{
  items: CartItem[] = [];
  constructor(private service: CartService,
              private notification: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.getItems();
  }

  getItems(){
    const observable: Observable<CartItem[]> = this.service.getCart();
    observable.subscribe({
      next: data => {
        this.items = data;
      }, error: error => {
        this.router.navigate(['']);
      }
    });
  }

  deleteByIndex(index: number){
    this.service.deleteCartItemById(this.items[index].seat.id).subscribe(
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
        this.items.splice(index,1);
      }
    );

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
    this.items.forEach((element) =>{
      sum += element.seat.price;
    });
    return sum;
  }

  checkout(): void {
    this.router.navigate(['/cart/checkout']);
  }
}
