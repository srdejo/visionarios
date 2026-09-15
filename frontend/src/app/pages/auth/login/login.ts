import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { AuthHero } from '../../../shared/auth-hero/auth-hero';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, AuthHero],
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly rememberPassword = signal(true);
  readonly emailAutocomplete = computed(() => (this.rememberPassword() ? 'username' : 'off'));
  readonly passwordAutocomplete = computed(() => (this.rememberPassword() ? 'current-password' : 'off'));

  submit(): void {
    this.error.set(null);
    this.loading.set(true);
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.storeCredentialIfRemembered();
          // El recordatorio de correo sin confirmar (si aplica) se muestra
          // dentro del dashboard (ver Home), no aca, para no dar la sensacion
          // de que el login quedo bloqueado.
          this.router.navigateByUrl('/home');
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

  private storeCredentialIfRemembered(): void {
    if (!this.rememberPassword()) {
      return;
    }
    const PasswordCredentialCtor = (window as unknown as { PasswordCredential?: new (data: unknown) => Credential })
      .PasswordCredential;
    if (!PasswordCredentialCtor || !navigator.credentials?.store) {
      return;
    }
    const credential = new PasswordCredentialCtor({ id: this.email, password: this.password });
    navigator.credentials.store(credential).catch(() => undefined);
  }
}
