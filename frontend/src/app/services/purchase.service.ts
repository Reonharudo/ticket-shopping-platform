import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Purchase} from '../dtos/purchases';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {
  private purchaseBaseUri: string = this.globals.backendUri + '/purchase';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getPurchases(): Observable<Purchase[]> {
    return this.httpClient.get<Purchase[]>(
      this.purchaseBaseUri
    );
  }

  getPurchase(purchaseNr: number): Observable<Purchase> {
    return this.httpClient.get<Purchase>(
      this.purchaseBaseUri + '/' + purchaseNr
    );
  }

  refundPurchase(purchaseNr: number): Observable<HttpResponse<any>> {
    return this.httpClient.delete<any>(this.purchaseBaseUri + '/' + purchaseNr);
  }


}
