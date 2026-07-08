import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IncidenciaDetalle, IncidenciaFiltros, IncidenciaResumen, PageResponse } from '../models/incidencia.model';

@Injectable({ providedIn: 'root' })
export class IncidenciaAdminService {
  private readonly base = `${environment.apiBaseUrl}/admin/incidents`;

  constructor(private http: HttpClient) {}

  listar(filtros: IncidenciaFiltros): Observable<PageResponse<IncidenciaResumen>> {
    let params = new HttpParams();
    Object.entries(filtros).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, String(value));
      }
    });
    return this.http.get<PageResponse<IncidenciaResumen>>(this.base, { params });
  }

  obtener(id: number): Observable<IncidenciaDetalle> {
    return this.http.get<IncidenciaDetalle>(`${this.base}/${id}`);
  }

  cambiarEstado(id: number, estado: string, observaciones?: string): Observable<IncidenciaDetalle> {
    return this.http.patch<IncidenciaDetalle>(`${this.base}/${id}/status`, { estado, observaciones });
  }

  reenviarWhatsapp(id: number): Observable<IncidenciaDetalle> {
    return this.http.post<IncidenciaDetalle>(`${this.base}/${id}/resend-whatsapp`, {});
  }
}
