import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home.component').then((m) => m.HomeComponent),
  },

  {
    path: 'incidencias',
    loadComponent: () =>
      import('./incidencias/edificio-select/edificio-select.component').then((m) => m.EdificioSelectComponent),
  },
  {
    path: 'incidencias/:edificioId/zonas',
    loadComponent: () => import('./incidencias/zona-select/zona-select.component').then((m) => m.ZonaSelectComponent),
  },
  {
    path: 'incidencias/:edificioId/zonas/:zonaId/formulario',
    loadComponent: () =>
      import('./incidencias/incidencia-form/incidencia-form.component').then((m) => m.IncidenciaFormComponent),
  },
  {
    path: 'incidencias/:edificioId/zonas/:zonaId/confirmacion',
    loadComponent: () =>
      import('./incidencias/confirmacion/confirmacion.component').then((m) => m.ConfirmacionComponent),
  },

  {
    path: 'admin/login',
    loadComponent: () => import('./admin/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'admin',
    loadComponent: () => import('./admin/layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./admin/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'edificios',
        loadComponent: () =>
          import('./admin/edificios/edificios-list.component').then((m) => m.EdificiosListComponent),
      },
      {
        path: 'zonas',
        loadComponent: () => import('./admin/zonas/zonas-list.component').then((m) => m.ZonasListComponent),
      },
      {
        path: 'zonas/:id',
        loadComponent: () => import('./admin/zonas/zona-detalle.component').then((m) => m.ZonaDetalleComponent),
      },
      {
        path: 'departamentos',
        loadComponent: () =>
          import('./admin/departamentos/departamentos-list.component').then((m) => m.DepartamentosListComponent),
      },
      {
        path: 'responsables',
        loadComponent: () =>
          import('./admin/responsables/responsables-list.component').then((m) => m.ResponsablesListComponent),
      },
      {
        path: 'asignaciones',
        loadComponent: () =>
          import('./admin/asignaciones/asignaciones-list.component').then((m) => m.AsignacionesListComponent),
      },
      {
        path: 'incidencias',
        loadComponent: () =>
          import('./admin/incidencias/incidencias-list.component').then((m) => m.IncidenciasListComponent),
      },
      {
        path: 'incidencias/:id',
        loadComponent: () =>
          import('./admin/incidencias/incidencia-detalle.component').then((m) => m.IncidenciaDetalleComponent),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
