import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EdificioPublico } from '../models/edificio.model';
import { ZonaPublica } from '../models/zona.model';
import { DepartamentoPublico } from '../models/departamento.model';
import { ReportarIncidenciaResponse } from '../models/incidencia.model';

/** Servicio para el flujo publico "Comunicar incidencia" (sin login). */
@Injectable({ providedIn: 'root' })
export class PublicIncidenciasService {
  private readonly base = `${environment.apiBaseUrl}/incidencias`;

  constructor(private http: HttpClient) {}

  listarEdificios(): Observable<EdificioPublico[]> {
    return this.http.get<EdificioPublico[]>(`${this.base}/buildings`);
  }

  listarZonas(edificioId: number): Observable<ZonaPublica[]> {
    return this.http.get<ZonaPublica[]>(`${this.base}/buildings/${edificioId}/zones`);
  }

  listarDepartamentos(zonaId: number): Observable<DepartamentoPublico[]> {
    return this.http.get<DepartamentoPublico[]>(`${this.base}/zones/${zonaId}/departments`);
  }

  reportarIncidencia(
    edificioId: number,
    zonaId: number,
    departamentoId: number,
    descripcion: string,
    imagenes: File[]
  ): Observable<ReportarIncidenciaResponse> {
    const formData = new FormData();
    formData.append('edificioId', String(edificioId));
    formData.append('zonaId', String(zonaId));
    formData.append('departamentoId', String(departamentoId));
    formData.append('descripcion', descripcion);
    imagenes.forEach((file) => formData.append('imagenes', file));

    return this.http.post<ReportarIncidenciaResponse>(`${this.base}/report`, formData);
  }
}
