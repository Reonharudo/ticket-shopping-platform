import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {Event} from 'src/app/dtos/event';
import {EventService} from '../../services/event.service';
import {ToastrService} from 'ngx-toastr';
import {EventDate} from 'src/app/dtos/eventDate';
import {AbbreviatedHallplan} from '../../dtos/hallplan/abbreviatedHallplan';
import {HallplanService} from '../../services/hallplan/hallplan.service';
import {of} from 'rxjs';
import {PersistedHallplan} from '../../dtos/hallplan/hallplan';
import {Router} from '@angular/router';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
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
  today: Date;
  eventForm: FormGroup;
  currentPage = 0;
  pageSize = 5;
  selectedRoomplan = '';
  roomplans = [];
  distance = 1;
  throttle = 2;

  constructor(private fb: FormBuilder,
              private service: EventService,
              private hallplanService: HallplanService,
              private notification: ToastrService,
              private router: Router) {
    this.today = new Date(new Date().toISOString().split('T')[0]);
  }
  observableRoomplans = (input: string) => (input === '')
    ? of ([])
    : this.hallplanService.getRoomplans(this.currentPage, input);


  ngOnInit(): void {
    this.eventForm = this.fb.group({
      title: ['', Validators.required],
      dateLocation: [EventDate, Validators.required],
      duration: ['', Validators.required],
      category: ['', Validators.required],
      artist: ['', Validators.required],
      description: [''],
      image: ['']
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.event.image = reader.result as string;
      };
    }
  }

  addDate() {
    this.event.eventDatesLocation.push(new EventDate());
  }


  deleteEntry(index: number) {
    this.event.eventDatesLocation.splice(index, 1);
  }

  async onSubmit() {
    console.log(this.event);
    this.eventForm.controls['title'].setValue(this.event.title);
    this.eventForm.controls['dateLocation'].setValue(this.event.eventDatesLocation);
    this.eventForm.controls['image'].setValue(this.event.image);
    this.eventForm.controls['duration'].setValue(this.event.duration);
    this.eventForm.controls['category'].setValue(this.event.category);
    this.eventForm.controls['description'].setValue(this.event.description);
    this.eventForm.controls['artist'].setValue(this.event.artist);
    for (const eventDate1 of this.event.eventDatesLocation) {
      await this.prepareHallplan(eventDate1);
    }
    console.log(this.eventForm);
    if (this.eventForm.valid) {
      console.log(this.event);
      const observable = this.service.create(this.event);
      observable.subscribe({
        next: data => {
          this.notification.success(`Event ${this.event.title} successfully created.`);
          this.router.navigateByUrl('/events');
        },
        error: error => {
          console.error('Error creating event', error);
          this.notification.error(`Could not create a new event. Errorcode: ${error.status}, Errortext: ${error.error.errors}`);
        }
      });
    } else {
      // If the form is invalid, mark all fields as touched to display error messages
      this.eventForm.markAllAsTouched();
    }
  }
  prepareHallplan(eventDate: any): Promise<PersistedHallplan>{
    if(eventDate.room.id){
      return new Promise((resolve, reject) => {
        this.hallplanService.createHallplanSnapshot(eventDate.room.id).subscribe({
          next: data => {
            eventDate.room = data.id;
            resolve(data);
          },
          error: error => {
            console.error('Error fetching hallplan', error);
            this.notification.error(`Could not fetch this hallplan. Errorcode: ${error.status}, Errortext: ${error.error.errors}`);
            reject(error);
          }
        });
      });
    }
  }

  formatHallPlanName(hallplan: AbbreviatedHallplan | null | undefined): string {
    return (hallplan == null)
      ? ''
      : `${hallplan.name}`;
  }
}
