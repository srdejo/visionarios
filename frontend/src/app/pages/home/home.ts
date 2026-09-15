import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { EventsService } from '../../core/events.service';
import { MaterialsService } from '../../core/materials.service';
import { EventResponse, MaterialResponse, PROFILE_LABELS } from '../../core/models';
import { InfoModal } from '../../shared/info-modal/info-modal';

@Component({
  selector: 'app-home',
  imports: [RouterLink, InfoModal],
  templateUrl: './home.html',
})
export class Home {
  private readonly auth = inject(AuthService);
  private readonly eventsService = inject(EventsService);
  private readonly materialsService = inject(MaterialsService);

  readonly user = this.auth.currentUser;
  readonly profileLabel = computed(() => {
    const p = this.user()?.profile;
    return p ? PROFILE_LABELS[p] : '';
  });

  readonly showVerifyReminder = signal(!!this.user() && !this.user()!.emailVerified);
  readonly resendState = signal<'idle' | 'sending' | 'sent' | 'error'>('idle');
  readonly resendMessage = computed(() => {
    switch (this.resendState()) {
      case 'sending':
        return 'Enviando...';
      case 'sent':
        return '¡Listo! Revisa tu correo.';
      case 'error':
        return 'No pudimos reenviarlo, intenta de nuevo.';
      default:
        return 'Te enviamos un enlace de confirmación a tu correo. Aún puedes usar tu cuenta, pero te recomendamos confirmarlo pronto.';
    }
  });

  resendVerification(): void {
    if (this.resendState() === 'sending') return;
    this.resendState.set('sending');
    this.auth.resendVerification().subscribe({
      next: (res) => this.resendState.set(res.success ? 'sent' : 'error'),
      error: () => this.resendState.set('error'),
    });
  }

  dismissVerifyReminder(): void {
    this.showVerifyReminder.set(false);
  }

  readonly events = signal<EventResponse[]>([]);
  readonly materials = signal<MaterialResponse[]>([]);

  readonly nextEvent = computed(() => this.events()[0] ?? null);
  readonly agenda = computed(() => this.events().slice(1));
  readonly featuredMaterials = computed(() => this.materials().slice(0, 2));

  constructor() {
    this.eventsService.list().subscribe((res) => {
      if (res.success && res.data) {
        this.events.set(
          [...res.data].sort((a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime()),
        );
      }
    });
    this.materialsService.list().subscribe((res) => {
      if (res.success && res.data) this.materials.set(res.data);
    });
  }

  daysUntil(iso: string): number {
    const diff = new Date(iso).getTime() - Date.now();
    return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
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
    const days = this.daysUntil(iso);
    return days <= 7;
  }
}
