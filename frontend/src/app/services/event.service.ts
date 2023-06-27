import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';

const baseUri = environment.backendUrl + '/api/v1/events';
import {Event, Performance} from 'src/app/dtos/event';
import {AbbreviatedEvent} from '../dtos/abbreviatedEvents';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  constructor(
    private http: HttpClient,
  ) {
  }

  /**
   * Create a new event in the system.
   *
   * @param event the data for the horse that should be created
   * @param token the token that is needed for authentication
   * @return an Observable for the created horse
   */
  create(event: Event): Observable<Event> {
    console.log(event);
    return this.http.post<Event>(
      baseUri,
      event
    );
  }

  /**
   * Get all events stored in the system.
   *
   * @return observable list of found events.
   */
  getAll(): Observable<any> {
    return this.http.get<any>(baseUri);
  }

  /**
   * Loads an event-page from the backend
   *
   * @param pageIndex index of the page that should be fetched
   * @return an Observable for the fetched page of event entries
   */
  getPage(pageIndex: number, fromDate: string, toDate: string, artist: string, location: string,
          titleCategory: string, startTime: any, duration: any ): Observable<AbbreviatedEvent[]> {
    let params: HttpParams = new HttpParams();
    params = params.set('pageIndex', pageIndex);
    if (fromDate != null) {
      params = params.set('fromDate', fromDate);
    }
    if (toDate != null) {
      params = params.set('untilDate', toDate);
    }
    if (artist !== '') {
      params = params.set('artist', artist);
    }
    if (location !== '') {
      params = params.set('location', location);
    }
    if(titleCategory !== ''){
      params = params.set('titleCategory', titleCategory);
    }
    if(startTime !== null){
      params = params.set('startTime', startTime);
    }
    if(duration !== null){
      params = params.set('duration', duration);
    }
    return this.http.get<AbbreviatedEvent[]>(baseUri, {params});
  }

  /**
   * Get all events stored in the system.
   *
   * @return observable list of found events.
   */
  getById(id: number): Observable<Event> {
    let params: HttpParams = new HttpParams();
    params = params.set('id', id);
    return this.http.get<Event>(baseUri + '/byId', {params});
  }

  /**
   * Get a Performance by HallplanId.
   *
   * @return performance.
   */
  getPerformance(hallplanId: number): Observable<Performance> {
    return this.http.get<any>(baseUri + '/performance/' + hallplanId);
  }

  /**
   * Get top 10 events.
   *
   * @return performance.
   */
  getTopEvents(): Observable<Event[]> {
    return this.http.get<any>(baseUri + '/topEvents');
  }
  /**
   * Get {@code number} Events by title.
   *
   * @param searchString the partial string that should be matched
   * @param number the maximum number of returned events
   *
   * @return Observable of event list
   */
  searchEventByName(searchString: string, number: number): Observable<Event[]> {
    let params: HttpParams = new HttpParams();
    params = params.set('searchString', searchString);
    params = params.set('number', number);
    return this.http.get<Event[]>(
      baseUri + '/search',
      {params}
    );
  }
}
