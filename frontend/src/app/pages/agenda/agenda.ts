import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { EventsService } from '../../core/events.service';
import { EventResponse } from '../../core/models';

@Component({
  selector: 'app-agenda',
  imports: [RouterLink],
  templateUrl: './agenda.html',
})
export class Agenda {
  private readonly eventsService = inject(EventsService);

  readonly events = signal<EventResponse[]>([]);
  // Proximos primero (mas cercano arriba); los pasados al final (mas reciente primero).
  readonly sortedEvents = computed(() => {
    const t = (e: EventResponse) => new Date(e.startsAt).getTime();
    const upcoming = this.events().filter((e) => !this.isPast(e.startsAt)).sort((a, b) => t(a) - t(b));
    const past = this.events().filter((e) => this.isPast(e.startsAt)).sort((a, b) => t(b) - t(a));
    return [...upcoming, ...past];
  });

  constructor() {
    this.eventsService.list().subscribe((res) => {
      if (res.success && res.data) this.events.set(res.data);
    });
  }

  day(iso: string): string {
    return new Date(iso).toLocaleDateString('es-ES', { day: '2-digit' });
  }

  month(iso: string): string {
    return new Date(iso).toLocaleDateString('es-ES', { month: 'short' }).toUpperCase();
  }

  time(iso: string): string {
    return new Date(iso).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  }

  isPast(iso: string): boolean {
    return new Date(iso).getTime() < Date.now();
  }

  isThisWeek(iso: string): boolean {
    const diff = new Date(iso).getTime() - Date.now();
    const days = Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
    return days <= 7;
  }
}
