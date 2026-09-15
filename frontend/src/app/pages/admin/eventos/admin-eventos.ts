import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BottomSheet } from '../../../shared/bottom-sheet/bottom-sheet';
import { EventsService } from '../../../core/events.service';
import { EventResponse, PROFILE_LABELS, Profile, UserResponse, initialsOf } from '../../../core/models';

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
  readonly saving = signal(false);
  readonly saveError = signal<string | null>(null);
  readonly notifyingId = signal<string | null>(null);
  readonly notifiedId = signal<string | null>(null);

  private static readonly ATTENDEES_PAGE_SIZE = 20;

  readonly attendeesEvent = signal<EventResponse | null>(null);
  readonly attendees = signal<UserResponse[] | null>(null);
  readonly attendeesLoading = signal(false);
  readonly attendeesPage = signal(0);
  readonly attendeesTotalPages = signal(1);

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
    this.saveError.set(null);
    this.title = '';
    this.startsAt = '';
    this.place = '';
    this.description = '';
    this.targetProfile = null;
    this.showModal.set(true);
  }

  openEdit(ev: EventResponse): void {
    this.editingId.set(ev.id);
    this.saveError.set(null);
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
    this.saveError.set(null);
    this.saving.set(true);
    request.subscribe({
      next: (res) => {
        this.saving.set(false);
        if (res.success) {
          this.showModal.set(false);
          this.load();
        } else {
          this.saveError.set(res.error ?? 'No pudimos guardar el evento.');
        }
      },
      error: () => {
        this.saving.set(false);
        this.saveError.set('No pudimos guardar el evento.');
      },
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

  openAttendees(ev: EventResponse): void {
    this.attendeesEvent.set(ev);
    this.attendees.set(null);
    this.attendeesPage.set(0);
    this.attendeesTotalPages.set(1);
    this.loadAttendees(ev.id, 0);
  }

  closeAttendees(): void {
    this.attendeesEvent.set(null);
  }

  attendeesPrevPage(): void {
    const ev = this.attendeesEvent();
    if (ev) this.loadAttendees(ev.id, this.attendeesPage() - 1);
  }

  attendeesNextPage(): void {
    const ev = this.attendeesEvent();
    if (ev) this.loadAttendees(ev.id, this.attendeesPage() + 1);
  }

  private loadAttendees(eventId: string, page: number): void {
    this.attendeesLoading.set(true);
    this.eventsService.attendees(eventId, page, AdminEventos.ATTENDEES_PAGE_SIZE).subscribe({
      next: (res) => {
        this.attendeesLoading.set(false);
        if (res.success && res.data) {
          this.attendees.set(res.data.content);
          this.attendeesPage.set(res.data.page);
          this.attendeesTotalPages.set(res.data.totalPages);
        }
      },
      error: () => this.attendeesLoading.set(false),
    });
  }

  initials(user: UserResponse): string {
    return initialsOf(user.fullName);
  }

  profileLabel(user: UserResponse): string {
    return PROFILE_LABELS[user.profile];
  }
}
