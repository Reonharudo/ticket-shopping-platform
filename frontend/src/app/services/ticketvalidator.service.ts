import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Globals } from '../global/globals';
import { Hallplan, PersistedHallplan, PersistedSeat, PersistedSeatRow, Seat, SeatRow } from 'src/app/dtos/hallplan/hallplan';
import { PersistedSection, Section } from 'src/app/dtos/hallplan/section';
import { Observable } from 'rxjs';
import { TicketPayload } from '../dtos/ticketPayload';
import { TicketSeat } from '../dtos/ticket';



@Injectable({
    providedIn: 'root'
})
export class TicketValidatorService {

    private baseUrl: string = this.globals.backendUri + '/ticket-validator';

    constructor(private http: HttpClient, private globals: Globals) {
    }

   getTicketPayload(ticket: TicketSeat): Observable<TicketPayload> {

        return this.http.post<TicketPayload>(
            `${this.baseUrl}`, ticket
        );
    }

    validatePayload(payload: string): Observable<TicketPayload> {
        return this.http.get<TicketPayload>(
            `${this.baseUrl}/validate?message=${payload}`
        );
    }
}
