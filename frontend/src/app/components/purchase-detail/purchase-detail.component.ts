import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PurchaseService} from '../../services/purchase.service';
import {ToastrService} from 'ngx-toastr';
import {Purchase} from '../../dtos/purchases';
import {Observable} from 'rxjs';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-purchase-detail',
  templateUrl: './purchase-detail.component.html',
  styleUrls: ['./purchase-detail.component.scss']
})
export class PurchaseDetailComponent implements OnInit {
  item: Purchase;
  user: User;

  constructor(private route: ActivatedRoute,
              private service: PurchaseService,
              private notification: ToastrService,
              private userService: UserService,
              private router: Router) {
  }

  ngOnInit() {
    this.userService.getSelf().subscribe(data => {
      this.user = data;
    });

    let purchaseNr;
    this.route.params.subscribe(params => {
      purchaseNr = params['id'];
    });
    const observable: Observable<Purchase> = this.service.getPurchase(purchaseNr);
    observable.subscribe({
      next: data => {
        this.item = data;
      }, error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Error:');
        console.error(error);
      }
    });
  }

  deletePurchase(purchaseNr: number) {

    const confirmed = window.confirm('Are you sure you want to refund this purchase? (this action cannot be undone)');
    if (confirmed === false) {
      return;
    }
    this.service.refundPurchase(purchaseNr).subscribe(
      (response) => {
        console.log('Status:', response.status);
        this.notification.success(`Ticket successfully removed from cart.`);
      },
      (error) => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Requested Performance does not exist');
        console.error(error);
      }, () => {
        this.router.navigate(['/purchases']);
      }
    );
  }

  sumOfItems(purchase: Purchase): number {
    let sum = 0;
    purchase.ticketList.forEach((element) => {
      sum += element.seat.price;
    });
    return sum;
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

}
