-- =============================================
-- SAP Consulting App - MySQL Setup Script
-- =============================================

-- Datenbank anlegen
CREATE DATABASE IF NOT EXISTS sap_consulting
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Benutzer anlegen und Rechte vergeben
CREATE USER IF NOT EXISTS 'consulting_app'@'localhost' IDENTIFIED BY 'geheim123';
GRANT ALL PRIVILEGES ON sap_consulting.* TO 'consulting_app'@'localhost';
FLUSH PRIVILEGES;

USE sap_consulting;

-- =============================================
-- Tabellen (werden normalerweise von JPA generiert,
-- hier als Referenz / Backup)
-- =============================================

CREATE TABLE IF NOT EXISTS kunde (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firmenname VARCHAR(100) NOT NULL,
    branche VARCHAR(50),
    ansprechpartner VARCHAR(100),
    adresse VARCHAR(200),
    email VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS berater (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vorname VARCHAR(50) NOT NULL,
    nachname VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    senioritaet VARCHAR(20) NOT NULL,
    stundensatz DECIMAL(8,2) NOT NULL,
    verfuegbar BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS projekt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bezeichnung VARCHAR(150) NOT NULL,
    beschreibung VARCHAR(500),
    sap_modul VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ANGEBOT',
    start_datum DATE,
    end_datum DATE,
    budget_stunden INT DEFAULT 0,
    budget_euro DECIMAL(10,2),
    kunde_id BIGINT NOT NULL,
    FOREIGN KEY (kunde_id) REFERENCES kunde(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sap_modul VARCHAR(10) NOT NULL,
    skill_level INT NOT NULL,
    zertifiziert BOOLEAN NOT NULL DEFAULT FALSE,
    berater_id BIGINT NOT NULL,
    FOREIGN KEY (berater_id) REFERENCES berater(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS zeiteintrag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    datum DATE NOT NULL,
    stunden DECIMAL(5,2) NOT NULL,
    beschreibung VARCHAR(300),
    abrechenbar BOOLEAN NOT NULL DEFAULT TRUE,
    projekt_id BIGINT NOT NULL,
    berater_id BIGINT NOT NULL,
    FOREIGN KEY (projekt_id) REFERENCES projekt(id),
    FOREIGN KEY (berater_id) REFERENCES berater(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS projekt_berater (
    projekt_id BIGINT NOT NULL,
    berater_id BIGINT NOT NULL,
    PRIMARY KEY (projekt_id, berater_id),
    FOREIGN KEY (projekt_id) REFERENCES projekt(id),
    FOREIGN KEY (berater_id) REFERENCES berater(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Testdaten
-- =============================================

INSERT INTO kunde (firmenname, branche, ansprechpartner, adresse, email) VALUES
('Bosch GmbH', 'Automotive', 'Hans Mueller', 'Stuttgart', 'h.mueller@bosch.de'),
('Siemens AG', 'Industrie', 'Anna Schmidt', 'Muenchen', 'a.schmidt@siemens.de'),
('BASF SE', 'Chemie', 'Peter Weber', 'Ludwigshafen', 'p.weber@basf.de');

INSERT INTO berater (vorname, nachname, email, senioritaet, stundensatz, verfuegbar) VALUES
('Max', 'Mustermann', 'max@consulting.de', 'SENIOR', 150.00, TRUE),
('Laura', 'Wolf', 'laura@consulting.de', 'PRINCIPAL', 180.00, TRUE),
('Tim', 'Berger', 'tim@consulting.de', 'JUNIOR', 100.00, TRUE);

INSERT INTO skill (sap_modul, skill_level, zertifiziert, berater_id) VALUES
('FI', 4, TRUE, 1),
('CO', 3, FALSE, 1),
('MM', 5, TRUE, 2),
('SD', 4, TRUE, 2),
('FI', 2, FALSE, 3);

INSERT INTO projekt (bezeichnung, beschreibung, sap_modul, status, start_datum, end_datum, budget_stunden, budget_euro, kunde_id) VALUES
('SAP FI Einfuehrung', 'Einfuehrung des SAP FI Moduls bei Bosch', 'FI', 'AKTIV', '2026-01-01', '2026-06-30', 500, 75000.00, 1),
('SAP MM Optimierung', 'Optimierung der MM-Prozesse bei Siemens', 'MM', 'ANGEBOT', '2026-03-01', '2026-09-30', 300, 54000.00, 2);

INSERT INTO projekt_berater (projekt_id, berater_id) VALUES (1, 1);

INSERT INTO zeiteintrag (datum, stunden, beschreibung, abrechenbar, projekt_id, berater_id) VALUES
('2026-01-15', 8.00, 'Kickoff-Meeting und Anforderungsanalyse', TRUE, 1, 1),
('2026-01-16', 6.50, 'Customizing FI-Grunddaten', TRUE, 1, 1),
('2026-01-17', 4.00, 'Interne Abstimmung', FALSE, 1, 1);
