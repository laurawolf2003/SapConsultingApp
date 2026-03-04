# SapConsultingApp

JEE-Anwendung (Java EE 8) für die IU-Hausarbeit – modelliert Geschäftsprozesse eines SAP-Beratungsunternehmens.

## Voraussetzungen

- **Java JDK 1.8** (Java 8)
- **Eclipse IDE for Enterprise Java Developers**
- **GlassFish 5.1 / Payara** – Installation laut Aufgabenstellung im Root-Verzeichnis (Windows: `C:\`)
- **Payara Tools 2.3** – Eclipse-Plugin (muss in Version **2.3** installiert werden, da neuere Versionen GlassFish 5.1 nicht mehr erkennen). Eine Installationsquelle liegt im Repo unter `tools/payara-tools-2.3/`.

## Projektstruktur

| Projekt | Typ | Beschreibung |
|---|---|---|
| `SapConsultingApp1` | EAR | Enterprise Application – bündelt EJB + Web |
| `SapConsultingApp-ejb1` | EJB | Entities und Session Beans (JPA, EJB) |
| `SapConsultingApp-web1` | Web | Servlets und Frontend (Servlet API, JSF) |

## Einrichtung nach dem Klonen

### 1. Server-Runtime anlegen

Die Projekt-Konfiguration referenziert eine Runtime mit dem Namen **„Payara"**. Diese muss lokal angelegt werden:

1. **Window → Preferences → Server → Runtime Environments → Add…**
2. **Payara / GlassFish** auswählen
3. Installationspfad angeben (z.B. `C:\glassfish5`)
4. **Wichtig:** Den Namen auf **„Payara"** setzen (muss exakt so heißen, damit `.classpath` korrekt aufgelöst wird)
5. **Finish**

> **Hintergrund:** Die `.classpath`-Dateien im Repo enthalten nur den logischen Namen `Payara` als Referenz – keine absoluten Pfade. Eclipse löst diesen Namen über die lokale Runtime-Konfiguration auf.

### 2. Server-Instanz erstellen

Damit der Server im Servers-Tab erscheint und man deployen kann:

1. **Window → Show View → Servers** (öffnet den Servers-Tab)
2. Im Servers-Tab: **Rechtsklick → New → Server**
3. **Payara / GlassFish** auswählen, die zuvor erstellte Runtime zuweisen
4. **Finish**

### 3. Projekte importieren

1. **File → Import → General → Existing Projects into Workspace**
2. Root-Verzeichnis des Repos auswählen
3. Eclipse erkennt alle drei Projekte automatisch (EAR, EJB, Web)
4. **Finish**

### 4. Deployen und Starten

1. Im Servers-Tab: **Rechtsklick auf den Server → Add and Remove…**
2. `SapConsultingApp1` (EAR) hinzufügen – das zieht EJB und Web automatisch mit
3. **Server starten** (Rechtsklick → Start)
4. Anwendung im Browser unter `http://localhost:8080/SapConsultingApp/` aufrufen

## Hinweise

- **Kein Build-Tool:** Das Projekt verwendet weder Maven noch Gradle. Eclipse kompiliert automatisch beim Speichern.
- **Server-Instanz nicht im Repo:** Die Server-Instanz (`servers.xml`) enthält absolute Pfade und liegt in `.metadata/` – sie ist bewusst nicht versioniert. Jeder Entwickler erstellt sie lokal (Schritt 2).
- **Runtime-Name muss stimmen:** Wenn die Runtime nicht exakt „Payara" heißt, zeigt Eclipse Classpath-Fehler an.
