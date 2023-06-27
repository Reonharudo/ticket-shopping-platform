
/*
    Section
*/

import { PersistedSection, Section } from './section';

export interface Hallplan {
    name: string;
    description: string;
    isTemplate: boolean;
    seatRows: SeatRow[];
}

export interface PersistedHallplan extends Hallplan {
    id: number;
    seatRows: PersistedSeatRow[];
}

/*
    Seat Row
*/

export interface SeatRow {
    rowNr: number;
    seats: Seat[];
}

export interface PersistedSeatRow extends SeatRow {
    id: number;
    seats: PersistedSeat[];
}

/*
    Seat
*/

export interface Seat {
    type: SeatType;
    seatNr: number;
    status: SeatStatus;
    section: Section;
    capacity: number;
    orderNr: number;
    boughtNr: number;
    reservedNr: number;
}

export interface PersistedSeat extends Seat {
    id: number;
    section: PersistedSection;
}

export interface PersistedSeatWithRowNr extends PersistedSeat {
    rowNr: number;
}

/*
    Enum
*/

export enum SeatStatus {
    reserved = 'RESERVED',
    occupied = 'OCCUPIED',
    free = 'FREE'
}

export enum SeatType {
    seat = 'SEAT',
    vacantSeat = 'VACANT_SEAT',
    standingSeat = 'STANDING_SEAT'
}
