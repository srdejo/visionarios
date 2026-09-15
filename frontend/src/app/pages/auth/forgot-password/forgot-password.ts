import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { AuthHero } from '../../../shared/auth-hero/auth-hero';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, RouterLink, AuthHero],
  templateUrl: './forgot-password.html',
})
export class ForgotPassword {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  submit(): void {
    this.error.set(null);
    this.loading.set(true);
    this.auth.forgotPassword(this.email).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.router.navigateByUrl('/login');
        } else {
          this.error.set(res.error ?? 'No encontramos ese correo.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No encontramos ese correo.');
      },
    });
  }
}
