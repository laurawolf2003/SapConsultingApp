package de.consulting.service;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Singleton EJB fuer anwendungsweite Konfigurationsparameter.
 * Wird beim Start des Applikationsservers einmalig instanziiert (@Startup).
 * Andere EJBs koennen diese Bean per @EJB injizieren.
 */
@Singleton
@Startup
public class SystemKonfiguration {

    private static final Logger LOG = Logger.getLogger(SystemKonfiguration.class.getName());

    /** Maximale buchbare Arbeitsstunden pro Tag und Berater (BR-7 Erweiterung). */
    private final int maxStundenProTag = 10;

    /**
     * Schwellenwert in Prozent: Ab dieser Budget-Auslastung wird eine Warnung ausgegeben (BR-6).
     */
    private final int budgetWarnungSchwellenwert = 90;

    @PostConstruct
    public void initialisieren() {
        LOG.info("SAP Consulting App erfolgreich gestartet. "
                + "Konfiguration: maxStundenProTag=" + maxStundenProTag
                + ", budgetWarnungSchwellenwert=" + budgetWarnungSchwellenwert + "%");
    }

    public int getMaxStundenProTag() {
        return maxStundenProTag;
    }

    public int getBudgetWarnungSchwellenwert() {
        return budgetWarnungSchwellenwert;
    }
}
