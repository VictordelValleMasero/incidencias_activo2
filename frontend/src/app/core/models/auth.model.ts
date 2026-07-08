export interface LoginRequest {
  email: string;
  password: string;
}

export interface AdminResponse {
  id: number;
  nombre: string;
  email: string;
}

export interface LoginResponse {
  token: string;
  admin: AdminResponse;
}
