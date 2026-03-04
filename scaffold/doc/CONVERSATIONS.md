# Zusammenfassung der bisherigen Unterhaltungen

## Überblick

Es gab zwei Unterhaltungen zur Klärung der JEE-Hausarbeit (IU Internationale Hochschule, WS 2025/26). Die Werkstudentin einer SAP-Beratungsfirma sollte verstehen, was die Aufgabenstellung konkret verlangt, und ein passendes Konzept erarbeiten.

---

## 1. Unterhaltung mit Claude Opus 4.6 (Hauptunterhaltung)

### Aufgabenstellung entmystifiziert

Die Aufgabe verlangt im Kern:
> „Baut eine vereinfachte Web-Anwendung mit Java/Jakarta EE, die ein paar eurer echten Geschäftsprozesse als Software abbildet."

| Akademischer Begriff | Bedeutung für SAP-Beratungsfirma |
|---|---|
| „Zentrale Strukturen abbilden" | Stammdaten modellieren: Berater, Kunden, SAP-Module, Projekte, Skills |
| „Prozesse abbilden" | Arbeitsabläufe als Software-Workflows: Projekt → Berater zuordnen → Zeiterfassung |
| „Leistungs-/Wertschöpfungsstufen" | Akquise → Angebot → Projekt → Beratung → Abnahme → Rechnung |
| „Verwaltungs-/Hilfsaktivitäten" | Skill-Management, Ressourcenplanung, Zeiterfassung |
| „Hoch aggregiert" | Vereinfacht – nicht jedes Detail, sondern die großen Linien |

### Gewähltes Konzept: „SAP Consulting Management System"

**Ausgewählte Bereiche:**
1. Projektmanagement (Kernprozess)
2. Ressourcen-/Beraterplanung (Kernprozess)
3. Zeiterfassung (Hilfsprozess)
4. Skill-Management (Hilfsprozess)

### Verbindlicher Tech-Stack

| Komponente | Version |
|---|---|
| Java JDK | 1.8 (Java 8) – kein Java 11+! |
| App-Server | GlassFish/Payara 5.1 (Full Profile) |
| Dynamic Web Module | 3.1 (Servlet 3.1) |
| JPA | 2.1 (EclipseLink via Payara) |
| MySQL | 8.0 |
| JSF | 2.3 (in Payara enthalten) – **Schwerpunkt** |
| CDI | 2.0 (in Payara enthalten) – **Schwerpunkt** |
| Build-Tool | **Keines!** Eclipse Dynamic Web Project |

### Datenmodell (5 JPA-Entities)

```
Kunde (1) ──────── (*) Projekt (*) ──────── (*) Berater (1) ──── (*) Skill
                         │
                         │ (1)
                         │
                    (*) Zeiteintrag
```

- **Kunde**: id, firmenname, branche, ansprechpartner, adresse, email
- **Projekt**: id, bezeichnung, beschreibung, sapModul (Enum), status (Enum), startDatum, endDatum, budgetStunden, budgetEuro, kunde (FK)
- **Berater**: id, vorname, nachname, email, senioritaet (Enum), stundensatz, verfuegbar
- **Skill**: id, sapModul (Enum), level (1–5), zertifiziert, berater (FK)
- **Zeiteintrag**: id, datum, stunden, beschreibung, abrechenbar, projekt (FK), berater (FK)

**Enums:**
- `SapModul`: FI, CO, MM, SD, PP, HR, BASIS
- `ProjektStatus`: ANGEBOT → GENEHMIGT → AKTIV → ABGESCHLOSSEN / STORNIERT
- `Senioritaet`: JUNIOR, SENIOR, PRINCIPAL, PARTNER

### Projektstruktur (Eclipse Dynamic Web Project)

```
SapConsultingApp/
├── src/
│   └── de/consulting/
│       ├── model/          ← JPA Entities (Kunde, Projekt, Berater, Skill, Zeiteintrag, Enums)
│       ├── service/        ← CDI Beans / Business-Logik
│       ├── controller/     ← JSF Backing Beans
│       └── converter/      ← JSF Converter
├── WebContent/
│   ├── WEB-INF/
│   │   ├── web.xml, beans.xml, faces-config.xml
│   │   └── classes/META-INF/persistence.xml
│   ├── resources/css/style.css
│   ├── template.xhtml      ← Facelets Master-Layout
│   ├── index.xhtml, projekte.xhtml, berater.xhtml, zeitbuchung.xhtml …
│   └── …
└── build/                   ← Eclipse baut automatisch
```

### Vollständig ausgearbeitete Komponenten

Claude Opus lieferte **vollständigen, lauffähigen Code** für:

**Konfiguration:**
- `web.xml` (Servlet 3.1, FacesServlet-Mapping)
- `beans.xml` (CDI 2.0, bean-discovery-mode="all")
- `faces-config.xml` (JSF 2.3)
- `persistence.xml` (JPA 2.1, JTA, EclipseLink/MySQL)

