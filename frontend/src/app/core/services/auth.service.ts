import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminResponse, LoginRequest, LoginResponse } from '../models/auth.model';

const TOKEN_KEY = 'a2i_token';
const ADMIN_KEY = 'a2i_admin';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly administrador = signal<AdminResponse | null>(this.leerAdminGuardado());

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiBaseUrl}/admin/auth/login`, request).pipe(
      tap((respuesta) => this.guardarSesion(respuesta))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ADMIN_KEY);
    this.administrador.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAutenticado(): boolean {
    return !!this.getToken();
  }

  private guardarSesion(respuesta: LoginResponse): void {
    localStorage.setItem(TOKEN_KEY, respuesta.token);
    localStorage.setItem(ADMIN_KEY, JSON.stringify(respuesta.admin));
    this.administrador.set(respuesta.admin);
  }

  private leerAdminGuardado(): AdminResponse | null {
    const raw = localStorage.getItem(ADMIN_KEY);
    return raw ? (JSON.parse(raw) as AdminResponse) : null;
  }
}
