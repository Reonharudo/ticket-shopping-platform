import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CartItem} from '../dtos/cartItem';
import {CreatePurchase} from '../dtos/purchases';
import {PersistedSeat} from '../dtos/hallplan/hallplan';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartBaseUri: string = this.globals.backendUri + '/cart';
  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getCart(): Observable<CartItem[]> {
    return this.httpClient.get<CartItem[]>(
      this.cartBaseUri
    );
  }
  deleteCartItemById(id: number): Observable<HttpResponse<any>> {
    return this.httpClient.delete<any>(this.cartBaseUri + '/' + id);
  }

  addToCart(seatList: PersistedSeat[]): Observable<HttpResponse<any>>{
    return this.httpClient.post<any>(this.cartBaseUri, seatList);
  }
  purchaseCart(purchaseItem: CreatePurchase): Observable<HttpResponse<any>>{
    return this.httpClient.post<any>(this.cartBaseUri + '/purchase', purchaseItem);
  }

}
