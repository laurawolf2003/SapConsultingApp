# ARCHITECTURE.md — SAP Consulting Management System

## 1. Projektübersicht

**Titel:** SAP Consulting Management System  
**Kontext:** Hochschulprojekt (IU Internationale Hochschule, WS 2025/26) — Fallstudie Jakarta Enterprise Edition (JEE)  
**Praxispartner:** Software-/Beratungsfirma im SAP-Umfeld  
**Projektname (Eclipse):** `SapConsultingApp`

### Zielsetzung

Vereinfachte Abbildung der zentralen Strukturen und Prozesse einer SAP-Beratungsfirma als JEE-Unternehmensanwendung. Der Fokus liegt auf der **Anwendungs- und Persistenzschicht**; die Präsentationsschicht wird nur soweit umgesetzt, wie es für die End-User-Interaktion erforderlich ist. Es handelt sich um eine hoch aggregierte, demonstrative Umsetzung — kein produktionsreifes System.

---

## 2. Unternehmenskontext

### Wertschöpfungskette (nach Porter)

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PRIMÄRE AKTIVITÄTEN                               │
│                                                                     │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  ┌─────────┐  ┌──────┐ │
│  │ AKQUISE  │→ │ ANGEBOT/ │→ │ PROJEKT-  │→ │BERATUNG/│→ │AFTER-│ │
│  │ Kunden-  │  │ PRESALES │  │ SETUP     │  │UMSETZUNG│  │CARE /│ │
│  │ gewinnung│  │ Scoping  │  │ Planung   │  │Go-Live  │  │SUPPORT│
│  └──────────┘  └──────────┘  └───────────┘  └─────────┘  └──────┘ │
├─────────────────────────────────────────────────────────────────────┤
│                 UNTERSTÜTZENDE AKTIVITÄTEN                          │
│                                                                     │
│  • Skill-/Kompetenzmanagement    • Ressourcenplanung               │
│  • Zeiterfassung                 • Abrechnung/Billing              │
│  • Weiterbildung (SAP Zertif.)  • Wissensmanagement               │
└─────────────────────────────────────────────────────────────────────┘
```

### Abgebildete Bereiche

Für die Aufgabe werden folgende Bereiche modelliert:

| Bereich | Typ | Beschreibung |
|---------|-----|--------------|
| **Projektmanagement** | Kernprozess | Projekte anlegen, Status verwalten, Kunden zuordnen |
| **Ressourcen-/Beraterplanung** | Kernprozess | Berater mit passenden Skills Projekten zuweisen |
| **Zeiterfassung** | Hilfsprozess | Stunden auf Projekte buchen, Auslastung tracken |
| **Skill-Management** | Hilfsprozess | SAP-Modul-Kompetenzen und Zertifizierungen verwalten |

### Annahmen und Abgrenzungen

- Keine echte SAP-Integration oder SAP-Systemanbindung
- Keine echte Rechnungserzeugung oder Steuerberechnung
- Vereinfachtes Billing (nur abrechenbare/nicht-abrechenbare Stunden)
- Kein Berechtigungssystem / Security (außerhalb des Scope)
- Kein komplexes UI-Design — funktionale JSF-Oberflächen genügen

---

## 3. Technologie-Stack

| Komponente | Version | Hinweis |
|---|---|---|
| **Java JDK** | 1.8 (Java 8) | Kein Java 9+! Kein `var`, keine Module, keine Records |
| **Application Server** | Payara/GlassFish 5.1 | Full Profile |
| **Dynamic Web Module** | 3.1 | = Servlet 3.1 Spec |
| **JPA** | 2.1 | EclipseLink (kommt mit Payara) |
| **MySQL** | 8.0 | Relationale Datenbank |
| **JSF** | 2.3 | In Payara 5.1 enthalten — **Schwerpunkt** |
| **CDI** | 2.0 | In Payara 5.1 enthalten — **Schwerpunkt** |
| **Bean Validation** | 2.0 | In Payara 5.1 enthalten |
| **Build-Tool** | ❌ **Keines** | Kein Maven, kein Gradle, kein Ant |
| **IDE** | Eclipse IDE for Enterprise Java Developers | Dynamic Web Project |

> **Hinweis:** Abweichungen von den Versionen sind nur zulässig, wenn keine Kompatibilitätsprobleme oder zusätzlicher Installationsaufwand entstehen.

---

## 4. Architektur — Schichtenmodell

```
┌─────────────────────────────────────────────────────────┐
│              PRÄSENTATIONSSCHICHT (minimal)              │
│                                                         │
│   JSF 2.3 Facelets (.xhtml)                            │
│   ├── template.xhtml (Master-Layout)                    │
│   ├── index.xhtml (Dashboard)                           │
│   ├── projekte.xhtml                                    │
│   ├── berater.xhtml                                     │
│   ├── kunden.xhtml                                      │
│   └── zeitbuchung.xhtml                                 │
│                                                         │
│   Backing Beans (JSF Controller)                        │
│   ├── ProjektController   (@Named, @ViewScoped)         │
│   ├── BeraterController   (@Named, @ViewScoped)         │
│   ├── ZeitController      (@Named, @ViewScoped)         │
│   └── DashboardController (@Named, @ViewScoped)         │
│                                                         │
│   Converter                                             │
│   ├── KundeConverter                                    │
│   └── BeraterConverter                                  │
├─────────────────────────────────────────────────────────┤
│              ANWENDUNGSSCHICHT (Fokus!)                  │
│                                                         │
│   CDI Beans / Services (@ApplicationScoped)             │
│   ├── ProjektService                                    │
│   │   └── Projekt-CRUD, Berater-Zuweisung,              │
│   │       Statusübergänge, Budget-Prüfung               │
│   ├── BeraterService                                    │
│   │   └── Berater-CRUD, Verfügbarkeitsabfrage           │
│   ├── ZeiterfassungService                              │
│   │   └── Zeitbuchung mit Validierung                   │
│   ├── SkillService                                      │
│   │   └── Skill-Zuweisung, Skill-Entfernung             │
│   └── KundeService                                      │
│       └── Kunden-CRUD                                   │
│                                                         │
│   Business-Regeln (in Services)                         │
│   • Skill-Matching bei Berater-Zuweisung                │
│   • Verfügbarkeitsprüfung                               │
│   • Projekt-Statusmaschine                              │
│   • Zeitbuchung nur auf aktive Projekte                 │
│   • Budget-Warnung bei Überschreitung                   │
├─────────────────────────────────────────────────────────┤
│              PERSISTENZSCHICHT                           │
│                                                         │
│   JPA 2.1 Entities                                      │
│   ├── Kunde                                             │
│   ├── Projekt                                           │
│   ├── Berater                                           │
│   ├── Skill                                             │
│   └── Zeiteintrag                                       │
│                                                         │
│   Enums                                                 │
│   ├── SapModul (FI, CO, MM, SD, PP, HR, BASIS)          │
│   └── ProjektStatus (ANGEBOT, GENEHMIGT, AKTIV,         │
│       ABGESCHLOSSEN, STORNIERT)                         │
│                                                         │
│   JPA Provider: EclipseLink (Payara-Standard)           │
│   Persistence Unit: SapConsultingPU (JTA)               │
│   JNDI DataSource: jdbc/SapConsultingDS                 │
├─────────────────────────────────────────────────────────┤
│              DATENBANK                                   │
│                                                         │
│   MySQL 8.0                                             │
│   Schema: sap_consulting                                │
│   User: consulting_app                                  │
│   JDBC Pool: SapConsultingPool (Payara Admin)           │
└─────────────────────────────────────────────────────────┘
```

---

## 5. Datenmodell

### Entity-Relationship-Diagramm

```
┌─────────────┐       ┌──────────────────┐       ┌──────────────┐
│   KUNDE     │       │     PROJEKT      │       │   BERATER    │
├─────────────┤       ├──────────────────┤       ├──────────────┤
│ id (PK)     │ 1   * │ id (PK)          │ *   * │ id (PK)      │
│ firmenname  │───────│ bezeichnung      │───────│ vorname      │
│ branche     │       │ beschreibung     │       │ nachname     │
│ ansprech-   │       │ sapModul (Enum)  │       │ email        │
│   partner   │       │ status (Enum)    │       │ senioritaet  │
│ adresse     │       │ startDatum       │       │ stundensatz  │
│ email       │       │ endDatum         │       │ verfuegbar   │
└─────────────┘       │ budgetStunden    │       └──────┬───────┘
                      │ budgetEuro       │              │ 1
                      │ kunde_id (FK)    │              │
                      └────────┬─────────┘              │
                               │ 1                      │
                               │                 ┌──────┴───────┐
                      ┌────────┴─────────┐       │    SKILL     │
                      │  ZEITEINTRAG     │       ├──────────────┤
                      ├──────────────────┤       │ id (PK)      │
                      │ id (PK)          │       │ sapModul     │
                      │ datum            │       │   (Enum)     │
                      │ stunden          │       │ level (1-5)  │
                      │ beschreibung     │       │ zertifiziert │
                      │ abrechenbar      │       │ berater_id   │
                      │ projekt_id (FK)  │       │   (FK)       │
                      │ berater_id (FK)  │       └──────────────┘
                      └──────────────────┘

