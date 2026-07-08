import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/** Rutas publicas del API que nunca deben llevar cabecera de autorizacion. */
const RUTAS_PUBLICAS = ['/api/incidencias/', '/api/admin/auth/'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const esPublica = RUTAS_PUBLICAS.some((ruta) => req.url.includes(ruta));

  if (token && !esPublica) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req);
};
