import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Edificio, EdificioRequest } from '../models/edificio.model';
import { EstadoActivoApi, estadoToApi, estadoToBoolean } from './estado-mapper';

interface EdificioApi extends Omit<Edificio, 'estado'> {
  estado: EstadoActivoApi;
}

@Injectable({ providedIn: 'root' })
export class EdificioService {
  private readonly base = `${environment.apiBaseUrl}/admin/buildings`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Edificio[]> {
    return this.http.get<EdificioApi[]>(this.base).pipe(map((lista) => lista.map(fromApi)));
  }

  crear(request: EdificioRequest): Observable<Edificio> {
    return this.http.post<EdificioApi>(this.base, toApi(request)).pipe(map(fromApi));
  }

  actualizar(id: number, request: EdificioRequest): Observable<Edificio> {
    return this.http.put<EdificioApi>(`${this.base}/${id}`, toApi(request)).pipe(map(fromApi));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function fromApi(e: EdificioApi): Edificio {
  return { ...e, estado: estadoToBoolean(e.estado) };
}

function toApi(request: EdificioRequest): Omit<EdificioRequest, 'estado'> & { estado: EstadoActivoApi } {
  return { ...request, estado: estadoToApi(request.estado) };
}
