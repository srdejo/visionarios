import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { MaterialsService } from '../../core/materials.service';
import { MaterialResponse, PROFILE_LABELS, Profile } from '../../core/models';

type FilterValue = 'ALL' | 'NETWORK' | Profile;

@Component({
  selector: 'app-biblioteca',
  imports: [],
  templateUrl: './biblioteca.html',
})
export class Biblioteca {
  private readonly auth = inject(AuthService);
  private readonly materialsService = inject(MaterialsService);

  readonly materials = signal<MaterialResponse[]>([]);
  readonly filter = signal<FilterValue>('ALL');

  readonly userProfile = computed(() => this.auth.currentUser()?.profile ?? null);
  readonly userProfileLabel = computed(() => {
    const p = this.userProfile();
    return p ? PROFILE_LABELS[p] : '';
  });

  readonly filtered = computed(() => {
    const f = this.filter();
    const list = this.materials();
    if (f === 'ALL') return list;
    if (f === 'NETWORK') return list.filter((m) => m.visibleProfile === null);
    return list.filter((m) => m.visibleProfile === f);
  });

  constructor() {
    this.materialsService.list().subscribe((res) => {
      if (res.success && res.data) this.materials.set(res.data);
    });
  }

  setFilter(f: FilterValue): void {
    this.filter.set(f);
  }

  visibilityLabel(m: MaterialResponse): string {
    return m.visibleProfile ? PROFILE_LABELS[m.visibleProfile] : 'Toda la red';
  }
}
