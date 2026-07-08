import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/** Descarga imagenes de incidencias autenticadas y las expone como blob/object URL. */
@Injectable({ providedIn: 'root' })
export class ImagenService {
  constructor(private http: HttpClient) {}

  descargar(imagenId: number): Observable<Blob> {
    return this.http.get(`${environment.apiBaseUrl}/admin/images/${imagenId}`, { responseType: 'blob' });
  }
}
