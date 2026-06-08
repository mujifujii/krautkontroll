package kkbf.krautkontroll.crowd;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simuliert eine Menschenmenge: spawnt N Personen um ein Zentrum und laesst sie
 * per Random Walk laufen. Jeder Tick broadcastet den aktuellen Stand an die Karte.
 *
 * <p>Zugleich unser "simuliertes Monitoring" und Demo-Werkzeug. Alle Personen leben
 * nur im RAM (siehe {@link Participant}).
 */
@Service
public class CrowdSimulator {

    private final SimpMessagingTemplate messaging;
    private final Map<String, Participant> participants = new ConcurrentHashMap<>();

    @Value("${krautkontroll.sim.count:1000}")
    private int count;

    @Value("${krautkontroll.sim.center-lat:53.5511}")
    private double centerLat;

    @Value("${krautkontroll.sim.center-lng:9.9937}")
    private double centerLng;

    /** Streuung der Startpositionen in Grad (~0.01 deg ≈ 1 km). */
    @Value("${krautkontroll.sim.spread:0.012}")
    private double spread;

    /** Maximale Schrittweite pro Tick in Grad (~0.0002 deg ≈ 20 m). */
    @Value("${krautkontroll.sim.step:0.0002}")
    private double step;

    public CrowdSimulator(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @PostConstruct
    void spawn() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            String id = "p-" + i;
            double lat = centerLat + (rnd.nextDouble() - 0.5) * spread;
            double lng = centerLng + (rnd.nextDouble() - 0.5) * spread;
            participants.put(id, new Participant(id, lat, lng, Status.OK));
        }
    }

    /** Random Walk + Broadcast, getaktet ueber krautkontroll.sim.tick-ms (Default 1s). */
    @Scheduled(fixedRateString = "${krautkontroll.sim.tick-ms:1000}")
    void tick() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (Participant p : participants.values()) {
            p.setLat(p.getLat() + (rnd.nextDouble() - 0.5) * step);
            p.setLng(p.getLng() + (rnd.nextDouble() - 0.5) * step);
        }
        broadcast();
    }

    /**
     * EINZIGE Broadcast-Stelle. Bewusst isoliert, damit sie spaeter ohne Eingriff in die
     * Tick-Logik von "alle Einzelpositionen" auf Delta-/Aggregat-/Heatmap-Versand
     * umgestellt werden kann (Skalierung jenseits einiger Tausend Personen).
     */
    private void broadcast() {
        List<PositionUpdate> snapshot = participants.values().stream()
                .map(PositionUpdate::from)
                .toList();
        messaging.convertAndSend("/topic/positions", snapshot);
    }
}
