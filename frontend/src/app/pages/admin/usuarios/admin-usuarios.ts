import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/admin.service';
import { BottomSheet } from '../../../shared/bottom-sheet/bottom-sheet';
import {
  AdminUsersResponse,
  BUSINESS_CATEGORY_LABELS,
  BusinessCategory,
  PROFILE_LABELS,
  Profile,
  UserResponse,
  ageFrom,
  initialsOf,
} from '../../../core/models';

interface Bucket {
  value: string;
  label: string;
  min: number;
  max: number;
}

const AGE_BUCKETS: Bucket[] = [
  { value: '18-25', label: '18-25', min: 18, max: 25 },
  { value: '26-35', label: '26-35', min: 26, max: 35 },
  { value: '36-45', label: '36-45', min: 36, max: 45 },
  { value: '46-60', label: '46-60', min: 46, max: 60 },
  { value: '60+', label: '60+', min: 60, max: Infinity },
];

const EMPLOYEE_BUCKETS: Bucket[] = [
  { value: '1-10', label: '1-10', min: 1, max: 10 },
  { value: '11-50', label: '11-50', min: 11, max: 50 },
  { value: '51-200', label: '51-200', min: 51, max: 200 },
  { value: '200+', label: '200+', min: 200, max: Infinity },
];

const PAGE_SIZE = 20;

@Component({
  selector: 'app-admin-usuarios',
  imports: [FormsModule, BottomSheet],
  templateUrl: './admin-usuarios.html',
})
export class AdminUsuarios {
  private readonly adminService = inject(AdminService);

  readonly result = signal<AdminUsersResponse | null>(null);
  readonly selectedUser = signal<UserResponse | null>(null);

  readonly ageBuckets = AGE_BUCKETS;
  readonly employeeBuckets = EMPLOYEE_BUCKETS;
  readonly profileOptions: { value: Profile; label: string }[] = (
    Object.keys(PROFILE_LABELS) as Profile[]
  ).map((value) => ({ value, label: PROFILE_LABELS[value] }));
  readonly businessCategoryOptions: { value: BusinessCategory; label: string }[] = (
    Object.keys(BUSINESS_CATEGORY_LABELS) as BusinessCategory[]
  ).map((value) => ({ value, label: BUSINESS_CATEGORY_LABELS[value] }));

  search = '';
  readonly searchTerm = signal('');
  readonly profileFilter = signal<Profile | 'ALL'>('ALL');
  readonly categoryFilter = signal<BusinessCategory | 'ALL'>('ALL');
  readonly ageBucketFilter = signal<string | 'ALL'>('ALL');
  readonly employeeBucketFilter = signal<string | 'ALL'>('ALL');
  readonly filtrosOpen = signal(false);
  readonly currentPage = signal(0);

  readonly activeCount = computed(() => {
    let count = 0;
    if (this.profileFilter() !== 'ALL') count++;
    if (this.categoryFilter() !== 'ALL') count++;
    if (this.ageBucketFilter() !== 'ALL') count++;
    if (this.employeeBucketFilter() !== 'ALL') count++;
    return count;
  });

  readonly activeChips = computed(() => {
    const chips: { label: string; onRemove: () => void }[] = [];
    const profile = this.profileFilter();
    const category = this.categoryFilter();
    const ageBucket = this.ageBucketFilter();
    const employeeBucket = this.employeeBucketFilter();
    if (profile !== 'ALL') {
      chips.push({ label: PROFILE_LABELS[profile], onRemove: () => this.setProfileFilter(profile) });
    }
    if (category !== 'ALL') {
      chips.push({ label: BUSINESS_CATEGORY_LABELS[category], onRemove: () => this.setCategoryFilter(category) });
    }
    if (ageBucket !== 'ALL') {
      chips.push({ label: `Edad ${ageBucket}`, onRemove: () => this.setAgeBucketFilter(ageBucket) });
    }
    if (employeeBucket !== 'ALL') {
      chips.push({ label: `Empleados ${employeeBucket}`, onRemove: () => this.setEmployeeBucketFilter(employeeBucket) });
    }
    return chips;
  });

  readonly users = computed(() => this.result()?.users ?? []);
  readonly totalPages = computed(() => this.result()?.totalPages ?? 1);
  readonly totalFiltered = computed(() => this.result()?.totalFiltered ?? 0);

  constructor() {
    this.load();
  }

  private load(): void {
    const ageBucket = this.ageBuckets.find((b) => b.value === this.ageBucketFilter());
    const employeeBucket = this.employeeBuckets.find((b) => b.value === this.employeeBucketFilter());
    const profile = this.profileFilter();
    const category = this.categoryFilter();

    this.adminService
      .listUsers({
        search: this.searchTerm() || undefined,
        profile: profile !== 'ALL' ? profile : undefined,
        businessCategory: category !== 'ALL' ? category : undefined,
        minAge: ageBucket?.min,
        maxAge: ageBucket && ageBucket.max !== Infinity ? ageBucket.max : undefined,
        minEmployees: employeeBucket?.min,
        maxEmployees: employeeBucket && employeeBucket.max !== Infinity ? employeeBucket.max : undefined,
        page: this.currentPage(),
        size: PAGE_SIZE,
      })
      .subscribe((res) => {
        if (res.success && res.data) this.result.set(res.data);
      });
  }

  applySearch(): void {
    this.searchTerm.set(this.search);
    this.currentPage.set(0);
    this.load();
  }

  setProfileFilter(p: Profile | 'ALL'): void {
    this.profileFilter.set(this.profileFilter() === p ? 'ALL' : p);
    this.currentPage.set(0);
    this.load();
  }

  setCategoryFilter(c: BusinessCategory | 'ALL'): void {
    this.categoryFilter.set(this.categoryFilter() === c ? 'ALL' : c);
    this.currentPage.set(0);
    this.load();
  }

  setAgeBucketFilter(v: string): void {
    this.ageBucketFilter.set(this.ageBucketFilter() === v ? 'ALL' : v);
    this.currentPage.set(0);
    this.load();
  }

  setEmployeeBucketFilter(v: string): void {
    this.employeeBucketFilter.set(this.employeeBucketFilter() === v ? 'ALL' : v);
    this.currentPage.set(0);
    this.load();
  }

  toggleFiltros(): void {
    this.filtrosOpen.set(!this.filtrosOpen());
  }

  clearAll(): void {
    this.profileFilter.set('ALL');
    this.categoryFilter.set('ALL');
    this.ageBucketFilter.set('ALL');
    this.employeeBucketFilter.set('ALL');
    this.currentPage.set(0);
    this.load();
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.set(this.currentPage() - 1);
      this.load();
    }
  }

  nextPage(): void {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.currentPage.set(this.currentPage() + 1);
      this.load();
    }
  }

  openDetail(u: UserResponse): void {
    this.selectedUser.set(u);
  }

  initials(user: UserResponse): string {
    return initialsOf(user.fullName);
  }

  profileLabel(user: UserResponse): string {
    return PROFILE_LABELS[user.profile];
  }

  businessCategoryLabel(user: UserResponse): string | null {
    return user.businessCategory ? BUSINESS_CATEGORY_LABELS[user.businessCategory] : null;
  }

  age(user: UserResponse): number | null {
    return ageFrom(user.birthDate);
  }

  memberSince(user: UserResponse): string {
    return new Date(user.createdAt).toLocaleDateString('es-ES', { day: '2-digit', month: 'long', year: 'numeric' });
  }
}
