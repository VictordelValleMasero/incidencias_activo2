import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Responsable, ResponsableRequest } from '../models/responsable.model';
import { EstadoActivoApi, estadoToApi, estadoToBoolean } from './estado-mapper';

interface ResponsableApi extends Omit<Responsable, 'estado'> {
  estado: EstadoActivoApi;
}

@Injectable({ providedIn: 'root' })
export class ResponsableService {
  private readonly base = `${environment.apiBaseUrl}/admin/responsibles`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Responsable[]> {
    return this.http.get<ResponsableApi[]>(this.base).pipe(map((lista) => lista.map(fromApi)));
  }

  crear(request: ResponsableRequest): Observable<Responsable> {
    return this.http.post<ResponsableApi>(this.base, toApi(request)).pipe(map(fromApi));
  }

  actualizar(id: number, request: ResponsableRequest): Observable<Responsable> {
    return this.http.put<ResponsableApi>(`${this.base}/${id}`, toApi(request)).pipe(map(fromApi));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function fromApi(r: ResponsableApi): Responsable {
  return { ...r, estado: estadoToBoolean(r.estado) };
}

function toApi(
  request: ResponsableRequest
): Omit<ResponsableRequest, 'estado'> & { estado: EstadoActivoApi } {
  return { ...request, estado: estadoToApi(request.estado) };
}
