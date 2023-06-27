import { Seat } from './hallplan';

export const RESERVED_DEFAULT_SECTION_NAME = 'Unassigned';

export interface Section{
    name: string;
    color: string;
    price: number;
}

export interface DetailedSection{
    name: string;
    color: string;
    price: number;
    seat: Seat;
}

export interface PersistedSection extends Section{
    id: number;
}

export interface DetailedPersistedSection extends PersistedSection{
    count: number;
}
