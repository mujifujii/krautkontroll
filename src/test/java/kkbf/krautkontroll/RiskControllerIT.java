package kkbf.krautkontroll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integrationstest (Endung *IT -> wird vom maven-failsafe-plugin ausgeführt).
 *
 * Startet den kompletten Spring-Context und prüft die HTTP-Schnittstelle
 * inklusive Controller + Service zusammen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RiskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void liefertScoreUeberHttp() throws Exception {
        mockMvc.perform(get("/risk").param("attendance", "500").param("capacity", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void volleAuslastungLiefert100() throws Exception {
        mockMvc.perform(get("/risk").param("attendance", "1000").param("capacity", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }
}
