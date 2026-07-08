/** Departamento - version reducida usada en el flujo publico de incidencias. */
export interface DepartamentoPublico {
  id: number;
  nombre: string;
}

/** Departamento - version completa gestionada desde el panel de administracion. */
export interface Departamento {
  id: number;
  nombre: string;
  descripcion: string | null;
  estado: boolean;
  responsablePrincipalId: number | null;
  plantillaWhatsapp: string | null;
}

export interface DepartamentoRequest {
  nombre: string;
  descripcion?: string;
  estado: boolean;
  responsablePrincipalId?: number | null;
  plantillaWhatsapp?: string;
}
