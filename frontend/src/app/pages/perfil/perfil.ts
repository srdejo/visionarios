import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ConfirmModal } from '../../shared/confirm-modal/confirm-modal';
import {
  BUSINESS_CATEGORY_LABELS,
  BusinessCategory,
  PROFILE_LABELS,
  UpdateProfileRequest,
  ageFrom,
  initialsOf,
} from '../../core/models';

@Component({
  selector: 'app-perfil',
  imports: [FormsModule, ConfirmModal],
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

  readonly initials = computed(() => initialsOf(this.user()?.fullName));

  readonly memberSince = computed(() => {
    const created = this.user()?.createdAt;
    return created ? new Date(created).toLocaleDateString('es-ES', { month: 'long', year: 'numeric' }) : '';
  });

  readonly businessCategoryOptions: { value: BusinessCategory; label: string }[] = (
    Object.keys(BUSINESS_CATEGORY_LABELS) as BusinessCategory[]
  ).map((value) => ({ value, label: BUSINESS_CATEGORY_LABELS[value] }));

  readonly saving = signal(false);
  readonly saveError = signal<string | null>(null);
  readonly saved = signal(false);

  fullName = this.user()?.fullName ?? '';
  phone = this.user()?.phone ?? '';
  birthDate = this.user()?.birthDate ?? '';
  profession = this.user()?.profession ?? '';
  yearsExperience: number | null = this.user()?.yearsExperience ?? null;
  currentCompany = this.user()?.currentCompany ?? '';
  businessProduct = this.user()?.businessProduct ?? '';
  operatingTime = this.user()?.operatingTime ?? '';
  companyName = this.user()?.companyName ?? '';
  employeeCount: number | null = this.user()?.employeeCount ?? null;
  yearsWithCompany: number | null = this.user()?.yearsWithCompany ?? null;
  businessCategory: BusinessCategory | null = this.user()?.businessCategory ?? null;

  save(): void {
    const profile = this.user()?.profile;
    if (!profile) return;

    const payload: UpdateProfileRequest = {
      fullName: this.fullName,
      phone: this.phone,
      birthDate: this.birthDate || undefined,
    };

    if (profile === 'PROFESIONAL') {
      payload.profession = this.profession;
      payload.yearsExperience = this.yearsExperience ?? undefined;
      payload.currentCompany = this.currentCompany;
    } else if (profile === 'EMPRENDEDOR') {
      payload.businessProduct = this.businessProduct;
      payload.operatingTime = this.operatingTime;
      payload.businessCategory = this.businessCategory ?? undefined;
    } else if (profile === 'EMPRESARIO') {
      payload.companyName = this.companyName;
      payload.employeeCount = this.employeeCount ?? undefined;
      payload.yearsWithCompany = this.yearsWithCompany ?? undefined;
      payload.businessCategory = this.businessCategory ?? undefined;
    }

    this.saveError.set(null);
    this.saved.set(false);
    this.saving.set(true);
    this.auth.updateProfile(payload).subscribe({
      next: (res) => {
        this.saving.set(false);
        if (res.success) {
          this.saved.set(true);
        } else {
          this.saveError.set(res.error ?? 'No pudimos guardar los cambios.');
        }
      },
      error: () => {
        this.saving.set(false);
        this.saveError.set('No pudimos guardar los cambios.');
      },
    });
  }

  readonly showLogoutConfirm = signal(false);

  logout(): void {
    this.auth.logout();
    this.showLogoutConfirm.set(false);
    this.router.navigateByUrl('/login');
  }
}
