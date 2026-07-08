import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ZonaService } from '../../core/services/zona.service';
import { EdificioService } from '../../core/services/edificio.service';
import { Zona } from '../../core/models/zona.model';
import { Edificio } from '../../core/models/edificio.model';

@Component({
  selector: 'app-zonas-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './zonas-list.component.html',
  styleUrl: './zonas-list.component.scss',
})
export class ZonasListComponent implements OnInit {
  private fb = inject(FormBuilder);

  zonas = signal<Zona[]>([]);
  edificios = signal<Edificio[]>([]);
  cargando = signal(true);
  mostrarModal = signal(false);
  errorMensaje = signal('');
  guardando = signal(false);

  formulario = this.fb.group({
    edificioId: [null as number | null, Validators.required],
    nombre: ['', Validators.required],
    codigo: ['', Validators.required],
    descripcion: [''],
    orden: [0, Validators.required],
    estado: [true],
  });

  constructor(
    private zonaService: ZonaService,
    private edificioService: EdificioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    forkJoin({
      zonas: this.zonaService.listar(),
      edificios: this.edificioService.listar(),
    }).subscribe(({ zonas, edificios }) => {
      this.zonas.set(zonas);
      this.edificios.set(edificios);
      this.cargando.set(false);
    });
  }

  nombreEdificio(edificioId: number): string {
    return this.edificios().find((e) => e.id === edificioId)?.nombre ?? '—';
  }

  abrirNuevaZona(): void {
    this.formulario.reset({ estado: true, orden: this.zonas().length, edificioId: this.edificios()[0]?.id ?? null });
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
    this.zonaService.crear(this.formulario.value as any).subscribe({
      next: (zona) => {
        this.guardando.set(false);
        this.mostrarModal.set(false);
        this.router.navigate(['/admin/zonas', zona.id]);
      },
      error: (err) => {
        this.errorMensaje.set(err?.error?.message ?? 'No se ha podido crear la zona');
        this.guardando.set(false);
      },
    });
  }

  abrirZona(id: number): void {
    this.router.navigate(['/admin/zonas', id]);
  }
}
