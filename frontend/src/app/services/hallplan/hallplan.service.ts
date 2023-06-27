import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Globals } from '../../global/globals';
import { Hallplan, PersistedHallplan, PersistedSeat, PersistedSeatRow, Seat, SeatRow } from 'src/app/dtos/hallplan/hallplan';
import {DetailedPersistedSection, PersistedSection, Section} from 'src/app/dtos/hallplan/section';
import { Observable } from 'rxjs';
import { AbbreviatedHallplan } from '../../dtos/hallplan/abbreviatedHallplan';


@Injectable({
    providedIn: 'root'
})
export class HallplanService {

    private baseUrl: string = this.globals.backendUri + '/hallplans';

    constructor(private http: HttpClient, private globals: Globals) {
    }

    getAllHallplans() {
        return this.http.get<Hallplan[]>(
            `${this.baseUrl}`
        );
    }

    searchHallplans(name: string, description: string, isTemplate: boolean) {
        return this.http.get<PersistedHallplan[]>(
            `${this.baseUrl}?name=${name}&description=${description}&isTemplate=${isTemplate}`
        );
    }

    deleteHallPlan(id: number) {
        return this.http.delete<void>(
            `${this.baseUrl}/${id}`
        );
    }

    createHallplan(hallplan: Hallplan) {
        console.log(hallplan);
        return this.http.post<Hallplan>(
            `${this.baseUrl}`, hallplan
        );
    }

    getHallplanById(id: number) {
        return this.http.get<PersistedHallplan>(
            `${this.baseUrl}/${id}`
        );
    }

    createSeatrow(hallplanId: number, seatrow: SeatRow) {
        return this.http.post<PersistedSeatRow>(
            `${this.baseUrl}/${hallplanId}/seatrows`,
            seatrow
        );
    }

    /*

        --------------------- Seats Endpoints ------------------------------

    */

    createSeatsBulk(hallplanId: number, seatrowId: number, seats: Seat[]) {
        return this.http.post<PersistedSeatRow>(
            `${this.baseUrl}/${hallplanId}/seatrows/${seatrowId}/seats/bulk`,
            {
                seats
            }
        );
    }

    updateSeatsBulk(hallplanId: number, seats: PersistedSeat[]) {
        return this.http.put<PersistedSeatRow>(
            `${this.baseUrl}/${hallplanId}/seats/bulk`,
            {
                seats
            }
        );
    }

    updateSeat(hallplanId: number, seats: PersistedSeat) {
        return this.http.put<PersistedSeatRow>(
            `${this.baseUrl}/${hallplanId}/seats`,
            seats
        );
    }

    deleteSeat(hallplanId: number, seatrowId: number, seatId: number) {
        return this.http.delete<void>(
            `${this.baseUrl}/${hallplanId}/seatrows/${seatrowId}/seats/${seatId}`
        );
    }

    /*

        --------------------- Section Endpoints ------------------------------

    */

    getAllSections(hallplanId: number) {
        return this.http.get<PersistedSection[]>(
            `${this.baseUrl}/${hallplanId}/sections`,
        );
    }

  getAllSectionsWithCounts(hallplanId: number) {
    return this.http.get<DetailedPersistedSection[]>(
      `${this.baseUrl}/${hallplanId}/sections`,
    );
  }

    createSection(hallplanId: number, section: Section) {
        return this.http.post<PersistedSection>(
            `${this.baseUrl}/${hallplanId}/sections`,
            section
        );
    }

    updateSection(hallplanId: number, sectionId: number, section: Section) {
        return this.http.put<PersistedSection>(
            `${this.baseUrl}/${hallplanId}/sections/${sectionId}`,
            section
        );
    }


    deleteSection(sectionId: number) {
        return this.http.delete<PersistedSection>(
            `${this.baseUrl}/sections/${sectionId}`
        );
    }

    /**
     * Delete seatrow by id (including its seats)
     *
     * @param hallplanId id of hallplan
     * @param seatrowId id of seatrow
     */
    deleteSeatrow(hallplanId: number, seatrowId: number) {
        return this.http.delete<void>(
            `${this.baseUrl}/${hallplanId}/seatrows/${seatrowId}`
        );
    }

  /**
   * Get Roomplans by pages.
   *
   * @param pageIndex index of the searched for page
   * @param search the string that should be matched
   */
  getRoomplans(pageIndex: number, search: string): Observable<AbbreviatedHallplan[]> {
    let params: HttpParams = new HttpParams();
    params = params.set('pageIndex', pageIndex);
    params = params.set('search', search);
    return this.http.get<AbbreviatedHallplan[]>(this.baseUrl+'/search', {params});
  }

  /**
   * Get a AbbreviatedHallplan by its id.
   *
   * @param id the id of the needed hallplan
   */
  getByIdAbbreviated(id: number): Observable<AbbreviatedHallplan> {
    let params: HttpParams = new HttpParams();
    params = params.set('id', id);
    return this.http.get<AbbreviatedHallplan>(this.baseUrl+'/byId', {params});
  }

  /**
   * Create a snapshot of the hallplan with given id.
   *
   * @param id the id of the needed hallplan
   */
  createHallplanSnapshot(id: number) {
    const url = `${this.baseUrl}/${id}/snapshot`;
    const body = JSON.stringify({ isTemplate: false });

    return this.http.post<PersistedHallplan>(url, body, {
      headers: {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        'Content-Type': 'application/json',
      },
    });
  }
}
