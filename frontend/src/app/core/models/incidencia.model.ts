export type EstadoIncidencia =
  | 'NUEVA'
  | 'NOTIFICADA'
  | 'ERROR_NOTIFICACION'
  | 'EN_REVISION'
  | 'EN_CURSO'
  | 'RESUELTA'
  | 'CERRADA'
  | 'DESCARTADA';

export const ESTADOS_INCIDENCIA: EstadoIncidencia[] = [
  'NUEVA',
  'NOTIFICADA',
  'ERROR_NOTIFICACION',
  'EN_REVISION',
  'EN_CURSO',
  'RESUELTA',
  'CERRADA',
  'DESCARTADA',
];

export interface ReportarIncidenciaResponse {
  codigo: string;
  mensaje: string;
  responsableNotificado: boolean;
}

export interface IncidenciaResumen {
  id: number;
  codigo: string;
  fecha: string;
  hora: string;
  edificio: string;
  zona: string;
  departamento: string;
  responsableNotificado: string | null;
  estado: EstadoIncidencia;
  descripcionResumida: string;
  tieneImagenes: boolean;
  whatsappEnviado: boolean;
  whatsappError: boolean;
}

export interface ImagenIncidencia {
  id: number;
  url: string;
  nombreOriginal: string;
}

export interface HistorialItem {
  estado: EstadoIncidencia;
  fecha: string;
  comentario?: string | null;
  usuario?: string | null;
}

export interface IncidenciaDetalle {
  id: number;
  codigo: string;
  edificio: string;
  zona: string;
  departamento: string;
  responsableNotificado: string | null;
  telefonoNotificado: string | null;
  descripcion: string;
  imagenes: ImagenIncidencia[];
  fecha: string;
  hora: string;
  estado: EstadoIncidencia;
  historial: HistorialItem[];
  whatsappEstado: string | null;
  mensajeWhatsapp: string | null;
  whatsappError: string | null;
  observacionesInternas: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface IncidenciaFiltros {
  fecha?: string;
  edificioId?: number;
  zonaId?: number;
  departamentoId?: number;
  estado?: EstadoIncidencia;
  whatsappError?: boolean;
  texto?: string;
  page?: number;
  size?: number;
}
