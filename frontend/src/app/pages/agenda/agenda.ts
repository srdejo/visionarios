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
  readonly sortedEvents = computed(() =>
    [...this.events()].sort((a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime()),
  );

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

  isThisWeek(iso: string): boolean {
    const diff = new Date(iso).getTime() - Date.now();
    const days = Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
    return days <= 7;
  }
}
