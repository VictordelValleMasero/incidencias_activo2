import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { IncidenciaAdminService } from '../../core/services/incidencia-admin.service';
import { ImagenService } from '../../core/services/imagen.service';
import { IncidenciaDetalle, ESTADOS_INCIDENCIA } from '../../core/models/incidencia.model';

@Component({
  selector: 'app-incidencia-detalle',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './incidencia-detalle.component.html',
  styleUrl: './incidencia-detalle.component.scss',
})
export class IncidenciaDetalleComponent implements OnInit {
  private fb = inject(FormBuilder);

  incidencia = signal<IncidenciaDetalle | null>(null);
  cargando = signal(true);
  guardandoEstado = signal(false);
  reenviando = signal(false);
  mensajeExito = signal('');
  imagenesCargadas = signal<{ id: number; url: string; nombreOriginal: string }[]>([]);
  estados = ESTADOS_INCIDENCIA;

  private id!: number;

  formularioEstado = this.fb.group({
    estado: [''],
    observaciones: [''],
  });

  constructor(
    private route: ActivatedRoute,
    private incidenciaService: IncidenciaAdminService,
    private imagenService: ImagenService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.incidenciaService.obtener(this.id).subscribe((inc) => {
      this.incidencia.set(inc);
      this.formularioEstado.reset({ estado: inc.estado, observaciones: inc.observacionesInternas ?? '' });
      this.cargando.set(false);
      this.cargarImagenes(inc.imagenes);
    });
  }

  private cargarImagenes(imagenes: { id: number; url: string; nombreOriginal: string }[]): void {
    this.imagenesCargadas().forEach((img) => URL.revokeObjectURL(img.url));
    this.imagenesCargadas.set([]);
    imagenes.forEach((img) => {
      this.imagenService.descargar(img.id).subscribe((blob) => {
        this.imagenesCargadas.set([
          ...this.imagenesCargadas(),
          { id: img.id, url: URL.createObjectURL(blob), nombreOriginal: img.nombreOriginal },
        ]);
      });
    });
  }

  guardarEstado(): void {
    const { estado, observaciones } = this.formularioEstado.value;
    if (!estado) return;
    if (!confirm(`¿Confirmas el cambio de estado a "${estado}"?`)) return;

    this.guardandoEstado.set(true);
    this.incidenciaService.cambiarEstado(this.id, estado, observaciones ?? undefined).subscribe({
      next: (inc) => {
        this.incidencia.set(inc);
        this.guardandoEstado.set(false);
        this.mostrarExito('Incidencia actualizada correctamente');
      },
      error: () => this.guardandoEstado.set(false),
    });
  }

  reenviarWhatsapp(): void {
    this.reenviando.set(true);
    this.incidenciaService.reenviarWhatsapp(this.id).subscribe({
      next: (inc) => {
        this.incidencia.set(inc);
        this.reenviando.set(false);
        this.mostrarExito('WhatsApp reenviado');
      },
      error: () => this.reenviando.set(false),
    });
  }

  private mostrarExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => this.mensajeExito.set(''), 3000);
  }
}