**JPA Entities (komplett mit Getter/Setter, Validierung, NamedQueries):**
- `Kunde.java`, `Berater.java`, `Skill.java`, `Projekt.java`, `Zeiteintrag.java`

**Service-Schicht (CDI @ApplicationScoped, @Transactional):**
- `ProjektService` – CRUD + Berater-Zuweisung mit Skill-Check + Statusmaschine
- `BeraterService` – CRUD + Verfügbarkeitsfilter + SAP-Modul-Suche
- `ZeiterfassungService` – Zeitbuchung mit Validierung (nur aktive Projekte, nur zugewiesene Berater)
- `SkillService` – Skill-Zuweisung/Aktualisierung
- `KundeService` – CRUD

**Controller (JSF Backing Beans, @Named @ViewScoped):**
- `ProjektController` – Projektverwaltung mit CRUD, Berater-Zuweisung, Statusänderung
- `ZeitController` – Zeitbuchungsformular

**JSF-Seiten:**
- `template.xhtml` – Master-Layout mit Navigation
- `projekte.xhtml` – Projektverwaltung (Formular + Datentabelle)
- `zeitbuchung.xhtml` – Zeiterfassungsformular

**CSS-Styling** und **SQL-Testdaten** (import.sql)

### Business-Regeln (implementiert)

- Nur Berater mit passendem SAP-Modul-Skill dürfen einem Projekt zugewiesen werden
- Nur verfügbare Berater können zugewiesen werden
- Zeitbuchung nur auf aktive Projekte
- Nur dem Projekt zugewiesene Berater dürfen Zeiten buchen
- Statusübergänge folgen einer definierten Zustandsmaschine
- Budget-Überschreitungswarnung bei Zeitbuchung

### Zusätzliche Code-Skizzen (nicht vollständig ausgearbeitet)

- **EJB-basierter ProjektService** (@Stateless statt CDI @ApplicationScoped)
- **JAX-RS REST-Endpunkte** für Projekte (GET, POST, PUT)
- **Timer** (@Singleton @Startup @Schedule) für monatliche Abrechnung
- Erwähnung von Interceptors für Audit-Logging

### Setup-Anleitung

Detaillierte Schritt-für-Schritt-Anleitung:
1. JDK 1.8, Eclipse, Payara 5.1, MySQL 8.0 installieren
2. MySQL-Connector in Payara einbinden (`domain1/lib/`)
3. Datenbank `sap_consulting` anlegen + User `consulting_app`
4. JDBC Connection Pool in Payara Admin Console konfigurieren (JNDI: `jdbc/SapConsultingDS`)
5. Eclipse Dynamic Web Project erstellen (Target Runtime: Payara 5.1)
6. Deployen via Eclipse → Run on Server

---

## 2. Unterhaltung mit GPT-5.2 (Kurzfassung)

GPT-5.2 erhielt dieselbe Ausgangsfrage und lieferte eine **konzeptionelle Übersicht** (keine vollständige Implementierung):

### Übereinstimmende Punkte mit Claude Opus
- Identifizierte dieselben Domänenobjekte: Kunde, Projekt, Berater/Consultant, Zeiteintrag, Skill
- Schlug dasselbe SAP-Beratungsszenario vor
- Empfahl ähnliche Statusmodelle (TimeEntry: ENTERED → SUBMITTED → APPROVED; Invoice: DRAFT → SENT → PAID)

### Unterschiede / Ergänzungen
- **REST-fokussierter Ansatz**: Schlug JAX-RS als primäre API-Schicht vor (statt JSF-Schwerpunkt)
- **Zusätzliche Entities**: Invoice, InvoiceLine, RateCard, Contract, Ticket
- **Zwei Szenario-Vorschläge**: A) Projekt/Timesheet/Billing oder B) Support/Ticketprozess mit SLA
- **Dokumentationsstruktur** empfohlen: Kurzbeschreibung, Abgrenzung, Domänenmodell (UML/ERD), Use Cases (5–8), Architektur, JEE-Mapping, Walkthrough
- Keine konkreten Code-Implementierungen, sondern REST-Endpoint-Auflistung

---

## Fazit & Status

Die **Claude-Opus-Unterhaltung** lieferte ein vollständiges, lauffähiges Grundgerüst mit:
- 5 JPA-Entities mit Beziehungen und Validierung
- 5 Service-Klassen mit Business-Logik
- 2 JSF-Controller + 3 JSF-Seiten
- Alle Konfigurationsdateien
- Testdaten + CSS

**Offene Punkte / noch zu ergänzen:**
- Seiten für Kunden- und Berater-Verwaltung (analog zu projekte.xhtml)
- Dashboard (index.xhtml)
- JSF Converter (KundeConverter, BeraterConverter)
- Mögliche Ergänzung: EJB statt reines CDI (für Demonstrationszwecke)
- Mögliche Ergänzung: Interceptors, Timer, JAX-RS (zusätzliche JEE-Features)
- Dokumentation / Konzeptpapier für die Abgabe
