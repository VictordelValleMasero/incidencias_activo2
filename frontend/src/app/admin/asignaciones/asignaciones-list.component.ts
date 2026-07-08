import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AsignacionService } from '../../core/services/asignacion.service';
import { EdificioService } from '../../core/services/edificio.service';
import { ZonaService } from '../../core/services/zona.service';
import { DepartamentoService } from '../../core/services/departamento.service';
import { ResponsableService } from '../../core/services/responsable.service';
import { Asignacion } from '../../core/models/asignacion.model';
import { Edificio } from '../../core/models/edificio.model';
import { Zona } from '../../core/models/zona.model';
import { Departamento } from '../../core/models/departamento.model';
import { Responsable } from '../../core/models/responsable.model';

@Component({
  selector: 'app-asignaciones-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './asignaciones-list.component.html',
  styleUrl: './asignaciones-list.component.scss',
})
export class AsignacionesListComponent implements OnInit {
  private fb = inject(FormBuilder);

  asignaciones = signal<Asignacion[]>([]);
  edificios = signal<Edificio[]>([]);
  zonas = signal<Zona[]>([]);
  departamentos = signal<Departamento[]>([]);
  responsables = signal<Responsable[]>([]);

  cargando = signal(true);
  mostrarModal = signal(false);
  guardando = signal(false);
  errorMensaje = signal('');
  editandoId = signal<number | null>(null);

  edificioSeleccionado = signal<number | null>(null);
  departamentoSeleccionado = signal<number | null>(null);

  zonasDisponibles = computed(() =>
    this.zonas().filter((z) => z.edificioId === this.edificioSeleccionado())
  );
  responsablesDisponibles = computed(() =>
    this.responsables().filter((r) => r.departamentoId === this.departamentoSeleccionado())
  );

  formulario = this.fb.group({
    edificioId: [null as number | null, Validators.required],
    zonaId: [null as number | null, Validators.required],
    departamentoId: [null as number | null, Validators.required],
    responsableId: [null as number | null, Validators.required],
    observaciones: [''],
    estado: [true],
  });

  constructor(
    private asignacionService: AsignacionService,
    private edificioService: EdificioService,
    private zonaService: ZonaService,
    private departamentoService: DepartamentoService,
    private responsableService: ResponsableService
  ) {
    this.formulario.get('edificioId')?.valueChanges.subscribe((id) => {
      this.edificioSeleccionado.set(id);
      this.formulario.get('zonaId')?.setValue(null);
    });
    this.formulario.get('departamentoId')?.valueChanges.subscribe((id) => {
      this.departamentoSeleccionado.set(id);
      this.formulario.get('responsableId')?.setValue(null);
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    forkJoin({
      asignaciones: this.asignacionService.listar(),
      edificios: this.edificioService.listar(),
      zonas: this.zonaService.listar(),
      departamentos: this.departamentoService.listar(),
      responsables: this.responsableService.listar(),
    }).subscribe(({ asignaciones, edificios, zonas, departamentos, responsables }) => {
      this.asignaciones.set(asignaciones);
      this.edificios.set(edificios);
      this.zonas.set(zonas);
      this.departamentos.set(departamentos);
      this.responsables.set(responsables);
      this.cargando.set(false);
    });
  }

  nombreEdificio(id: number): string {
    return this.edificios().find((e) => e.id === id)?.nombre ?? '—';
  }
  nombreZona(id: number): string {
    return this.zonas().find((z) => z.id === id)?.nombre ?? '—';
  }
  nombreDepartamento(id: number): string {
    return this.departamentos().find((d) => d.id === id)?.nombre ?? '—';
  }
  nombreResponsable(id: number): string {
    const r = this.responsables().find((resp) => resp.id === id);
    return r ? `${r.nombre} ${r.apellidos ?? ''}`.trim() : '—';
  }

  abrirNueva(): void {
    this.editandoId.set(null);
    this.formulario.reset({ estado: true });
    this.edificioSeleccionado.set(null);
    this.departamentoSeleccionado.set(null);
    this.errorMensaje.set('');
    this.mostrarModal.set(true);
  }

  abrirEdicion(a: Asignacion): void {
    this.editandoId.set(a.id);
    this.edificioSeleccionado.set(a.edificioId);
    this.departamentoSeleccionado.set(a.departamentoId);
    this.formulario.reset({
      edificioId: a.edificioId,
      zonaId: a.zonaId,
      departamentoId: a.departamentoId,
      responsableId: a.responsableId,
      observaciones: a.observaciones ?? '',
      estado: a.estado,
    });
    this.errorMensaje.set('');
    this.mostrarModal.set(true);
  }

  cerrarModal(): void {
    this.mostrarModal.set(false);
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    const id = this.editandoId();
    const peticion = id
      ? this.asignacionService.actualizar(id, this.formulario.value as any)
      : this.asignacionService.crear(this.formulario.value as any);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarModal.set(false);
        this.cargar();
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido guardar la asignación');
        this.guardando.set(false);
      },
    });
  }

  eliminar(a: Asignacion): void {
    if (!confirm('¿Eliminar esta asignación?')) return;
    this.asignacionService.eliminar(a.id).subscribe(() => this.cargar());
  }
}
