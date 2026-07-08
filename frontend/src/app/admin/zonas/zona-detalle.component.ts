import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ZonaService } from '../../core/services/zona.service';
import { EdificioService } from '../../core/services/edificio.service';
import { AsignacionService } from '../../core/services/asignacion.service';
import { Zona } from '../../core/models/zona.model';
import { Edificio } from '../../core/models/edificio.model';
import { Asignacion } from '../../core/models/asignacion.model';

@Component({
  selector: 'app-zona-detalle',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './zona-detalle.component.html',
  styleUrl: './zona-detalle.component.scss',
})
export class ZonaDetalleComponent implements OnInit {
  private fb = inject(FormBuilder);

  zona = signal<Zona | null>(null);
  edificios = signal<Edificio[]>([]);
  asignacionesZona = signal<Asignacion[]>([]);
  cargando = signal(true);
  guardando = signal(false);
  mensajeExito = signal('');
  errorMensaje = signal('');

  private zonaId!: number;

  formulario = this.fb.group({
    edificioId: [null as number | null, Validators.required],
    nombre: ['', Validators.required],
    codigo: ['', Validators.required],
    descripcion: [''],
    imagen: [''],
    orden: [0, Validators.required],
    estado: [true],
  });

  constructor(
    private route: ActivatedRoute,
    private zonaService: ZonaService,
    private edificioService: EdificioService,
    private asignacionService: AsignacionService
  ) {}

  ngOnInit(): void {
    this.zonaId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarTodo();
  }

  private cargarTodo(): void {
    this.cargando.set(true);

    forkJoin({
      zona: this.zonaService.obtener(this.zonaId),
      edificios: this.edificioService.listar(),
      asignaciones: this.asignacionService.listar(),
    }).subscribe(({ zona, edificios, asignaciones }) => {
      this.edificios.set(edificios);
      this.zona.set(zona);
      this.asignacionesZona.set(asignaciones.filter((a) => a.zonaId === this.zonaId));

      this.formulario.patchValue({
        edificioId: zona.edificioId,
        nombre: zona.nombre,
        codigo: zona.codigo,
        descripcion: zona.descripcion ?? '',
        imagen: zona.imagen ?? '',
        orden: zona.orden,
        estado: zona.estado,
      });
      this.cargando.set(false);
    });
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.errorMensaje.set('');
    this.zonaService.actualizar(this.zonaId, this.formulario.value as any).subscribe({
      next: (zona) => {
        this.zona.set(zona);
        this.guardando.set(false);
        this.mostrarExito('Datos de la zona actualizados');
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido actualizar la zona');
        this.guardando.set(false);
      },
    });
  }

  private mostrarExito(mensaje: string): void {
    this.mensajeExito.set(mensaje);
    setTimeout(() => this.mensajeExito.set(''), 3000);
  }
}
