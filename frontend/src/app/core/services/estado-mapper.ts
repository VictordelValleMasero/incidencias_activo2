/**
 * El backend serializa el estado activo/inactivo de Edificio, Zona, Departamento,
 * Responsable y Asignacion como el enum `EstadoActivo` ("ACTIVO" | "INACTIVO").
 * En el frontend se modela como boolean para simplificar checkboxes y badges;
 * estos helpers hacen la conversion en el borde HTTP (services), sin que el
 * resto de la app (componentes, formularios) tenga que conocer el enum.
 */
export type EstadoActivoApi = 'ACTIVO' | 'INACTIVO';

export function estadoToBoolean(estado: EstadoActivoApi): boolean {
  return estado === 'ACTIVO';
}

export function estadoToApi(activo: boolean): EstadoActivoApi {
  return activo ? 'ACTIVO' : 'INACTIVO';
}
