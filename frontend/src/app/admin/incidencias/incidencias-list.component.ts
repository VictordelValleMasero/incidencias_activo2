import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { IncidenciaAdminService } from '../../core/services/incidencia-admin.service';
import { EdificioService } from '../../core/services/edificio.service';
import { ZonaService } from '../../core/services/zona.service';
import { DepartamentoService } from '../../core/services/departamento.service';
import { IncidenciaResumen, ESTADOS_INCIDENCIA } from '../../core/models/incidencia.model';
import { Edificio } from '../../core/models/edificio.model';
import { Zona } from '../../core/models/zona.model';
import { Departamento } from '../../core/models/departamento.model';

@Component({
  selector: 'app-incidencias-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './incidencias-list.component.html',
  styleUrl: './incidencias-list.component.scss',
})
export class IncidenciasListComponent implements OnInit {
  private fb = inject(FormBuilder);

  incidencias = signal<IncidenciaResumen[]>([]);
  edificios = signal<Edificio[]>([]);
  zonas = signal<Zona[]>([]);
  departamentos = signal<Departamento[]>([]);
  cargando = signal(true);
  totalPaginas = signal(0);
  totalElementos = signal(0);
  paginaActual = signal(0);
  estados = ESTADOS_INCIDENCIA;

  zonasFiltroDisponibles = computed(() => {
    const edificioId = this.filtros.get('edificioId')?.value;
    if (!edificioId) return this.zonas();
    return this.zonas().filter((z) => z.edificioId === Number(edificioId));
  });

  filtros = this.fb.group({
    fecha: [''],
    edificioId: [''],
    zonaId: [''],
    departamentoId: [''],
    estado: [''],
    whatsappError: [false],
    texto: [''],
  });

  constructor(
    private incidenciaService: IncidenciaAdminService,
    private edificioService: EdificioService,
    private zonaService: ZonaService,
    private departamentoService: DepartamentoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const whatsappError = this.route.snapshot.queryParamMap.get('whatsappError');
    if (whatsappError === 'true') {
      this.filtros.patchValue({ whatsappError: true });
    }

    forkJoin({
      edificios: this.edificioService.listar(),
      zonas: this.zonaService.listar(),
      departamentos: this.departamentoService.listar(),
    }).subscribe(({ edificios, zonas, departamentos }) => {
      this.edificios.set(edificios);
      this.zonas.set(zonas);
      this.departamentos.set(departamentos);
      this.buscar();
    });
  }

  buscar(pagina = 0): void {
    this.cargando.set(true);
    const valores = this.filtros.value;

    this.incidenciaService
      .listar({
        fecha: valores.fecha || undefined,
        edificioId: valores.edificioId ? Number(valores.edificioId) : undefined,
        zonaId: valores.zonaId ? Number(valores.zonaId) : undefined,
        departamentoId: valores.departamentoId ? Number(valores.departamentoId) : undefined,
        estado: (valores.estado as any) || undefined,
        whatsappError: valores.whatsappError || undefined,
        texto: valores.texto || undefined,
        page: pagina,
        size: 20,
      })
      .subscribe((resultado) => {
        this.incidencias.set(resultado.content);
        this.totalPaginas.set(resultado.totalPages);
        this.totalElementos.set(resultado.totalElements);
        this.paginaActual.set(resultado.number);
        this.cargando.set(false);
      });
  }

  cambiarPagina(delta: number): void {
    const nueva = this.paginaActual() + delta;
    if (nueva < 0 || nueva >= this.totalPaginas()) return;
    this.buscar(nueva);
  }

  abrir(id: number): void {
    this.router.navigate(['/admin/incidencias', id]);
  }
}
