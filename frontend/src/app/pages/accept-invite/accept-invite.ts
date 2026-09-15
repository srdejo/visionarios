import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { Profile } from '../../core/models';
import { PasswordInput } from '../../shared/password-input/password-input';

@Component({
  selector: 'app-accept-invite',
  imports: [FormsModule, PasswordInput],
  templateUrl: './accept-invite.html',
})
export class AcceptInvite {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  readonly token = this.route.snapshot.paramMap.get('token') ?? '';
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  fullName = '';
  phone = '';
  password = '';
  profile: Profile = 'PROFESIONAL';

  submit(): void {
    this.error.set(null);
    this.loading.set(true);
    this.auth
      .acceptInvite(this.token, {
        fullName: this.fullName,
        phone: this.phone,
        password: this.password,
        profile: this.profile,
      })
      .subscribe({
        next: (res) => {
          this.loading.set(false);
          if (res.success) {
            this.router.navigateByUrl('/home');
          } else {
            this.error.set(res.error ?? 'No pudimos aceptar la invitación.');
          }
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Enlace inválido o expirado.');
        },
      });
  }
}
