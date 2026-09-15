import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { BUSINESS_CATEGORY_LABELS, BusinessCategory, PROFILE_LABELS, Profile, RegisterRequest } from '../../../core/models';
import { AuthHero } from '../../../shared/auth-hero/auth-hero';
import { PasswordInput } from '../../../shared/password-input/password-input';

interface ProfileCard {
  value: Profile;
  label: string;
  description: string;
}

interface BusinessCategoryOption {
  value: BusinessCategory;
  label: string;
}

@Component({
  selector: 'app-register',
  imports: [FormsModule, AuthHero, PasswordInput],
  templateUrl: './register.html',
})
export class Register {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly profileCards: ProfileCard[] = [
    { value: 'PROFESIONAL', label: 'Profesional', description: 'Trabajas para una empresa o buscas empleo.' },
    { value: 'EMPRENDEDOR', label: 'Emprendedor', description: 'Ya tienes un producto o negocio en marcha.' },
    { value: 'EMPRESARIO', label: 'Empresario', description: 'Tienes una o más empresas y personas a cargo.' },
  ];

  readonly businessCategoryOptions: BusinessCategoryOption[] = (
    Object.keys(BUSINESS_CATEGORY_LABELS) as BusinessCategory[]
  ).map((value) => ({ value, label: BUSINESS_CATEGORY_LABELS[value] }));

  readonly step = signal(1);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  fullName = '';
  phone = '';
  email = '';
  password = '';
  confirmPassword = '';
  birthDate = '';

  selectedProfile = signal<Profile | null>(null);

  profession = '';
  yearsExperience: number | null = null;
  currentCompany = '';
  readonly searchingJob = signal(false);
  businessProduct = '';
  operatingTime = '';
  companyName = '';
  employeeCount: number | null = null;
  yearsWithCompany: number | null = null;
  businessCategory: BusinessCategory | null = null;

  readonly profileLabel = computed(() => {
    const p = this.selectedProfile();
    return p ? PROFILE_LABELS[p] : '';
  });

  back(): void {
    if (this.step() > 1) {
      this.step.update((s) => s - 1);
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  goStep1to2(): void {
    if (this.password !== this.confirmPassword) {
      this.error.set('Las contraseñas no coinciden');
      return;
    }
    this.error.set(null);
    this.step.set(2);
  }

  selectProfile(p: Profile): void {
    this.selectedProfile.set(p);
  }

  setSearchingJob(value: boolean): void {
    this.searchingJob.set(value);
    if (value) {
      this.currentCompany = '';
    }
  }

  goStep2to3(): void {
    if (this.selectedProfile()) {
      this.step.set(3);
    }
  }

  submitRegistration(): void {
    const profile = this.selectedProfile();
    if (!profile) return;

    if (this.password !== this.confirmPassword) {
      this.error.set('Las contraseñas no coinciden');
      return;
    }

    const payload: RegisterRequest = {
      fullName: this.fullName,
      phone: this.phone,
      email: this.email,
      password: this.password,
      confirmPassword: this.confirmPassword,
      profile,
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

    this.error.set(null);
    this.loading.set(true);
    this.auth.register(payload).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.step.set(4);
        } else {
          this.error.set(res.error ?? 'No pudimos crear tu cuenta.');
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.error ?? 'No pudimos crear tu cuenta.');
      },
    });
  }

  enterNetwork(): void {
    this.router.navigateByUrl('/login');
  }
}
