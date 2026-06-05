import { Injectable, signal } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/** Ein Positions-Update wie es das Backend auf /topic/positions sendet. */
export interface PositionUpdate {
  id: string;
  lat: number;
  lng: number;
  status: string;
}

/**
 * Hält die STOMP-über-WebSocket-Verbindung zum Backend und stellt den
 * aktuellen Positions-Snapshot als Signal bereit.
 */
@Injectable({ providedIn: 'root' })
export class StompService {
  /** Letzter empfangener Positions-Snapshot. */
  readonly positions = signal<PositionUpdate[]>([]);
  /** Verbindungsstatus (für die HUD-Anzeige). */
  readonly connected = signal(false);

  private client?: Client;

  connect(): void {
    if (this.client) {
      return;
    }
    this.client = new Client({
      // SockJS statt nativem WebSocket -> Proxy/Fallback-freundlich im Dev-Setup.
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 2000,
      onConnect: () => {
        this.connected.set(true);
        this.client?.subscribe('/topic/positions', (message: IMessage) => {
          this.positions.set(JSON.parse(message.body) as PositionUpdate[]);
        });
      },
      onWebSocketClose: () => this.connected.set(false),
      onStompError: (frame) => console.error('STOMP-Fehler:', frame.headers['message']),
    });
    this.client.activate();
  }
}
