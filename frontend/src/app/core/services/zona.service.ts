import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Zona, ZonaRequest } from '../models/zona.model';
import { EstadoActivoApi, estadoToApi, estadoToBoolean } from './estado-mapper';

interface ZonaApi extends Omit<Zona, 'estado'> {
  estado: EstadoActivoApi;
}

@Injectable({ providedIn: 'root' })
export class ZonaService {
  private readonly base = `${environment.apiBaseUrl}/admin/zones`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Zona[]> {
    return this.http.get<ZonaApi[]>(this.base).pipe(map((lista) => lista.map(fromApi)));
  }

  obtener(id: number): Observable<Zona> {
    return this.http.get<ZonaApi>(`${this.base}/${id}`).pipe(map(fromApi));
  }

  crear(request: ZonaRequest): Observable<Zona> {
    return this.http.post<ZonaApi>(this.base, toApi(request)).pipe(map(fromApi));
  }

  actualizar(id: number, request: ZonaRequest): Observable<Zona> {
    return this.http.put<ZonaApi>(`${this.base}/${id}`, toApi(request)).pipe(map(fromApi));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function fromApi(z: ZonaApi): Zona {
  return { ...z, estado: estadoToBoolean(z.estado) };
}

function toApi(request: ZonaRequest): Omit<ZonaRequest, 'estado'> & { estado: EstadoActivoApi } {
  return { ...request, estado: estadoToApi(request.estado) };
}
