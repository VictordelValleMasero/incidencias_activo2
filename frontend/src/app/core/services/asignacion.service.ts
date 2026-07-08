import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Asignacion, AsignacionRequest } from '../models/asignacion.model';
import { EstadoActivoApi, estadoToApi, estadoToBoolean } from './estado-mapper';

interface AsignacionApi extends Omit<Asignacion, 'estado'> {
  estado: EstadoActivoApi;
}

@Injectable({ providedIn: 'root' })
export class AsignacionService {
  private readonly base = `${environment.apiBaseUrl}/admin/assignments`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Asignacion[]> {
    return this.http.get<AsignacionApi[]>(this.base).pipe(map((lista) => lista.map(fromApi)));
  }

  crear(request: AsignacionRequest): Observable<Asignacion> {
    return this.http.post<AsignacionApi>(this.base, toApi(request)).pipe(map(fromApi));
  }

  actualizar(id: number, request: AsignacionRequest): Observable<Asignacion> {
    return this.http.put<AsignacionApi>(`${this.base}/${id}`, toApi(request)).pipe(map(fromApi));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function fromApi(a: AsignacionApi): Asignacion {
  return { ...a, estado: estadoToBoolean(a.estado) };
}

function toApi(
  request: AsignacionRequest
): Omit<AsignacionRequest, 'estado'> & { estado: EstadoActivoApi } {
  return { ...request, estado: estadoToApi(request.estado) };
}
