import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { DepartamentoService } from '../../core/services/departamento.service';
import { ResponsableService } from '../../core/services/responsable.service';
import { Departamento } from '../../core/models/departamento.model';
import { Responsable } from '../../core/models/responsable.model';

@Component({
  selector: 'app-departamentos-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './departamentos-list.component.html',
  styleUrl: './departamentos-list.component.scss',
})
export class DepartamentosListComponent implements OnInit {
  private fb = inject(FormBuilder);

  departamentos = signal<Departamento[]>([]);
  responsables = signal<Responsable[]>([]);
  cargando = signal(true);
  mostrarModal = signal(false);
  guardando = signal(false);
  errorMensaje = signal('');
  editandoId = signal<number | null>(null);

  formulario = this.fb.group({
    nombre: ['', Validators.required],
    descripcion: [''],
    responsablePrincipalId: [null as number | null],
    plantillaWhatsapp: [''],
    estado: [true],
  });

  constructor(
    private departamentoService: DepartamentoService,
    private responsableService: ResponsableService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    forkJoin({
      departamentos: this.departamentoService.listar(),
      responsables: this.responsableService.listar(),
    }).subscribe(({ departamentos, responsables }) => {
      this.departamentos.set(departamentos);
      this.responsables.set(responsables);
      this.cargando.set(false);
    });
  }

  nombreResponsable(id: number | null): string {
    if (!id) return '—';
    const r = this.responsables().find((resp) => resp.id === id);
    return r ? `${r.nombre} ${r.apellidos ?? ''}`.trim() : '—';
  }

  abrirNuevo(): void {
    this.editandoId.set(null);
    this.formulario.reset({ estado: true });
    this.errorMensaje.set('');
    this.mostrarModal.set(true);
  }

  abrirEdicion(dep: Departamento): void {
    this.editandoId.set(dep.id);
    this.formulario.reset({
      nombre: dep.nombre,
      descripcion: dep.descripcion ?? '',
      responsablePrincipalId: dep.responsablePrincipalId,
      plantillaWhatsapp: dep.plantillaWhatsapp ?? '',
      estado: dep.estado,
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
      ? this.departamentoService.actualizar(id, this.formulario.value as any)
      : this.departamentoService.crear(this.formulario.value as any);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarModal.set(false);
        this.cargar();
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido guardar el departamento');
        this.guardando.set(false);
      },
    });
  }

  eliminar(dep: Departamento): void {
    if (!confirm(`¿Desactivar el departamento "${dep.nombre}"?`)) return;
    this.departamentoService.eliminar(dep.id).subscribe(() => this.cargar());
  }
}
