package kkbf.krautkontroll.crowd;

/**
 * Wire-DTO fuer ein einzelnes Positions-Update Richtung Client.
 * Bewusst flach und serialisierungsfreundlich (Status als String).
 */
public record PositionUpdate(String id, double lat, double lng, String status) {

    public static PositionUpdate from(Participant p) {
        return new PositionUpdate(p.getId(), p.getLat(), p.getLng(), p.getStatus().name());
    }
}
