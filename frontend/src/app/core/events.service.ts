import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, EventRequest, EventResponse, PagedResponse, UserResponse } from './models';

@Injectable({ providedIn: 'root' })
export class EventsService {
  constructor(private readonly http: HttpClient) {}

  list(): Observable<ApiResponse<EventResponse[]>> {
    return this.http.get<ApiResponse<EventResponse[]>>('/api/events');
  }

  get(id: string): Observable<ApiResponse<EventResponse>> {
    return this.http.get<ApiResponse<EventResponse>>(`/api/events/${id}`);
  }

  rsvp(id: string): Observable<ApiResponse<EventResponse>> {
    return this.http.post<ApiResponse<EventResponse>>(`/api/events/${id}/rsvp`, {});
  }

  create(payload: EventRequest): Observable<ApiResponse<EventResponse>> {
    return this.http.post<ApiResponse<EventResponse>>('/api/admin/events', payload);
  }

  update(id: string, payload: EventRequest): Observable<ApiResponse<EventResponse>> {
    return this.http.put<ApiResponse<EventResponse>>(`/api/admin/events/${id}`, payload);
  }

  notify(id: string): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(`/api/admin/events/${id}/notify`, {});
  }

  attendees(id: string, page: number, size: number): Observable<ApiResponse<PagedResponse<UserResponse>>> {
    return this.http.get<ApiResponse<PagedResponse<UserResponse>>>(`/api/admin/events/${id}/attendees`, {
      params: { page, size },
    });
  }
}
