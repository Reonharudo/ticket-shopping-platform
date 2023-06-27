import {Component, OnInit} from '@angular/core';
import {EventDate} from '../../dtos/eventDate';
import {Event} from '../../dtos/event';
import {ActivatedRoute} from '@angular/router';
import {EventService} from '../../services/event.service';
import {HallplanService} from '../../services/hallplan/hallplan.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-event-detail',
  templateUrl: './event-detail.component.html',
  styleUrls: ['./event-detail.component.scss']
})
export class EventDetailComponent implements OnInit{

  eventDate: EventDate = {
    date: new Date(),
    city: '',
    areaCode: '',
    address: '',
    startingTime: '',
  };
  event: Event = {
    title: '',
    eventDatesLocation: EventDate[100] = [this.eventDate],
    duration: '',
    category: '',
    artist: '',
    description: '',
    image: '',
  };
  hallPlanNames: Record<number, string>[] = [];
  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private hallplanService: HallplanService,
    private notification: ToastrService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(async params => {
      this.event.id = parseInt(params.get('id'), 10);
      await this.fetchEventDetails();
      for (const eventDate1 of this.event.eventDatesLocation) {
        eventDate1.roomName =  await this.fetchHallplanName(eventDate1.room);
      }
    });
  }

  fetchEventDetails(): Promise<Event> {
    return new Promise((resolve, reject) => {
      this.eventService.getById(this.event.id).subscribe({
        next: data => {
          this.event= data;
          resolve(data);
        },
        error: error => {
          console.error('Error fetching event', error);
          this.notification.error(`Could not fetch this event. Errorcode: ${error.status}, Errortext: ${error.error.errors}`);
          reject(error);
        }
      });
    });
  }

  fetchHallplanName(id: number): Promise<string>{
    return new Promise((resolve, reject) => {
      this.hallplanService.getByIdAbbreviated(id).subscribe({
        next: data => {
          const rec = { key: id, value: data.name };
          this.hallPlanNames.push(rec);
          resolve(data.name);
        },
        error: error => {
          console.error('Error fetching hallplans', error);
          this.notification.error(`Could not fetch hallplans. Errorcode: ${error.status}, Errortext: ${error.error.errors}`);
          reject(error);
        }
      });
    });
  }

  transformDuration(timeString: string): string {
    const [hours, minutes] = timeString.split(':');
    return `${parseInt(hours, 10)}h ${parseInt(minutes, 10)}m`;
  }

  transformTime(timeString: string): string {
    const [hours, minutes] = timeString.split(':');
    const formattedHours = parseInt(hours, 10).toString().padStart(2, '0');
    const formattedMinutes = parseInt(minutes, 10).toString().padStart(2, '0');
    return `${formattedHours}:${formattedMinutes} (CET)`;
  }

  transformDateFormat(dateString: string): string {
    const [year, month, day] = dateString.split('-');
    return `${day}.${month}.${year}`;
  }
}