Zwischentabelle: projekt_berater (projekt_id, berater_id)
```

### Entity-Beziehungen

| Beziehung | Kardinalität | JPA-Annotation | Beschreibung |
|---|---|---|---|
| Kunde → Projekt | 1:N | `@OneToMany(mappedBy="kunde")` | Ein Kunde hat mehrere Projekte |
| Projekt → Kunde | N:1 | `@ManyToOne` | Jedes Projekt gehört zu genau einem Kunden |
| Projekt ↔ Berater | N:M | `@ManyToMany` + `@JoinTable` | Berater werden Projekten zugewiesen |
| Projekt → Zeiteintrag | 1:N | `@OneToMany(mappedBy="projekt")` | Zeitbuchungen pro Projekt |
| Berater → Zeiteintrag | 1:N | `@OneToMany(mappedBy="berater")` | Zeitbuchungen pro Berater |
| Berater → Skill | 1:N | `@OneToMany(mappedBy="berater")` | Skills eines Beraters |
| Skill → Berater | N:1 | `@ManyToOne` | Jeder Skill gehört zu einem Berater |

### Enums

**SapModul:**

| Wert | Bezeichnung |
|------|-------------|
| `FI` | Financial Accounting |
| `CO` | Controlling |
| `MM` | Materials Management |
| `SD` | Sales & Distribution |
| `PP` | Production Planning |
| `HR` | Human Resources |
| `BASIS` | SAP Basis / Administration |

**ProjektStatus** (mit Statusübergangs-Maschine):

```
  ┌──────────┐
  │ ANGEBOT  │
  └────┬─────┘
       │
  ┌────▼─────┐       ┌────────────┐
  │GENEHMIGT │       │ STORNIERT  │ ◄── (von ANGEBOT, GENEHMIGT oder AKTIV)
  └────┬─────┘       └────────────┘
       │
  ┌────▼─────┐
  │  AKTIV   │
  └────┬─────┘
       │
  ┌────▼──────────┐
  │ABGESCHLOSSEN  │
  └───────────────┘
