/** Zona - version reducida usada en el flujo publico de incidencias. */
export interface ZonaPublica {
  id: number;
  nombre: string;
  codigo: string;
}

/** Zona - version completa gestionada desde el panel de administracion. */
export interface Zona {
  id: number;
  edificioId: number;
  nombre: string;
  codigo: string;
  descripcion: string | null;
  imagen: string | null;
  estado: boolean;
  orden: number;
}

export interface ZonaRequest {
  edificioId: number;
  nombre: string;
  codigo: string;
  descripcion?: string;
  imagen?: string;
  estado: boolean;
  orden: number;
}
