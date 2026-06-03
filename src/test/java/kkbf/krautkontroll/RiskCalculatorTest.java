package kkbf.krautkontroll;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Unit-Tests für {@link RiskCalculator} – laufen automatisiert in der CI-Pipeline. */
class RiskCalculatorTest {

    private final RiskCalculator calc = new RiskCalculator();

    @Test
    void halbeAuslastungErgibt50() {
        assertEquals(50, calc.calculateScore(500, 1000));
    }

    @Test
    void volleAuslastungErgibt100() {
        assertEquals(100, calc.calculateScore(1000, 1000));
    }

    @Test
    void ungueltigeKapazitaetWirftException() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.calculateScore(100, 0));
    }
}
