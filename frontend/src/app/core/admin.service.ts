import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminInviteResponse, AdminUsersResponse, ApiResponse } from './models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private readonly http: HttpClient) {}

  listUsers(): Observable<ApiResponse<AdminUsersResponse>> {
    return this.http.get<ApiResponse<AdminUsersResponse>>('/api/admin/users');
  }

  createInvite(email: string): Observable<ApiResponse<AdminInviteResponse>> {
    return this.http.post<ApiResponse<AdminInviteResponse>>('/api/admin/invites', { email });
  }

  listInvites(): Observable<ApiResponse<AdminInviteResponse[]>> {
    return this.http.get<ApiResponse<AdminInviteResponse[]>>('/api/admin/invites');
  }
}
