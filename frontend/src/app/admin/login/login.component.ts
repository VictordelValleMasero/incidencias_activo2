import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);

  cargando = signal(false);
  errorMensaje = signal('');

  formulario = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  constructor(private authService: AuthService, private router: Router) {}

  iniciarSesion(): void {
    if (this.formulario.invalid || this.cargando()) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.errorMensaje.set('');

    const { email, password } = this.formulario.value;
    this.authService.login({ email: email!, password: password! }).subscribe({
      next: () => this.router.navigate(['/admin/dashboard']),
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'Credenciales incorrectas. Inténtalo de nuevo.');
        this.cargando.set(false);
      },
    });
  }
}
