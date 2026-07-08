import { IncidenciaResumen } from './incidencia.model';

export interface ZonaConteoDto {
  zona: string;
  total: number;
}

export interface DashboardResumen {
  incidenciasHoy: number;
  pendientes: number;
  notificadas: number;
  errorWhatsapp: number;
  totalEdificios: number;
  totalZonas: number;
  totalDepartamentos: number;
  ultimasIncidencias: IncidenciaResumen[];
  zonasConMasIncidencias: ZonaConteoDto[];
}
