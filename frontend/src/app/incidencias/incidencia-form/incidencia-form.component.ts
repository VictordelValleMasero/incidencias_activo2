import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PublicIncidenciasService } from '../../core/services/public-incidencias.service';
import { DepartamentoPublico } from '../../core/models/departamento.model';

const MAX_IMAGENES = 5;
const MAX_TAMANO_MB = 5;
const TIPOS_PERMITIDOS = ['image/jpeg', 'image/png', 'image/webp'];

type EstadoCarga = 'cargando' | 'listo' | 'error';

@Component({
  selector: 'app-incidencia-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './incidencia-form.component.html',
  styleUrl: './incidencia-form.component.scss',
})
export class IncidenciaFormComponent implements OnInit {
  private fb = inject(FormBuilder);

  edificioId!: number;
  zonaId!: number;
  edificioNombre = signal('');
  zonaNombre = signal('');

  estadoCarga = signal<EstadoCarga>('cargando');
  departamentos = signal<DepartamentoPublico[]>([]);
  enviando = signal(false);
  errorEnvio = signal('');
  errorImagenes = signal('');

  imagenes = signal<File[]>([]);
  previews = signal<string[]>([]);

  formulario = this.fb.group({
    departamentoId: [null as number | null, Validators.required],
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private publicService: PublicIncidenciasService
  ) {}

  ngOnInit(): void {
    this.edificioId = Number(this.route.snapshot.paramMap.get('edificioId'));
    this.zonaId = Number(this.route.snapshot.paramMap.get('zonaId'));

    const state = this.router.getCurrentNavigation()?.extras.state ?? (history.state as Record<string, unknown>);
    const edificioNombre = state?.['edificioNombre'] as string | undefined;
    const zonaNombre = state?.['zonaNombre'] as string | undefined;
    if (edificioNombre) this.edificioNombre.set(edificioNombre);
    if (zonaNombre) this.zonaNombre.set(zonaNombre);

    this.cargarDepartamentos();
  }

  cargarDepartamentos(): void {
    this.estadoCarga.set('cargando');
    this.publicService.listarDepartamentos(this.zonaId).subscribe({
      next: (departamentos) => {
        this.departamentos.set(departamentos);
        this.estadoCarga.set('listo');
      },
      error: () => {
        this.estadoCarga.set('error');
      },
    });
  }

  get descripcionLength(): number {
    return this.formulario.get('descripcion')?.value?.length ?? 0;
  }

  onSeleccionarImagenes(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    this.errorImagenes.set('');
    const actuales = this.imagenes();
    const nuevos = Array.from(input.files);
    const rechazados: string[] = [];

    const validos = nuevos.filter((file) => {
      if (!TIPOS_PERMITIDOS.includes(file.type)) {
        rechazados.push(`${file.name} (formato no permitido)`);
        return false;
      }
      if (file.size > MAX_TAMANO_MB * 1024 * 1024) {
        rechazados.push(`${file.name} (supera ${MAX_TAMANO_MB}MB)`);
        return false;
      }
      return true;
    });

    const total = [...actuales, ...validos].slice(0, MAX_IMAGENES);
    if (actuales.length + validos.length > MAX_IMAGENES) {
      rechazados.push(`solo se permiten ${MAX_IMAGENES} imágenes como máximo`);
    }

    if (rechazados.length > 0) {
      this.errorImagenes.set(`Algunas imágenes no se han adjuntado: ${rechazados.join(', ')}.`);
    }

    this.imagenes.set(total);
    this.previews.set(total.map((f) => URL.createObjectURL(f)));
    input.value = '';
  }

  eliminarImagen(index: number): void {
    const actuales = [...this.imagenes()];
    actuales.splice(index, 1);
    this.imagenes.set(actuales);
    this.previews.set(actuales.map((f) => URL.createObjectURL(f)));
  }

  enviar(): void {
    if (this.formulario.invalid || this.enviando()) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.errorEnvio.set('');
    const { departamentoId, descripcion } = this.formulario.value;

    this.publicService
      .reportarIncidencia(this.edificioId, this.zonaId, departamentoId as number, descripcion as string, this.imagenes())
      .subscribe({
        next: (respuesta) => {
          this.enviando.set(false);
          this.router.navigate(['/incidencias', this.edificioId, 'zonas', this.zonaId, 'confirmacion'], {
            state: {
              codigo: respuesta.codigo,
              responsableNotificado: respuesta.responsableNotificado,
              edificioNombre: this.edificioNombre(),
              zonaNombre: this.zonaNombre(),
            },
          });
        },
        error: (err) => {
          this.errorEnvio.set(err?.error?.message ?? 'No se ha podido enviar la incidencia. Inténtalo de nuevo.');
          this.enviando.set(false);
        },
      });
  }

  volver(): void {
    this.router.navigate(['/incidencias', this.edificioId, 'zonas']);
  }
}
