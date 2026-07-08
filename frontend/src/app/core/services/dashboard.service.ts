import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardResumen } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  obtenerResumen(): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${environment.apiBaseUrl}/admin/dashboard`);
  }
}
