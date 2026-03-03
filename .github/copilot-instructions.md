# Copilot Instructions — JEEAbgabe

## Project Overview

University assignment (IU Internationale Hochschule, WS 2025/26) building a **Jakarta Enterprise Edition (JEE)** application. The app models core business structures and processes of a real company (practice partner) as a case study, implementing at least **5 use cases** from the company's operations.

**Architecture priorities** (per assignment):
- **Application & persistence layers** are the focus — use EJBs, JPA, CDI, etc.
- **Presentation layer** uses the Servlet API — keep it minimal, just enough for end-user interaction.
- All JEE middle- and back-end technologies covered in the course must be demonstrated.

## Tech Stack

| Component | Version / Tool |
|---|---|
| Java | JDK 1.8 (Java 8) |
| Platform | Jakarta EE (Java EE 8) |
| App Server | GlassFish 5.1 / Payara |
| IDE | Eclipse IDE for Enterprise Java Developers |
| Persistence | JPA with a relational database |
| Frontend | Servlets (+ JSP if needed) |

## Build & Run

**No build tools (Maven, Gradle, Ant, etc.)** — all building, deploying, and testing is done through Eclipse IDE functionality. The project uses Eclipse's built-in compiler and server adapters.

- Build: Eclipse auto-compiles on save.
- Deploy: Right-click project → Run on Server (GlassFish/Payara configured in Eclipse).
- Dependencies: Add JARs manually to `WEB-INF/lib/` or reference server runtime libraries in Eclipse build path.
- Do **not** generate `pom.xml`, `build.gradle`, or similar build-tool config files.

## Key Conventions

- **Language**: Code and commit messages may be in German (university context).
- **Java 8 only**: Do not use Java 9+ features (var, modules, records, etc.). The target runtime is GlassFish 5.1 which requires Java 8.
- **JEE patterns**: Use `@Stateless`, `@Stateful`, `@Singleton` EJBs, `@Entity` JPA entities, `@Inject` CDI, and Servlet API — not Spring.
- **Persistence**: Define entities with JPA annotations. Use `persistence.xml` for the persistence unit configuration.
- **Assignment docs** are in `doc/` — refer to them for requirements and setup instructions.
