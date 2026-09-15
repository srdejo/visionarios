import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BottomSheet } from '../../../shared/bottom-sheet/bottom-sheet';
import { EventsService } from '../../../core/events.service';
import { EventResponse, PROFILE_LABELS, Profile } from '../../../core/models';

@Component({
  selector: 'app-admin-eventos',
  imports: [FormsModule, BottomSheet],
  templateUrl: './admin-eventos.html',
})
export class AdminEventos {
  private readonly eventsService = inject(EventsService);

  readonly events = signal<EventResponse[]>([]);
  readonly showModal = signal(false);
  readonly editingId = signal<string | null>(null);
  readonly notifyingId = signal<string | null>(null);
  readonly notifiedId = signal<string | null>(null);

  readonly profileOptions: { value: Profile; label: string }[] = [
    { value: 'PROFESIONAL', label: PROFILE_LABELS.PROFESIONAL },
    { value: 'EMPRENDEDOR', label: PROFILE_LABELS.EMPRENDEDOR },
    { value: 'EMPRESARIO', label: PROFILE_LABELS.EMPRESARIO },
  ];

  title = '';
  startsAt = '';
  place = '';
  description = '';
  targetProfile: Profile | null = null;

  constructor() {
    this.load();
  }

  load(): void {
    this.eventsService.list().subscribe((res) => {
      if (res.success && res.data) this.events.set(res.data);
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.title = '';
    this.startsAt = '';
    this.place = '';
    this.description = '';
    this.targetProfile = null;
    this.showModal.set(true);
  }

  openEdit(ev: EventResponse): void {
    this.editingId.set(ev.id);
    this.title = ev.title;
    this.startsAt = ev.startsAt.slice(0, 16);
    this.place = ev.place;
    this.description = ev.description;
    this.targetProfile = ev.targetProfile;
    this.showModal.set(true);
  }

  toggleTarget(p: Profile): void {
    this.targetProfile = this.targetProfile === p ? null : p;
  }

  save(): void {
    const payload = {
      title: this.title,
      startsAt: new Date(this.startsAt).toISOString(),
      place: this.place,
      description: this.description,
      targetProfile: this.targetProfile,
    };
    const id = this.editingId();
    const request = id ? this.eventsService.update(id, payload) : this.eventsService.create(payload);
    request.subscribe((res) => {
      if (res.success) {
        this.showModal.set(false);
        this.load();
      }
    });
  }

  notify(ev: EventResponse): void {
    this.notifyingId.set(ev.id);
    this.notifiedId.set(null);
    this.eventsService.notify(ev.id).subscribe({
      next: () => {
        this.notifyingId.set(null);
        this.notifiedId.set(ev.id);
      },
      error: () => this.notifyingId.set(null),
    });
  }

  dateLabel(iso: string): string {
    return new Date(iso).toLocaleString('es-ES', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  targetLabel(ev: EventResponse): string {
    return ev.targetProfile ? PROFILE_LABELS[ev.targetProfile] : 'Toda la red';
  }
}
