package de.consulting.cdi;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Sitzungs-scoped CDI-Bean fuer einmalig anzuzeigende Statusmeldungen (Flash Messages).
 * Servlets setzen eine Nachricht vor dem PRG-Redirect; die JSF-View liest und loescht sie.
 */
@Named
@SessionScoped
public class FlashMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nachricht;
    private String typ = "info";

    /**
     * Setzt eine neue Flash-Nachricht.
     *
     * @param nachricht Anzuzeigende Meldung
     * @param typ       Darstellungstyp: "info", "warn" oder "error"
     */
    public void setze(String nachricht, String typ) {
        this.nachricht = nachricht;
        this.typ = typ;
    }

    /** Gibt an, ob eine ungelesene Nachricht vorliegt. */
    public boolean isVorhanden() {
        return nachricht != null && !nachricht.isEmpty();
    }

    /**
     * Liefert den Nachrichtentext und loescht ihn danach (einmaliges Anzeigen).
     */
    public String getNachricht() {
        String msg = nachricht != null ? nachricht : "";
        nachricht = null;
        return msg;
    }

    public String getTyp() {
        return typ != null ? typ : "info";
    }
}
