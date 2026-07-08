import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirmacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmacion.component.html',
  styleUrl: './confirmacion.component.scss',
})
export class ConfirmacionComponent implements OnInit {
  codigo = signal<string | null>(null);
  mensaje = signal('El responsable del departamento seleccionado ha sido notificado.');
  responsableNotificado = signal(true);

  constructor(private router: Router) {}

  ngOnInit(): void {
    const state = this.router.getCurrentNavigation()?.extras.state ?? (history.state as Record<string, unknown>);
    const codigo = state?.['codigo'] as string | undefined;
    const responsableNotificado = state?.['responsableNotificado'] as boolean | undefined;

    if (codigo) this.codigo.set(codigo);
    if (responsableNotificado !== undefined) {
      this.responsableNotificado.set(responsableNotificado);
      if (!responsableNotificado) {
        this.mensaje.set(
          'La incidencia ha quedado registrada, pero no se ha podido notificar por WhatsApp al responsable. El equipo revisará el envío.'
        );
      }
    }
  }

  comunicarOtra(): void {
    this.router.navigateByUrl('/incidencias');
  }

  volverInicio(): void {
    this.router.navigateByUrl('/');
  }
}
