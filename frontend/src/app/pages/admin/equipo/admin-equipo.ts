import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/admin.service';
import { AuthService } from '../../../core/auth.service';
import { AdminInviteResponse, UserResponse } from '../../../core/models';

@Component({
  selector: 'app-admin-equipo',
  imports: [FormsModule],
  templateUrl: './admin-equipo.html',
})
export class AdminEquipo {
  private readonly adminService = inject(AdminService);
  private readonly auth = inject(AuthService);

  readonly currentUser = this.auth.currentUser;
  readonly admins = signal<UserResponse[]>([]);
  readonly invites = signal<AdminInviteResponse[]>([]);
  readonly inviteEmail = '';
  readonly lastInviteLink = signal<string | null>(null);
  readonly copied = signal(false);

  email = '';

  constructor() {
    this.load();
  }

  load(): void {
    this.adminService.listUsers().subscribe((res) => {
      if (res.success && res.data) this.admins.set(res.data.users.filter((u) => u.role === 'ADMIN'));
    });
    this.adminService.listInvites().subscribe((res) => {
      if (res.success && res.data) this.invites.set(res.data);
    });
  }

  sendInvite(): void {
    if (!this.email) return;
    this.adminService.createInvite(this.email).subscribe((res) => {
      if (res.success && res.data) {
        this.lastInviteLink.set(`${window.location.origin}/aceptar-invitacion/${res.data.token}`);
        this.copied.set(false);
        this.email = '';
        this.load();
      }
    });
  }

  copyLink(): void {
    const link = this.lastInviteLink();
    if (!link) return;
    navigator.clipboard?.writeText(link).then(() => {
      this.copied.set(true);
    });
  }

  inviteStatus(inv: AdminInviteResponse): string {
    if (inv.used) return 'Activa';
    if (inv.expired) return 'Expirada';
    return 'Pendiente';
  }

  initials(user: UserResponse): string {
    return user.fullName
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((n) => n[0]?.toUpperCase())
      .join('');
  }
}
