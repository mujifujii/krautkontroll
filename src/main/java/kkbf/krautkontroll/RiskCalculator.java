package kkbf.krautkontroll;

/** Berechnet einen einfachen Crowd-Risk-Score aus der Auslastung. */
public class RiskCalculator {

    /**
     * @param attendance erwartete Besucherzahl
     * @param capacity   maximale Kapazität (muss &gt; 0 sein)
     * @return Score von 0 (leer) bis 100 (voll ausgelastet)
     */
    public int calculateScore(int attendance, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity muss > 0 sein");
        }
        double occupancy = (double) attendance / capacity;
        return (int) Math.round(Math.min(occupancy, 1.0) * 100);
    }
}
