import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../services/auth.service';
import {NewsService} from '../../../services/news/news.service';
import {AbbreviatedNews} from '../../../dtos/news';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-news',
  templateUrl: './news-overview.component.html',
  styleUrls: ['./news-overview.component.scss']
})
export class NewsOverviewComponent implements OnInit {
  pageIndex = 0;
  distance = 1;
  throttle = 2; // ms
  showAlreadyReadNews = false;
  news: AbbreviatedNews[] = [];

  constructor(private authService: AuthService,
              private newsService: NewsService,
              private notification: ToastrService) {}

  ngOnInit() {
    this.initializeLoadedPages();
  }

  /**
   * Loads the first page of news from the backend and deletes all other pages from frontend
   */
  initializeLoadedPages() {
    this.pageIndex = 0;
    this.newsService.getPage(0, this.showAlreadyReadNews).subscribe({
      next: (news: AbbreviatedNews[]) => {
        this.news = news;
      },
      error: error => {
        console.error('Error fetching news entries', error);
        const errorMessage = error.status === 0
          ? 'No connection to server'
          : error.message.message;
        this.notification.error(errorMessage, 'Could not fetch news entries');
      },
    });
  }

  /**
   * @return Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * Loads the next page of news from the backend
   */
  onScroll(): void {
    this.newsService.getPage(++this.pageIndex, this.showAlreadyReadNews)
      .subscribe((news: AbbreviatedNews[]) => {
      console.log('GET page ' + this.pageIndex);
      this.news.push(...news);
    });
  }

  /**
   * Formats the date/time of creation of a given news entry as a date string and leaves out the time
   */
  dateOfCreationAsLocaleDate(entry: AbbreviatedNews): string {
    return new Date(entry.createdAt).toLocaleDateString();
  }

  /**
   * Formats the date/time of creation of a given news entry as a time string and leaves out the date
   */
  timeOfCreationAsLocaleTime(entry: AbbreviatedNews): string {
    return new Date(entry.createdAt).toLocaleTimeString();
  }

  /**
   * Toggles if already read or not read news are shown and reloads all news entries
   */
  toggleMode(): void {
    this.showAlreadyReadNews = !this.showAlreadyReadNews;
    this.initializeLoadedPages();
  }
}
