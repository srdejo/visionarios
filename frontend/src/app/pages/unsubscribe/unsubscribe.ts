import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiResponse } from '../../core/models';

@Component({
  selector: 'app-unsubscribe',
  imports: [RouterLink],
  templateUrl: './unsubscribe.html',
})
export class Unsubscribe {
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);

  readonly token = this.route.snapshot.paramMap.get('token') ?? '';
  readonly loading = signal(false);
  readonly done = signal(false);
  readonly error = signal<string | null>(null);

  confirm(): void {
    this.error.set(null);
    this.loading.set(true);
    this.http.post<ApiResponse<null>>(`/api/mail/unsubscribe/${this.token}`, {}).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.done.set(true);
        } else {
          this.error.set(res.error ?? 'No pudimos procesar la baja.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Enlace inválido.');
      },
    });
  }
}
