package kkbf.krautkontroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // aktiviert @Scheduled fuer den CrowdSimulator-Tick
public class KrautkontrollApplication {

    public static void main(String[] args) {
        SpringApplication.run(KrautkontrollApplication.class, args);
    }

}
