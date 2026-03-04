package de.consulting.cdi;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.consulting.model.Berater;
import de.consulting.model.ProjektStatus;
import de.consulting.model.SapModul;

/**
 * Anwendungsweit gueltige Konfigurationsdaten fuer JSF-Views.
 * Stellt Enum-Werte bereit, sodass Servlets diese nicht mehr als Request-Attribute setzen muessen.
 */
@Named("app")
@ApplicationScoped
public class AppKonfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Alle verfuegbaren SAP-Module. */
    public SapModul[] getSapModule() {
        return SapModul.values();
    }

    /** Alle definierten Projektstatus-Werte. */
    public ProjektStatus[] getStatusWerte() {
        return ProjektStatus.values();
    }

    /** Alle Berater-Senioritaetsstufen. */
    public Berater.Senioritaet[] getSenioritaetWerte() {
        return Berater.Senioritaet.values();
    }
}
