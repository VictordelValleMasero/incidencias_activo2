import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PublicIncidenciasService } from '../../core/services/public-incidencias.service';
import { EdificioPublico } from '../../core/models/edificio.model';

const DESCRIPCIONES: Record<string, string> = {
  JARRODS: 'Comunicar incidencia en Jarrods',
  CAMPUS: 'Comunicar incidencia en Campus',
};

@Component({
  selector: 'app-edificio-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './edificio-select.component.html',
  styleUrl: './edificio-select.component.scss',
})
export class EdificioSelectComponent implements OnInit {
  edificios = signal<EdificioPublico[]>([]);
  cargando = signal(true);
  error = signal('');

  constructor(
    private publicService: PublicIncidenciasService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.publicService.listarEdificios().subscribe({
      next: (edificios) => {
        this.edificios.set(edificios);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se han podido cargar los edificios. Comprueba tu conexión e inténtalo de nuevo.');
        this.cargando.set(false);
      },
    });
  }

  descripcion(edificio: EdificioPublico): string {
    return DESCRIPCIONES[edificio.codigo?.toUpperCase()] ?? `Comunicar incidencia en ${edificio.nombre}`;
  }

  seleccionar(edificio: EdificioPublico): void {
    this.router.navigate(['/incidencias', edificio.id, 'zonas'], {
      state: { edificioNombre: edificio.nombre },
    });
  }

  volver(): void {
    this.router.navigateByUrl('/');
  }
}
