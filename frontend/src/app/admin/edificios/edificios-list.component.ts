import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { EdificioService } from '../../core/services/edificio.service';
import { Edificio } from '../../core/models/edificio.model';

@Component({
  selector: 'app-edificios-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edificios-list.component.html',
  styleUrl: './edificios-list.component.scss',
})
export class EdificiosListComponent implements OnInit {
  private fb = inject(FormBuilder);

  edificios = signal<Edificio[]>([]);
  cargando = signal(true);
  mostrarModal = signal(false);
  guardando = signal(false);
  errorMensaje = signal('');
  editandoId = signal<number | null>(null);

  formulario = this.fb.group({
    nombre: ['', Validators.required],
    codigo: ['', Validators.required],
    descripcion: [''],
    orden: [0, Validators.required],
    estado: [true],
  });

  constructor(private edificioService: EdificioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.edificioService.listar().subscribe((edificios) => {
      this.edificios.set(edificios);
      this.cargando.set(false);
    });
  }

  abrirNuevo(): void {
    this.editandoId.set(null);
    this.formulario.reset({ estado: true, orden: this.edificios().length });
    this.errorMensaje.set('');
    this.mostrarModal.set(true);
  }

  abrirEdicion(edificio: Edificio): void {
    this.editandoId.set(edificio.id);
    this.formulario.reset({
      nombre: edificio.nombre,
      codigo: edificio.codigo,
      descripcion: edificio.descripcion ?? '',
      orden: edificio.orden,
      estado: edificio.estado,
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
      ? this.edificioService.actualizar(id, this.formulario.value as any)
      : this.edificioService.crear(this.formulario.value as any);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarModal.set(false);
        this.cargar();
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido guardar el edificio');
        this.guardando.set(false);
      },
    });
  }

  eliminar(edificio: Edificio): void {
    if (!confirm(`¿Desactivar el edificio "${edificio.nombre}"?`)) return;
    this.edificioService.eliminar(edificio.id).subscribe(() => this.cargar());
  }
}
