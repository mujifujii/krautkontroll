# Testkonzept – Krautkontroll

Beschreibt die Qualitätssicherung (QS) im Software-Development-Life-Cycle.
Deckt die Anforderungen aus **Teil 1** ab:

- **Must:** Unit-Tests laufen automatisiert in der CI.
- **Should:** mindestens **drei** QS-Maßnahmen *neben* den automatisierten Tests
  sowie automatisierte **Integrationstests**.

## 1. Ziel

Jede Änderung wird automatisiert geprüft, bevor sie nach `main` bzw. in die
Produktivumgebung gelangt. Fehler sollen so früh wie möglich auffallen
("Shift Left").

## 2. Teststufen (Testpyramide)

| Stufe | Was wird getestet? | Werkzeug | Datei |
|-------|--------------------|----------|-------|
| **Unit** | Risiko-Logik isoliert | JUnit 5 | `RiskCalculatorTest` (3 Tests) |
| **Integration** | Controller + Service über HTTP | Spring Boot Test + MockMvc | `RiskControllerIT` (2 Tests) |
| **Context** | Spring-Context startet fehlerfrei | Spring Boot Test | `KrautkontrollApplicationTests` |

Unit-Tests laufen über das **maven-surefire-plugin** (`*Test`), Integrationstests
(`*IT`) über das **maven-failsafe-plugin**. Beide werden mit `./mvnw verify`
automatisch in der CI ausgeführt.

## 3. QS-Maßnahmen NEBEN den automatisierten Tests (Should: mind. 3)

1. **Testabdeckung mit JaCoCo (automatisiert in der CI).**
   Bei jedem Lauf wird ein Coverage-Report erzeugt und als Artefakt abgelegt.
   Macht sichtbar, welcher Code ungetestet ist.

2. **Pull Requests + Branch Protection (Vier-Augen-Prinzip).**
   Änderungen gelangen nur per Pull Request nach `main`; die CI muss grün sein
   (Required Status Check), bevor gemergt werden darf.
   *(In GitHub unter Settings → Branches → Branch protection rules aktivieren.)*

3. **Reproduzierbare Builds.**
   Der **Maven-Wrapper** (`mvnw`) pinnt die Maven-Version; die CI baut in einer
   frischen, sauberen Umgebung mit `npm`-/Maven-Cache. So ist das Ergebnis
   unabhängig vom lokalen Rechner ("works on my machine" wird vermieden).

4. **Definition of Done (DoD).**
   Eine Aufgabe gilt erst als fertig, wenn: Code reviewt, alle Tests grün,
   Build erfolgreich, Doku aktualisiert.

5. **Lesbarer Code.**
   Sprechende Namen + Kommentare an nicht offensichtlichen Stellen erleichtern
   Reviews und Wartung.

## 4. Automatisierung in der Pipeline

Definiert in `.github/workflows/ci.yml`. Bei **jedem Push und Pull Request**:

1. Checkout
2. JDK 21 + Cache
3. `./mvnw verify` → Kompilieren · Unit-Tests · Integrationstests · JaCoCo
4. Artefakte ablegen (JAR + Coverage-Report)
5. **Nur auf `main`:** Docker-Image bauen und nach GHCR deployen (CD)

Zusätzlich `.github/workflows/monitoring.yml`: zeitgesteuerter Healthcheck
(`/actuator/health`) als **simuliertes Monitoring** der Produktionsumgebung.

## 5. Beispiel-Testszenarien (Crowd-Risk)

| Eingabe | Erwartung |
|---------|-----------|
| 500 von 1000 Plätzen | Score 50 |
| 1000 von 1000 Plätzen | Score 100 (voll) |
| Kapazität = 0 | `IllegalArgumentException` |
| `GET /risk?attendance=500&capacity=1000` | HTTP 200, Body `50` |

## 6. Ausblick

- Coverage-Schwelle als hartes CI-Gate (z. B. min. 80 %).
- Statische Analyse (Checkstyle/SpotBugs) als zusätzliche QS-Stage.
- `npm audit`/Dependabot für Sicherheits-/Dependency-Scanning.
