package kkbf.krautkontroll;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stellt den berechneten Crowd-Risk-Score über HTTP bereit (REST-API).
 *
 * Beispiel: GET /risk?attendance=500&capacity=1000  ->  50
 */
@RestController
public class RiskController {

    private final RiskCalculator riskCalculator;

    // Konstruktor-Injektion des Service (Spring Dependency Injection)
    public RiskController(RiskCalculator riskCalculator) {
        this.riskCalculator = riskCalculator;
    }

    @GetMapping("/risk")
    public int risk(@RequestParam int attendance, @RequestParam int capacity) {
        return riskCalculator.calculateScore(attendance, capacity);
    }
}
