import {Component, OnInit} from '@angular/core';
import {NewsService} from '../../../services/news/news.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {News} from '../../../dtos/news';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit {
  news: News = {
    title: 'no title',
    fullText: '',
    coverImage: null,
    images: [],
    shortText: null
  };
  selectedIndex = 0;

  constructor(
    private service: NewsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private authService: AuthService,
  ) {
  }

  /**
   * Loads the data for the selected news entry from the resolver and sets the cover image as the first image in the carousel
   */
  ngOnInit(): void {
    this.route.data.subscribe({
      next: data => {
      if (data.news != null) {
        this.news = data.news;
        if (this.news.coverImage != null) {
          this.news.images.unshift(this.news.coverImage);
        }
      }
    }
  });
  }

  /**
   * Sets the currently shown image in the carousel to the next image in the list. If the current image is the last image
   * in the list, the first image (= cover image) is shown next.
   */
  nextImage(): void {
    this.selectedIndex = (++this.selectedIndex) % this.news.images.length;
  }

  /**
   * Sets the currently shown image in the carousel to the previous image in the list. If the current image is the first image
   * in the list (= cover image), the last image is shown next.
   */
  previousImage(): void {
    const listLength = this.news.images.length;
    this.selectedIndex = (((--this.selectedIndex) % listLength) + listLength) % listLength;
  }

  /**
   * Sets the currently shown image in the carousel to image in the list with index 'index'. The given index has to be a
   * valid index in the list, otherwise, an error is displayed.
   *
   * @param index the index of the image in the list, which should be displayed in the carousel
   */
  setImage(index: number): void {
    if (index < this.news.images.length) {
      this.selectedIndex = index;
    } else {
      this.notification.error('The index of the image could not be found in the given list of images',
        'The specified image could not be loaded');
    }
  }

  /**
   * Formats the date/time of creation of a given news entry as a date string and leaves out the time
   */
  dateOfCreationAsLocaleDate(): string {
    return new Date(this.news.createdAt).toLocaleDateString();
  }

  /**
   * Formats the date/time of creation of a given news entry as a time string and leaves out the date
   */
  timeOfCreationAsLocaleTime(): string {
    return new Date(this.news.createdAt).toLocaleTimeString();
  }

  /**
   * @return Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * Deletes the currently displayed news entry and all its images from the backend
   */
  public onDelete(): void {
    const observable = this.service.deleteById(this.news.id);
    observable.subscribe({
      next: () => {
        this.notification.success(`News ${this.news.title} successfully deleted.`);
        this.router.navigate(['/news']);
      },
      error: error => {
        console.error(`Error deleting news`, error);
        const errorMessage = error.status === 0
          ? 'No connection to server'
          : error.message.message;
        this.notification.error(errorMessage, `Could not delete news`);
      }
    });
  }
}
