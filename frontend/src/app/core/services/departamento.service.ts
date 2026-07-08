import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Departamento, DepartamentoRequest } from '../models/departamento.model';
import { EstadoActivoApi, estadoToApi, estadoToBoolean } from './estado-mapper';

interface DepartamentoApi extends Omit<Departamento, 'estado'> {
  estado: EstadoActivoApi;
}

@Injectable({ providedIn: 'root' })
export class DepartamentoService {
  private readonly base = `${environment.apiBaseUrl}/admin/departments`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Departamento[]> {
    return this.http.get<DepartamentoApi[]>(this.base).pipe(map((lista) => lista.map(fromApi)));
  }

  crear(request: DepartamentoRequest): Observable<Departamento> {
    return this.http.post<DepartamentoApi>(this.base, toApi(request)).pipe(map(fromApi));
  }

  actualizar(id: number, request: DepartamentoRequest): Observable<Departamento> {
    return this.http.put<DepartamentoApi>(`${this.base}/${id}`, toApi(request)).pipe(map(fromApi));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function fromApi(d: DepartamentoApi): Departamento {
  return { ...d, estado: estadoToBoolean(d.estado) };
}

function toApi(
  request: DepartamentoRequest
): Omit<DepartamentoRequest, 'estado'> & { estado: EstadoActivoApi } {
  return { ...request, estado: estadoToApi(request.estado) };
}
