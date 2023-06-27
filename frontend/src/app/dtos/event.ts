import {EventDate} from './eventDate';

export interface Event {
  id?: number;
  title: string;
  eventDatesLocation: EventDate[];
  duration: string;
  category: string;
  artist: string;
  description?: string;
  image?: string;
  soldTickets?: number;
}

export interface Performance {
  id?: number;
  title: string;
  performanceDate: EventDate;
  duration: string;
  category: string;
  artist: string;
  description?: string;
  image?: string;
}
