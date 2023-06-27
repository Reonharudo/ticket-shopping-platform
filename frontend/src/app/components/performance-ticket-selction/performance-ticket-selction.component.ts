import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {PersistedSeat} from '../../dtos/hallplan/hallplan';
import {EventService} from '../../services/event.service';
import {Performance} from '../../dtos/event';

@Component({
  selector: 'app-performance-ticket-selction',
  templateUrl: './performance-ticket-selction.component.html',
  styleUrls: ['./performance-ticket-selction.component.scss']
})
export class PerformanceTicketSelctionComponent implements OnInit{
  hallplanId: number;
  selectedSeats: PersistedSeat[] = [];
  performance: Performance = {} as Performance;
  constructor(private route: ActivatedRoute,
              private notification: ToastrService,
              private eventService: EventService,
              private router: Router) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.hallplanId = params['id'];
    });
    this.getPerformance(this.hallplanId);
  }

getPerformance(hallplanId: number): void{
    this.eventService.getPerformance(hallplanId).subscribe({
      next: data => {
        this.performance = data;
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Requested Performance does not exist');
        console.error(error);
      }
    });
}

  handleEvent(selectedSeats: PersistedSeat[]): void {
    this.selectedSeats = selectedSeats;
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
