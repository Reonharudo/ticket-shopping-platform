import { Injectable } from '@angular/core';
import {
  Resolve,
  ActivatedRouteSnapshot, Router
} from '@angular/router';
import { catchError, EMPTY, Observable} from 'rxjs';
import { News } from '../../dtos/news';
import { NewsService } from '../../services/news/news.service';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NewsResolver implements Resolve<News> {
  constructor(private newsService: NewsService,
              private authService: AuthService,
              private router: Router,
              private notification: ToastrService) {
  }

  /**
   * Loads the data for the news entry with the id 'id' specified as route parameter from the backend and adds user who
   * reads the news to relation readByUsers
   *
   * @return Observable of the fetched news entry
   */
  resolve(route: ActivatedRouteSnapshot): Observable<News> {
    console.log('Resolve Route');
    const news = this.newsService.getById(route.params?.id).pipe(
      catchError(error => {
        console.error(`Error loading news`, error);
        const errorMessage = error.status === 0
          ? 'No connection to server'
          : 'Maybe the requested news does not exist?';
        this.notification.error(errorMessage, `Could not load news`);
        this.router.navigate(['news']);
        return EMPTY;
      })
    );
    const observable = this.newsService.putNewsRead(route.params?.id);
    observable.subscribe({
      next: () => {},
      error: error => {
        console.error(`Error creating relation`, error);
        const errorMessage = error.status === 0
          ? 'No connection to server'
          : error.message.message;
        this.notification.error(errorMessage, `Could not create relation`);
      }
    });
    console.log(news);
    return news;
  }
}
