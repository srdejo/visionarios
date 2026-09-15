import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { AuthHero } from '../../../shared/auth-hero/auth-hero';
import { PasswordInput } from '../../../shared/password-input/password-input';

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule, RouterLink, AuthHero, PasswordInput],
  templateUrl: './reset-password.html',
})
export class ResetPassword {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  readonly token = this.route.snapshot.paramMap.get('token') ?? '';
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  password = '';
  confirmPassword = '';

  submit(): void {
    if (this.password !== this.confirmPassword) {
      this.error.set('Las contraseñas no coinciden');
      return;
    }
    this.error.set(null);
    this.loading.set(true);
    this.auth.resetPassword(this.token, this.password, this.confirmPassword).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.router.navigateByUrl('/login');
        } else {
          this.error.set(res.error ?? 'No pudimos restablecer tu contraseña.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Enlace inválido o expirado.');
      },
    });
  }
}
