import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';
import { AuthService } from '../../core/auth.service';
import { initialsOf, PROFILE_LABELS } from '../../core/models';
import { ConfirmModal } from '../confirm-modal/confirm-modal';

interface NavItem {
  path: string;
  icon: string;
  label: string;
  activeMatch?: string[];
}

const USER_NAV: NavItem[] = [
  { path: '/home', icon: 'fa-house', label: 'Inicio' },
  { path: '/biblioteca', icon: 'fa-book', label: 'Material' },
  { path: '/agenda', icon: 'fa-calendar-days', label: 'Agenda', activeMatch: ['/eventos'] },
  { path: '/perfil', icon: 'fa-user', label: 'Perfil' },
];

const ADMIN_NAV: NavItem[] = [
  { path: '/admin/eventos', icon: 'fa-calendar-days', label: 'Eventos' },
  { path: '/admin/material', icon: 'fa-book', label: 'Material' },
  { path: '/admin/usuarios', icon: 'fa-users', label: 'Usuarios' },
  { path: '/admin/equipo', icon: 'fa-user-shield', label: 'Equipo' },
  { path: '/perfil', icon: 'fa-user', label: 'Perfil' },
];

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, ConfirmModal],
  templateUrl: './sidebar.html',
})
export class Sidebar {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly showLogoutConfirm = signal(false);

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map((e) => e.urlAfterRedirects),
      startWith(this.router.url),
    ),
    { initialValue: this.router.url },
  );

  readonly user = this.auth.currentUser;
  readonly isAdmin = computed(() => this.auth.isAdmin());
  readonly nav = computed(() => (this.isAdmin() ? ADMIN_NAV : USER_NAV));
  readonly initials = computed(() => initialsOf(this.user()?.fullName));
  readonly roleLabel = computed(() => {
    if (this.isAdmin()) return 'Administrador';
    const p = this.user()?.profile;
    return p ? PROFILE_LABELS[p] : '';
  });

  isActive(item: NavItem): boolean {
    const u = this.url();
    return u.startsWith(item.path) || (item.activeMatch ?? []).some((p) => u.startsWith(p));
  }

  readonly visible = computed(() => {
    const u = this.url();
    if (!this.auth.isAuthenticated()) return false;
    return !['/login', '/registro', '/olvide-password'].some((p) => u.startsWith(p));
  });

  logout(): void {
    this.auth.logout();
    this.showLogoutConfirm.set(false);
    this.router.navigateByUrl('/login');
  }
}
