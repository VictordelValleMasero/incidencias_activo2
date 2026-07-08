import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ResponsableService } from '../../core/services/responsable.service';
import { DepartamentoService } from '../../core/services/departamento.service';
import { Responsable } from '../../core/models/responsable.model';
import { Departamento } from '../../core/models/departamento.model';

@Component({
  selector: 'app-responsables-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './responsables-list.component.html',
  styleUrl: './responsables-list.component.scss',
})
export class ResponsablesListComponent implements OnInit {
  private fb = inject(FormBuilder);

  responsables = signal<Responsable[]>([]);
  departamentos = signal<Departamento[]>([]);
  cargando = signal(true);
  mostrarModal = signal(false);
  guardando = signal(false);
  errorMensaje = signal('');
  editandoId = signal<number | null>(null);

  formulario = this.fb.group({
    nombre: ['', Validators.required],
    apellidos: [''],
    cargo: [''],
    departamentoId: [null as number | null, Validators.required],
    telefonoWhatsapp: ['', [Validators.required, Validators.pattern(/^\+[1-9][0-9]{7,14}$/)]],
    email: ['', Validators.email],
    horario: [''],
    estado: [true],
  });

  constructor(
    private responsableService: ResponsableService,
    private departamentoService: DepartamentoService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    forkJoin({
      responsables: this.responsableService.listar(),
      departamentos: this.departamentoService.listar(),
    }).subscribe(({ responsables, departamentos }) => {
      this.responsables.set(responsables);
      this.departamentos.set(departamentos);
      this.cargando.set(false);
    });
  }

  nombreDepartamento(id: number): string {
    return this.departamentos().find((d) => d.id === id)?.nombre ?? '—';
  }

  abrirNuevo(): void {
    this.editandoId.set(null);
    this.formulario.reset({ estado: true });
    this.errorMensaje.set('');
    this.mostrarModal.set(true);
  }

  abrirEdicion(resp: Responsable): void {
    this.editandoId.set(resp.id);
    this.formulario.reset({
      nombre: resp.nombre,
      apellidos: resp.apellidos ?? '',
      cargo: resp.cargo ?? '',
      departamentoId: resp.departamentoId,
      telefonoWhatsapp: resp.telefonoWhatsapp,
      email: resp.email ?? '',
      horario: resp.horario ?? '',
      estado: resp.estado,
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
      ? this.responsableService.actualizar(id, this.formulario.value as any)
      : this.responsableService.crear(this.formulario.value as any);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarModal.set(false);
        this.cargar();
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido guardar el responsable');
        this.guardando.set(false);
      },
    });
  }

  eliminar(resp: Responsable): void {
    if (!confirm(`¿Desactivar a "${resp.nombre}"?`)) return;
    this.responsableService.eliminar(resp.id).subscribe(() => this.cargar());
  }
}