```

**Erlaubte Übergänge:**
- ANGEBOT → GENEHMIGT | STORNIERT
- GENEHMIGT → AKTIV | STORNIERT
- AKTIV → ABGESCHLOSSEN | STORNIERT
- ABGESCHLOSSEN → (Endzustand)
- STORNIERT → (Endzustand)

**Berater.Senioritaet:**
- `JUNIOR`, `SENIOR`, `PRINCIPAL`, `PARTNER`

---

## 6. Use Cases

### UC-1: Kundenverwaltung
**Akteur:** Sachbearbeiter  
**Beschreibung:** Kunden (Firmenname, Branche, Ansprechpartner, Adresse, E-Mail) anlegen, bearbeiten, löschen und auflisten.  
**JEE-Technologien:** JPA (Entity `Kunde`, NamedQueries), CDI (`KundeService`), JSF (Kunden-XHTML)

### UC-2: Projektverwaltung mit Statusmaschine
**Akteur:** Projektmanager  
**Beschreibung:** Projekte anlegen (mit Kunde, SAP-Modul, Budget, Zeitraum), bearbeiten, löschen. Projektstatus über definierte Übergänge ändern (ANGEBOT → GENEHMIGT → AKTIV → ABGESCHLOSSEN; jederzeit STORNIERT).  
**Business-Regeln:**
- Nur gültige Statusübergänge erlaubt (Statusmaschine)
- Projekt muss einem Kunden zugeordnet sein

**JEE-Technologien:** JPA (Entity `Projekt`, `@Enumerated`, NamedQueries), CDI (`ProjektService` mit `validiereStatusuebergang()`), JSF, Bean Validation (`@NotNull`, `@Size`, `@Min`)

### UC-3: Berater einem Projekt zuweisen (Skill-Matching)
**Akteur:** Projektmanager  
**Beschreibung:** Einem Projekt werden verfügbare Berater zugewiesen. Dabei wird geprüft, ob der Berater den passenden SAP-Modul-Skill besitzt und verfügbar ist.  
**Business-Regeln:**
- Berater muss einen Skill für das SAP-Modul des Projekts haben
- Berater muss als verfügbar markiert sein

**JEE-Technologien:** JPA (`@ManyToMany`, `@JoinTable`), CDI (`ProjektService.beraterZuweisen()`), Bean Validation

### UC-4: Zeiterfassung auf Projekte
**Akteur:** Berater  
**Beschreibung:** Berater buchen Stunden auf zugewiesene, aktive Projekte. Buchungen enthalten Datum, Stunden, Tätigkeitsbeschreibung und Abrechenbar-Kennzeichen. Bei Budget-Überschreitung wird gewarnt.  
**Business-Regeln:**
- Zeitbuchung nur auf Projekte mit Status AKTIV
- Nur zugewiesene Berater dürfen buchen
- Budget-Warnung bei Überschreitung (nicht blockierend)

**JEE-Technologien:** JPA (`Zeiteintrag`-Entity, NamedQueries), CDI (`ZeiterfassungService`), JSF (Zeitbuchungsformular), Bean Validation (`@DecimalMin`, `@DecimalMax`)

### UC-5: Skill-/Kompetenzmanagement
**Akteur:** Personalverantwortlicher  
**Beschreibung:** Beratern werden SAP-Modul-Skills (mit Level 1–5 und Zertifizierungsstatus) zugewiesen. Existierende Skills können aktualisiert oder entfernt werden. Berater können nach SAP-Modul-Kompetenz gesucht werden.  
**JEE-Technologien:** JPA (`Skill`-Entity, `@OneToMany` mit `orphanRemoval`), CDI (`SkillService`), NamedQueries (`Berater.findBySapModul`)

### UC-6: Berater- und Ressourcenverwaltung
**Akteur:** Personalverantwortlicher  
**Beschreibung:** Berater (Vorname, Nachname, E-Mail, Seniorität, Stundensatz, Verfügbarkeit) anlegen, bearbeiten und auflisten. Verfügbare Berater filtern. Berater nach SAP-Modul-Kompetenz suchen.  
**JEE-Technologien:** JPA (Entity `Berater`, NamedQueries), CDI (`BeraterService`), JSF

### UC-7: Dashboard / Projektauslastung
**Akteur:** Projektmanager / Management  
**Beschreibung:** Übersicht über alle Projekte mit gebuchten Stunden, Budget-Auslastung in Prozent und abrechenbaren Stunden. Schnellübersicht über Projektstatus.  
**JEE-Technologien:** JPA (aggregierte Abfragen), JSF (Dashboard-Seite), CDI

---

## 7. JEE-Technologie-Mapping

Diese Tabelle zeigt, wie die Aufgabenanforderungen auf JEE-Technologien abgebildet werden:

| JEE-Technologie | Einsatz | Klassen/Dateien |
|---|---|---|
| **JPA 2.1** | 5 Entities, 2 Enums, `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@JoinTable`, `@NamedQuery`, `@Enumerated`, `@Temporal` | `model/*` |
| **CDI 2.0** | `@ApplicationScoped` Services, `@Named` + `@ViewScoped` Controller, `@Inject`, `@PostConstruct` | `service/*`, `controller/*` |
| **JSF 2.3** | Facelets-Templates, `h:dataTable`, `h:selectOneMenu`, `f:selectItems`, `f:convertDateTime`, Custom Converter | `*.xhtml`, `converter/*` |
| **Bean Validation** | `@NotNull`, `@Size`, `@Min`, `@Max`, `@DecimalMin`, `@DecimalMax`, `@Positive` | `model/*` |
| **JTA** | `@Transactional` auf Service-Methoden | `service/*` |
| **Servlet 3.1** | `web.xml` v3.1, `FacesServlet`-Mapping | `WEB-INF/web.xml` |

---

## 8. Projektstruktur (Eclipse Dynamic Web Project)

```
SapConsultingApp/                          ← Eclipse Dynamic Web Project
├── src/                                   ← Java Source Folder
│   └── de/
│       └── consulting/
│           ├── model/                     ← JPA Entities
│           │   ├── Kunde.java
│           │   ├── Projekt.java
│           │   ├── Berater.java
│           │   ├── Skill.java
│           │   ├── Zeiteintrag.java
│           │   ├── SapModul.java          ← Enum
│           │   └── ProjektStatus.java     ← Enum
│           │
│           ├── service/                   ← CDI Beans (Business-Logik)
│           │   ├── ProjektService.java
│           │   ├── BeraterService.java
│           │   ├── ZeiterfassungService.java
│           │   ├── SkillService.java
│           │   └── KundeService.java
│           │
│           ├── controller/                ← JSF Backing Beans
│           │   ├── ProjektController.java
│           │   ├── BeraterController.java
│           │   ├── ZeitController.java
│           │   └── DashboardController.java
│           │
│           └── converter/                 ← JSF Converter
│               ├── KundeConverter.java
│               └── BeraterConverter.java
│
├── WebContent/                            ← Web Root
│   ├── META-INF/
│   │   └── MANIFEST.MF
│   │
│   ├── WEB-INF/
│   │   ├── web.xml                        ← Servlet 3.1 Config
│   │   ├── beans.xml                      ← CDI Aktivierung (bean-discovery-mode="all")
│   │   ├── faces-config.xml               ← JSF 2.3 Config
│   │   └── classes/
│   │       └── META-INF/
│   │           └── persistence.xml        ← JPA 2.1 Config (SapConsultingPU)
│   │
│   ├── resources/
│   │   └── css/
│   │       └── style.css
│   │
│   ├── template.xhtml                     ← JSF Facelets Master-Template
│   ├── index.xhtml                        ← Dashboard
│   ├── projekte.xhtml                     ← Projektverwaltung
│   ├── projekt-detail.xhtml               ← Projekt bearbeiten + Berater zuweisen
│   ├── berater.xhtml                      ← Beraterübersicht
│   ├── berater-detail.xhtml               ← Berater + Skills verwalten
│   ├── kunden.xhtml                       ← Kundenverwaltung
│   └── zeitbuchung.xhtml                  ← Zeiterfassung
│
└── build/                                 ← Eclipse kompiliert automatisch hierhin
```

> **Kein Build-Tool:** Alle Abhängigkeiten kommen vom Payara-5.1-Server-Classpath. Es werden keine zusätzlichen JARs unter `WEB-INF/lib/` benötigt, da Payara 5.1 Full Profile JPA, JSF, CDI, Bean Validation etc. mitbringt.

---

## 9. Konfiguration

### persistence.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             version="2.1">
    <persistence-unit name="SapConsultingPU" transaction-type="JTA">
        <jta-data-source>jdbc/SapConsultingDS</jta-data-source>
        <class>de.consulting.model.Kunde</class>
        <class>de.consulting.model.Projekt</class>
        <class>de.consulting.model.Berater</class>
        <class>de.consulting.model.Skill</class>
        <class>de.consulting.model.Zeiteintrag</class>
        <properties>
            <property name="javax.persistence.schema-generation.database.action"
                      value="drop-and-create"/>
            <property name="eclipselink.logging.level" value="FINE"/>
            <property name="eclipselink.target-database" value="MySQL"/>
        </properties>
    </persistence-unit>
</persistence>
```

> ⚠️ `drop-and-create` nur für Entwicklung — bei Abgabe auf `create` oder `none` umstellen!

### JDBC (Payara Admin Console)

| Einstellung | Wert |
|---|---|
| **Connection Pool** | `SapConsultingPool` |
| Resource Type | `javax.sql.DataSource` |
| Database Vendor | MySQL |
| ServerName | `localhost` |
| PortNumber | `3306` |
| DatabaseName | `sap_consulting` |
| User | `consulting_app` |
| **JDBC Resource** | `jdbc/SapConsultingDS` → Pool: `SapConsultingPool` |

### MySQL-Datenbank

```sql
CREATE DATABASE sap_consulting
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER 'consulting_app'@'localhost' IDENTIFIED BY 'geheim123';
GRANT ALL PRIVILEGES ON sap_consulting.* TO 'consulting_app'@'localhost';
FLUSH PRIVILEGES;
```

---

## 10. Business-Regeln (Zusammenfassung)

| # | Regel | Ort |
|---|---|---|
| BR-1 | Nur zugewiesene Berater dürfen Zeiten auf ein Projekt buchen | `ZeiterfassungService.buchen()` |
| BR-2 | Zeitbuchung nur auf Projekte mit Status AKTIV | `ZeiterfassungService.buchen()` |
| BR-3 | Berater muss Skill für das SAP-Modul des Projekts besitzen | `ProjektService.beraterZuweisen()` |
| BR-4 | Berater muss als verfügbar markiert sein | `ProjektService.beraterZuweisen()` |
| BR-5 | Projektstatus folgt definierter Statusmaschine | `ProjektService.statusAendern()` |
| BR-6 | Budget-Warnung bei Stunden-Überschreitung | `ZeiterfassungService.buchen()` |
| BR-7 | Skill-Level zwischen 1 (Grundkenntnisse) und 5 (Experte) | `Skill` Entity, Bean Validation |

---

## 11. Deployment

1. **MySQL starten** und Datenbank `sap_consulting` anlegen
2. **MySQL Connector/J** nach `payara5/glassfish/domains/domain1/lib/` kopieren
3. **Payara 5.1** starten und JDBC Pool + Resource konfigurieren (Admin Console `:4848`)
4. **Eclipse:** Rechtsklick auf Projekt → *Run As → Run on Server → Payara 5.1*
5. **Browser:** `http://localhost:8080/SapConsultingApp/index.xhtml`

### Testfluss

```
Kunde anlegen → Berater anlegen + Skills zuweisen
     → Projekt anlegen (mit Kunde und SAP-Modul)
          → Berater dem Projekt zuweisen (Skill-Check)
               → Zeiten buchen (Validierung)
                    → Auslastung im Dashboard prüfen
```

---

## 12. Abdeckung der Aufgabenanforderungen

| Anforderung (Aufgabenstellung) | Abgedeckt durch |
|---|---|
| JPA 2.1 | 5 Entities, `@OneToMany`, `@ManyToMany`, `@ManyToOne`, NamedQueries, Bean Validation |
| JSF (**Schwerpunkt**) | Facelets-Templates, `h:dataTable`, `h:selectOneMenu`, `f:selectItems`, `f:convertDateTime`, Custom Converter |
| CDI (**Schwerpunkt**) | `@Named`, `@ViewScoped`, `@ApplicationScoped`, `@Inject`, `@PostConstruct` |
| Servlet 3.1 | `web.xml` v3.1, FacesServlet-Mapping |
| Persistenzschicht | JPA + MySQL 8.0, `persistence.xml` v2.1, EclipseLink |
| Anwendungsschicht | 5 Service-Klassen mit Business-Logik (Validierung, Statusmaschine, Skill-Matching) |
| Präsentationsschicht (minimal) | 7–8 XHTML-Seiten, Master-Template, CSS |
| Realer Unternehmenskontext | SAP-Beratungsfirma: Kunden, Projekte, Berater, Skills, Zeiterfassung |
| Mind. 5 Use Cases | 7 Use Cases definiert |
| Kein Build-Tool | Eclipse Dynamic Web Project, Payara-Classpath |
| GlassFish/Payara 5.1 | Server-Konfiguration + JDBC Pool |
| Java 1.8 | Keine Java 9+ Features, for-Schleifen statt Streams wo nötig |
