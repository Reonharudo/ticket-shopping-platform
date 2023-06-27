import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../../global/globals';
import {AbbreviatedNews, News} from '../../dtos/news';


@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private newsBaseUri: string = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }


  /**
   * Persists news with images to the backend
   *
   * @param news to persist
   * @return an Observable for the created news
   */
  create(news: News): Observable<News> {
    return this.httpClient.post<News>(
      this.newsBaseUri,
      news,
    );
  }


  /**
   * Loads a news-page from the backend
   *
   * @param pageIndex index of the page that should be fetched
   * @param loadAlreadyRead determines if news should get loaded that the user has already read or news that he has not read before
   * @return an Observable for the fetched page of news entries
   */
  getPage(pageIndex: number, loadAlreadyRead: boolean): Observable<AbbreviatedNews[]> {
    let params: HttpParams = new HttpParams();
    params = params.set('pageIndex', pageIndex);
    params = params.set('loadAlreadyRead', loadAlreadyRead);
    return this.httpClient.get<AbbreviatedNews[]>(this.newsBaseUri, {params});
  }


  /**
   * Fetch the news with the id "id" stored in the system and add relation news_read between user and news entry
   *
   * @param id The id of the news to fetch
   * @return observable of found news.
   */
  getById(id: number): Observable<News> {
    return this.httpClient.get<News>(this.newsBaseUri + `/${id}`);
  }

  /**
   * Delete the news with the id "id" stored in the system
   *
   * @param id The id of the news to delete
   * @return Not Found Exception if news with id "id" was not found
   */
  deleteById(id: number): Observable<any> {
    return this.httpClient.delete<News>(this.newsBaseUri + `/${id}`);
  }

  /**
   * Create a relation "news_read" between the user and the news with the id "id" if it hasn't existed already
   *
   * @param id The id of the news that was read
   * @return Not Found Exception if news with id "id" was not found
   */
  putNewsRead(id: number): Observable<any> {
    return this.httpClient.put<News>(this.newsBaseUri + `/${id}`, null);
  }
}
