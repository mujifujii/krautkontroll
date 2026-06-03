package kkbf.krautkontroll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integrationstest (Endung *IT -> wird vom maven-failsafe-plugin ausgeführt).
 *
 * Startet die komplette Anwendung inkl. eingebettetem Webserver auf einem
 * zufälligen Port und ruft die echte HTTP-Schnittstelle auf. Prüft damit
 * Controller + Service + Web-Schicht im Zusammenspiel.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RiskControllerIT {

    // Vom Spring-Test-Framework gesetzt, wenn webEnvironment = RANDOM_PORT.
    @Value("${local.server.port}")
    private int port;

    @Test
    void liefertScoreUeberHttp() throws Exception {
        HttpResponse<String> response = get("/risk?attendance=500&capacity=1000");
        assertEquals(200, response.statusCode());
        assertEquals("50", response.body());
    }

    @Test
    void volleAuslastungLiefert100() throws Exception {
        HttpResponse<String> response = get("/risk?attendance=1000&capacity=1000");
        assertEquals(200, response.statusCode());
        assertEquals("100", response.body());
    }

    /** Hilfsmethode: führt eine GET-Anfrage gegen die laufende App aus. */
    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}
