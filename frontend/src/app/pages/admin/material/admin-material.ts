import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BottomSheet } from '../../../shared/bottom-sheet/bottom-sheet';
import { MaterialsService } from '../../../core/materials.service';
import { MaterialResponse, MaterialType, PROFILE_LABELS, Profile } from '../../../core/models';

@Component({
  selector: 'app-admin-material',
  imports: [FormsModule, BottomSheet],
  templateUrl: './admin-material.html',
})
export class AdminMaterial {
  private readonly materialsService = inject(MaterialsService);

  readonly materials = signal<MaterialResponse[]>([]);
  readonly showModal = signal(false);
  readonly editingId = signal<string | null>(null);

  readonly typeOptions: MaterialType[] = ['PDF', 'DOC', 'XLS', 'MP3'];
  readonly profileOptions: { value: Profile; label: string }[] = [
    { value: 'PROFESIONAL', label: PROFILE_LABELS.PROFESIONAL },
    { value: 'EMPRENDEDOR', label: PROFILE_LABELS.EMPRENDEDOR },
    { value: 'EMPRESARIO', label: PROFILE_LABELS.EMPRESARIO },
  ];

  title = '';
  type: MaterialType = 'PDF';
  meta = '';
  driveUrl = '';
  visibleProfile: Profile | null = null;

  constructor() {
    this.load();
  }

  load(): void {
    this.materialsService.list().subscribe((res) => {
      if (res.success && res.data) this.materials.set(res.data);
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.title = '';
    this.type = 'PDF';
    this.meta = '';
    this.driveUrl = '';
    this.visibleProfile = null;
    this.showModal.set(true);
  }

  openEdit(m: MaterialResponse): void {
    this.editingId.set(m.id);
    this.title = m.title;
    this.type = m.type;
    this.meta = m.meta;
    this.driveUrl = m.driveUrl;
    this.visibleProfile = m.visibleProfile;
    this.showModal.set(true);
  }

  toggleVisible(p: Profile): void {
    this.visibleProfile = this.visibleProfile === p ? null : p;
  }

  save(): void {
    const payload = {
      title: this.title,
      type: this.type,
      meta: this.meta,
      driveUrl: this.driveUrl,
      visibleProfile: this.visibleProfile,
    };
    const id = this.editingId();
    const request = id ? this.materialsService.update(id, payload) : this.materialsService.create(payload);
    request.subscribe((res) => {
      if (res.success) {
        this.showModal.set(false);
        this.load();
      }
    });
  }

  visibilityLabel(m: MaterialResponse): string {
    return m.visibleProfile ? PROFILE_LABELS[m.visibleProfile] : 'Toda la red';
  }
}
