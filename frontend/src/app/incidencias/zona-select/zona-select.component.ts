import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PublicIncidenciasService } from '../../core/services/public-incidencias.service';
import { ZonaPublica } from '../../core/models/zona.model';

const UMBRAL_BUSCADOR = 8;

@Component({
  selector: 'app-zona-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './zona-select.component.html',
  styleUrl: './zona-select.component.scss',
})
export class ZonaSelectComponent implements OnInit {
  edificioId!: number;
  edificioNombre = signal<string>('');
  zonas = signal<ZonaPublica[]>([]);
  cargando = signal(true);
  error = signal('');
  textoBusqueda = signal('');

  zonasFiltradas = computed(() => {
    const texto = this.textoBusqueda().trim().toLowerCase();
    if (!texto) return this.zonas();
    return this.zonas().filter((z) => z.nombre.toLowerCase().includes(texto));
  });

  mostrarBuscador = computed(() => this.zonas().length > UMBRAL_BUSCADOR);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publicService: PublicIncidenciasService
  ) {}

  ngOnInit(): void {
    this.edificioId = Number(this.route.snapshot.paramMap.get('edificioId'));

    const state = this.router.getCurrentNavigation()?.extras.state ?? (history.state as Record<string, unknown>);
    const nombreDesdeEstado = state?.['edificioNombre'] as string | undefined;
    if (nombreDesdeEstado) {
      this.edificioNombre.set(nombreDesdeEstado);
    } else {
      this.publicService.listarEdificios().subscribe((edificios) => {
        const encontrado = edificios.find((e) => e.id === this.edificioId);
        if (encontrado) this.edificioNombre.set(encontrado.nombre);
      });
    }

    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.publicService.listarZonas(this.edificioId).subscribe({
      next: (zonas) => {
        this.zonas.set(zonas);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se han podido cargar las zonas. Comprueba tu conexión e inténtalo de nuevo.');
        this.cargando.set(false);
      },
    });
  }

  seleccionar(zona: ZonaPublica): void {
    this.router.navigate(['/incidencias', this.edificioId, 'zonas', zona.id, 'formulario'], {
      state: { edificioNombre: this.edificioNombre(), zonaNombre: zona.nombre },
    });
  }

  volver(): void {
    this.router.navigateByUrl('/incidencias');
  }
}
