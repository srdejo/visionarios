import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { EventsService } from '../../core/events.service';
import { EventResponse, PROFILE_LABELS } from '../../core/models';

@Component({
  selector: 'app-evento-detalle',
  imports: [],
  templateUrl: './evento-detalle.html',
})
export class EventoDetalle {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventsService = inject(EventsService);
  private readonly auth = inject(AuthService);

  readonly isAdmin = computed(() => this.auth.isAdmin());
  readonly event = signal<EventResponse | null>(null);
  readonly loading = signal(true);
  readonly rsvpLoading = signal(false);

  readonly targetLabel = computed(() => {
    const ev = this.event();
    if (!ev) return '';
    return ev.targetProfile ? PROFILE_LABELS[ev.targetProfile] : 'Toda la red';
  });

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.eventsService.get(id).subscribe((res) => {
        this.loading.set(false);
        if (res.success && res.data) this.event.set(res.data);
      });
    }
  }

  back(): void {
    this.router.navigateByUrl('/home');
  }

  toggleRsvp(): void {
    const ev = this.event();
    if (!ev || this.rsvpLoading()) return;
    this.rsvpLoading.set(true);
    this.eventsService.rsvp(ev.id).subscribe({
      next: (res) => {
        this.rsvpLoading.set(false);
        if (res.success && res.data) this.event.set(res.data);
      },
      error: () => this.rsvpLoading.set(false),
    });
  }

  addToCalendar(): void {
    const ev = this.event();
    if (!ev) return;

    const start = new Date(ev.startsAt);
    const end = new Date(start.getTime() + 2 * 60 * 60 * 1000);
    const toGCalDate = (d: Date) => d.toISOString().replace(/[-:]/g, '').split('.')[0] + 'Z';

    const params = new URLSearchParams({
      action: 'TEMPLATE',
      text: ev.title,
      dates: `${toGCalDate(start)}/${toGCalDate(end)}`,
      details: ev.description,
      location: ev.place,
    });

    window.open(`https://calendar.google.com/calendar/render?${params.toString()}`, '_blank');
  }

  dateLabel(iso: string): string {
    return new Date(iso).toLocaleDateString('es-ES', { day: '2-digit', month: 'long', year: 'numeric' });
  }

  timeLabel(iso: string): string {
    return new Date(iso).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  }
}
