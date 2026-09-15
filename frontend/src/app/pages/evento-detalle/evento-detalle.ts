import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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

  readonly event = signal<EventResponse | null>(null);
  readonly loading = signal(true);

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
    if (!ev) return;
    this.eventsService.rsvp(ev.id).subscribe((res) => {
      if (res.success && res.data) this.event.set(res.data);
    });
  }

  addToCalendar(): void {
    // Placeholder: integración real con calendario queda pendiente (YAGNI para este alcance).
  }

  dateLabel(iso: string): string {
    return new Date(iso).toLocaleDateString('es-ES', { day: '2-digit', month: 'long', year: 'numeric' });
  }

  timeLabel(iso: string): string {
    return new Date(iso).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  }
}
