import { Component, inject, signal } from '@angular/core';
import { AdminService } from '../../../core/admin.service';
import { AdminUsersResponse, PROFILE_LABELS, UserResponse } from '../../../core/models';

@Component({
  selector: 'app-admin-usuarios',
  imports: [],
  templateUrl: './admin-usuarios.html',
})
export class AdminUsuarios {
  private readonly adminService = inject(AdminService);

  readonly result = signal<AdminUsersResponse | null>(null);

  constructor() {
    this.adminService.listUsers().subscribe((res) => {
      if (res.success && res.data) this.result.set(res.data);
    });
  }

  initials(user: UserResponse): string {
    return user.fullName
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((n) => n[0]?.toUpperCase())
      .join('');
  }

  profileLabel(user: UserResponse): string {
    return PROFILE_LABELS[user.profile];
  }
}
