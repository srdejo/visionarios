import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminInviteResponse, AdminUsersQuery, AdminUsersResponse, ApiResponse } from './models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private readonly http: HttpClient) {}

  listUsers(query: AdminUsersQuery = {}): Observable<ApiResponse<AdminUsersResponse>> {
    const params: Record<string, string | number> = {};
    if (query.search) params['search'] = query.search;
    if (query.profile) params['profile'] = query.profile;
    if (query.businessCategory) params['businessCategory'] = query.businessCategory;
    if (query.minAge != null) params['minAge'] = query.minAge;
    if (query.maxAge != null) params['maxAge'] = query.maxAge;
    if (query.minEmployees != null) params['minEmployees'] = query.minEmployees;
    if (query.maxEmployees != null) params['maxEmployees'] = query.maxEmployees;
    params['page'] = query.page ?? 0;
    params['size'] = query.size ?? 20;
    return this.http.get<ApiResponse<AdminUsersResponse>>('/api/admin/users', { params });
  }

  createInvite(email: string): Observable<ApiResponse<AdminInviteResponse>> {
    return this.http.post<ApiResponse<AdminInviteResponse>>('/api/admin/invites', { email });
  }

  listInvites(): Observable<ApiResponse<AdminInviteResponse[]>> {
    return this.http.get<ApiResponse<AdminInviteResponse[]>>('/api/admin/invites');
  }
}
