import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface Tile {
  id: string;
  label: string;
  className: string;
  route?: string;
}

/**
 * Pantalla inicial de "Activo 2" (shell estatico).
 * Replica visualmente la interfaz de referencia (Interfaz.png): fondo oscuro,
 * seccion "Ultimas nominas" y una rejilla de 6 accesos directos.
 * Unicamente el tile "Incidencias" navega a un modulo funcional real.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  nominas = [
    { mes: 'Diciembre' },
    { mes: 'Noviembre' },
  ];

  tiles: Tile[] = [
    { id: 'salario', label: 'Salario', className: 'tile-salario' },
    { id: 'jornada', label: 'Jornada', className: 'tile-jornada' },
    { id: 'documentos', label: 'Documentos', className: 'tile-documentos' },
    { id: 'formacion', label: 'Formación', className: 'tile-formacion' },
    { id: 'informacion', label: 'Información', className: 'tile-informacion' },
    { id: 'incidencias', label: 'Incidencias', className: 'tile-incidencias', route: '/incidencias' },
  ];

  constructor(private router: Router) {}

  onTileClick(tile: Tile): void {
    if (tile.route) {
      this.router.navigateByUrl(tile.route);
    }
  }
}
