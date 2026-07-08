/** Edificio - version reducida usada en el flujo publico de incidencias. */
export interface EdificioPublico {
  id: number;
  nombre: string;
  codigo: string;
}

/** Edificio - version completa gestionada desde el panel de administracion. */
export interface Edificio {
  id: number;
  nombre: string;
  codigo: string;
  descripcion: string | null;
  estado: boolean;
  orden: number;
}

export interface EdificioRequest {
  nombre: string;
  codigo: string;
  descripcion?: string;
  estado: boolean;
  orden: number;
}
