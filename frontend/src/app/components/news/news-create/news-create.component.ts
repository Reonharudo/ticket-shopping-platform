import {Component, OnInit} from '@angular/core';
import {FormBuilder, NgForm} from '@angular/forms';
import {NewsService} from '../../../services/news/news.service';
import {News} from '../../../dtos/news';
import {Observable, of} from 'rxjs';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {EventService} from '../../../services/event.service';
import {Event} from '../../../dtos/event';


@Component({
  selector: 'app-news-create-component',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {

  news: News = {
    title: '',
    shortText: '',
    fullText: '',
    coverImage: null,
    images: [],
    eventId: null
  };
  selectedFiles: File[];
  selectedEvent?: Event;

  constructor(private fb: FormBuilder,
              private service: NewsService,
              private eventService: EventService,
              private notification: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  /**
   * Load cover image as a base64 string and store it in the news
   */
  onCoverImageSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.news.coverImage = reader.result as string;
      };
    }
  }

  /**
   * Load images as base64 strings and store them in the news
   */
  onAdditionalImagesSelected(event: any) {
    this.selectedFiles = event.target.files;
    for (const file of this.selectedFiles) {
      this.convertToBase64AndAddToImages(file);
    }
  }

  /**
   * Validates form with news entry and persists it in the backend
   *
   * @param form The form with the data of the news entry
   */
  public onSubmit(form: NgForm): void {

    if (this.selectedEvent) {
      this.news.eventId = this.selectedEvent.id;
    } else {
      this.news.eventId = null;
    }

    console.log('is form valid?', form.valid, this.news);

    if (form.valid) {

      if (this.news.title.trim().length === 0) {
        this.notification.warning('The title is blank', 'The form is not valid');
        return;
      }

      if (this.news.shortText.trim().length === 0) {
        this.notification.warning('The short description is blank', 'The form is not valid');
        return;
      }

      const observable: Observable<News> = this.service.create(this.news);

      observable.subscribe({
        next: () => {
          this.notification.success(`News ${this.news.title} successfully created.`);
          this.router.navigate(['/news']);
        },
        error: error => {
          console.error(`Error creating news`, error, this.news);
          const errorMessage = error.status === 0
            ? 'No connection to server'
            : error.message.message;
          this.notification.error(errorMessage, `Could not create news`);
        }
      });
    } else {
      this.notification.warning('Please fill out the required fields', 'The form is not valid');
    }
  }

  /**
   * Set cover image to null
   */
  public removeCoverImage() {
    this.news.coverImage = null;
  }

  /**
   * Remove image with index 'i' from list. The given index has to be a
   * valid index in the list, otherwise, an error is displayed.
   *
   * @param i The index of the image to remove
   */
  public removeImage(i: number) {
    if (i < this.news.images.length) {
      this.news.images.splice(i,1);
    } else {
      this.notification.error('The index of the image could not be found in the given list of images',
        'The specified image could not be removed');
    }
  }

  public formatEventTitle(event: Event | null | undefined): string {
    return (event == null)
      ? ''
      : `${event.title}`;
  }

  eventSuggestions = (input: string): Observable<string[]> => (input === '')
    ? of([])
    : this.eventService.searchEventByName(input, 5);

  private convertToBase64AndAddToImages(file: File) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      const base64String = reader.result as string;
      this.news.images.push(base64String);
    };
    reader.onerror = (error) => {
      this.notification.error('File could not be converted to base64 format', 'Could not process Image');
      console.error('Error converting file to base64:', error);
    };
  }
}

