import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { PROFILE_LABELS } from '../../core/models';

@Component({
  selector: 'app-perfil',
  imports: [],
  templateUrl: './perfil.html',
})
export class Perfil {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly user = this.auth.currentUser;
  readonly profileLabel = computed(() => {
    const p = this.user()?.profile;
    return p ? PROFILE_LABELS[p] : '';
  });

  readonly initials = computed(() => {
    const name = this.user()?.fullName ?? '';
    return name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((n) => n[0]?.toUpperCase())
      .join('');
  });

  readonly memberSince = computed(() => {
    const created = this.user()?.createdAt;
    return created ? new Date(created).toLocaleDateString('es-ES', { month: 'long', year: 'numeric' }) : '';
  });

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
