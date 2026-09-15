import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { AuthHero } from '../../../shared/auth-hero/auth-hero';
import { InfoModal } from '../../../shared/info-modal/info-modal';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, AuthHero, InfoModal],
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly showVerifyReminder = signal(false);

  submit(): void {
    this.error.set(null);
    this.loading.set(true);
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          if (res.data && !res.data.user.emailVerified) {
            this.showVerifyReminder.set(true);
          } else {
            this.router.navigateByUrl('/home');
          }
        } else {
          this.error.set(res.error ?? 'No pudimos iniciar sesión.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Correo o contraseña incorrectos.');
      },
    });
  }

  dismissVerifyReminder(): void {
    this.showVerifyReminder.set(false);
    this.router.navigateByUrl('/home');
  }
}
