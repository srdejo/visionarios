import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, MaterialRequest, MaterialResponse } from './models';

@Injectable({ providedIn: 'root' })
export class MaterialsService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<ApiResponse<MaterialResponse[]>> {
    return this.http.get<ApiResponse<MaterialResponse[]>>('/api/materials');
  }

  create(payload: MaterialRequest): Observable<ApiResponse<MaterialResponse>> {
    return this.http.post<ApiResponse<MaterialResponse>>('/api/admin/materials', payload);
  }

  update(id: string, payload: MaterialRequest): Observable<ApiResponse<MaterialResponse>> {
    return this.http.put<ApiResponse<MaterialResponse>>(`/api/admin/materials/${id}`, payload);
  }
}
