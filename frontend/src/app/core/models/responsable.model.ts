export interface Responsable {
  id: number;
  nombre: string;
  apellidos: string | null;
  cargo: string | null;
  departamentoId: number;
  departamentoNombre?: string;
  telefonoWhatsapp: string;
  email: string | null;
  horario: string | null;
  estado: boolean;
}

export interface ResponsableRequest {
  nombre: string;
  apellidos?: string;
  cargo?: string;
  departamentoId: number;
  telefonoWhatsapp: string;
  email?: string;
  horario?: string;
  estado: boolean;
}
