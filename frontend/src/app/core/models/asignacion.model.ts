export interface Asignacion {
  id: number;
  edificioId: number;
  zonaId: number;
  departamentoId: number;
  responsableId: number;
  estado: boolean;
  observaciones: string | null;

  /** Campos derivados que el backend puede incluir para facilitar el listado. */
  edificioNombre?: string;
  zonaNombre?: string;
  departamentoNombre?: string;
  responsableNombre?: string;
}

export interface AsignacionRequest {
  edificioId: number;
  zonaId: number;
  departamentoId: number;
  responsableId: number;
  estado: boolean;
  observaciones?: string;
}
