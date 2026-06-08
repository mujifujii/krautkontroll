package kkbf.krautkontroll.crowd;

/**
 * In-Memory-Live-Zustand einer Person.
 *
 * <p>BEWUSST KEINE JPA-{@code @Entity}: Die Live-Position aendert sich jede Sekunde und
 * lebt nur im Arbeitsspeicher (Architektur-Entscheidung "Live-State != Historie").
 * Persistiert werden spaeter nur Stammdaten (Identitaet/Gruppe) und Ereignisse,
 * nicht jeder Positions-Tick.
 */
public class Participant {

    private final String id;
    private double lat;
    private double lng;
    private Status status;

    public Participant(String id, double lat, double lng, Status status) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
