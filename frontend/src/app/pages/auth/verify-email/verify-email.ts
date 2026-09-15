import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';

@Component({
  selector: 'app-verify-email',
  imports: [RouterLink],
  templateUrl: './verify-email.html',
})
export class VerifyEmail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);

  readonly token = this.route.snapshot.paramMap.get('token') ?? '';
  readonly loading = signal(true);
  readonly done = signal(false);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.auth.verifyEmail(this.token).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.done.set(true);
        } else {
          this.error.set(res.error ?? 'No pudimos confirmar tu correo.');
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.error ?? 'Enlace inválido o expirado.');
      },
    });
  }
}
