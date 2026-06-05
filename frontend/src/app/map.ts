import { AfterViewInit, Component, OnDestroy, effect, inject } from '@angular/core';
import * as L from 'leaflet';
import { PositionUpdate, StompService } from './stomp.service';

/**
 * Map-First-Ansicht: rendert die Live-Positionen aus dem WebSocket-Stream als
 * farbige Marker auf einer OpenStreetMap-Karte. Farbe = Status.
 */
@Component({
  selector: 'app-map',
  imports: [],
  template: `
    <div id="map"></div>
    <div class="hud">
      <span class="dot" [class.on]="stomp.connected()"></span>
      {{ stomp.connected() ? 'verbunden' : 'getrennt' }} ·
      {{ stomp.positions().length }} Personen
    </div>
  `,
  styles: [
    `
      :host {
        display: block;
        position: relative;
      }
      #map {
        height: 100vh;
        width: 100%;
      }
      .hud {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 1000;
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 6px 12px;
        border-radius: 8px;
        background: rgba(17, 24, 39, 0.85);
        color: #fff;
        font: 13px system-ui, sans-serif;
      }
      .dot {
        width: 9px;
        height: 9px;
        border-radius: 50%;
        background: #ef4444;
      }
      .dot.on {
        background: #22c55e;
      }
    `,
  ],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  readonly stomp = inject(StompService);

  private map?: L.Map;
  private readonly markers = new Map<string, L.CircleMarker>();

  constructor() {
    // Reagiert auf jeden neuen Positions-Snapshot aus dem Stream.
    effect(() => {
      const positions = this.stomp.positions();
      if (this.map) {
        this.render(positions);
      }
    });
  }

  ngAfterViewInit(): void {
    this.map = L.map('map').setView([53.5511, 9.9937], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap-Mitwirkende',
    }).addTo(this.map);
    this.stomp.connect();
  }

  private render(positions: PositionUpdate[]): void {
    const seen = new Set<string>();

    for (const p of positions) {
      seen.add(p.id);
      const existing = this.markers.get(p.id);
      const color = this.colorFor(p.status);

      if (existing) {
        existing.setLatLng([p.lat, p.lng]);
        existing.setStyle({ color, fillColor: color });
      } else {
        // circleMarker statt marker() -> umgeht die bekannte Leaflet-Icon-Pfad-Falle.
        const created = L.circleMarker([p.lat, p.lng], {
          radius: 6,
          weight: 2,
          color,
          fillColor: color,
          fillOpacity: 0.85,
        }).addTo(this.map!);
        this.markers.set(p.id, created);
      }
    }

    // Personen, die nicht mehr im Snapshot sind, von der Karte entfernen.
    for (const [id, marker] of this.markers) {
      if (!seen.has(id)) {
        marker.remove();
        this.markers.delete(id);
      }
    }
  }

  private colorFor(status: string): string {
    switch (status) {
      case 'HELP':
        return '#f59e0b';
      case 'BLOCKED':
        return '#ef4444';
      default:
        return '#22c55e';
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }
}
