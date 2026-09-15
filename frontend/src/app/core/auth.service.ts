import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';
import {
  ApiResponse,
  AuthResponse,
  RegisterRequest,
  RegisterResponse,
  UpdateProfileRequest,
  UserResponse,
} from './models';

const TOKEN_KEY = 'visionarios_token';
const USER_KEY = 'visionarios_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly currentUserSignal = signal<UserResponse | null>(this.readStoredUser());
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly isAdmin = computed(() => this.currentUserSignal()?.role === 'ADMIN');

  constructor(private readonly http: HttpClient) {}

  get token(): string | null {
    return this.tokenSignal();
  }

  login(email: string, password: string): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>('/api/auth/login', { email, password })
      .pipe(tap((res) => this.persistSession(res)));
  }

  register(payload: RegisterRequest): Observable<ApiResponse<RegisterResponse>> {
    return this.http.post<ApiResponse<RegisterResponse>>('/api/auth/register', payload);
  }

  forgotPassword(email: string): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>('/api/auth/forgot-password', { email });
  }

  resetPassword(token: string, password: string, confirmPassword: string): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>('/api/auth/reset-password', { token, password, confirmPassword });
  }

  verifyEmail(token: string): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>(`/api/auth/verify-email/${token}`, {});
  }

  resendVerification(): Observable<ApiResponse<null>> {
    return this.http.post<ApiResponse<null>>('/api/auth/resend-verification', {});
  }

  acceptInvite(
    token: string,
    payload: { fullName: string; phone: string; password: string; profile: string },
  ): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`/api/auth/accept-invite/${token}`, payload)
      .pipe(tap((res) => this.persistSession(res)));
  }

  fetchMe(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>('/api/users/me').pipe(
      tap((res) => this.persistUser(res)),
    );
  }

  updateProfile(payload: UpdateProfileRequest): Observable<ApiResponse<UserResponse>> {
    return this.http
      .put<ApiResponse<UserResponse>>('/api/users/me', payload)
      .pipe(tap((res) => this.persistUser(res)));
  }

  private persistUser(res: ApiResponse<UserResponse>): void {
    if (res.success && res.data) {
      this.currentUserSignal.set(res.data);
      localStorage.setItem(USER_KEY, JSON.stringify(res.data));
    }
  }

  logout(): void {
    if (this.tokenSignal()) {
      // Revoca el token en el servidor (ver TokenRevocationStore); no bloquea el
      // logout local si falla (token ya vencido, red caida, etc.).
      this.http
        .post('/api/auth/logout', {})
        .pipe(catchError(() => of(null)))
        .subscribe();
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenSignal.set(null);
    this.currentUserSignal.set(null);
  }

  private persistSession(res: ApiResponse<AuthResponse>): void {
    if (res.success && res.data) {
      localStorage.setItem(TOKEN_KEY, res.data.token);
      localStorage.setItem(USER_KEY, JSON.stringify(res.data.user));
      this.tokenSignal.set(res.data.token);
      this.currentUserSignal.set(res.data.user);
    }
  }

  private readStoredUser(): UserResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as UserResponse) : null;
  }
}
