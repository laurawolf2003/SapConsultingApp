package de.consulting.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.consulting.model.Berater;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.Zeiteintrag;
import de.consulting.service.BeraterService;
import de.consulting.service.ProjektService;
import de.consulting.service.ZeiterfassungService;

@Named
@ViewScoped
public class ZeitController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ZeiterfassungService zeitService;

    @Inject
    private ProjektService projektService;

    @Inject
    private BeraterService beraterService;

    private Zeiteintrag neuerEintrag;
    private List<Zeiteintrag> zeiteintraege;
    private Long ausgewaehltesProjektId;

    @PostConstruct
    public void init() {
        neuerEintrag = new Zeiteintrag();
        zeiteintraege = new ArrayList<Zeiteintrag>();
    }

    public String buchen() {
        try {
            String warnung = zeitService.buchen(neuerEintrag);
            if (warnung != null) {
                addMessage(FacesMessage.SEVERITY_WARN, warnung);
            } else {
                addMessage(FacesMessage.SEVERITY_INFO, "Zeiteintrag gebucht.");
            }
            // Liste aktualisieren
            if (ausgewaehltesProjektId != null) {
                zeiteintraege = zeitService.findByProjekt(ausgewaehltesProjektId);
            }
            neuerEintrag = new Zeiteintrag();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public void projektGewaehlt() {
        if (ausgewaehltesProjektId != null) {
            zeiteintraege = zeitService.findByProjekt(ausgewaehltesProjektId);
        } else {
            zeiteintraege = new ArrayList<Zeiteintrag>();
        }
    }

    // ----- Getter / Setter -----

    public Zeiteintrag getNeuerEintrag() {
        return neuerEintrag;
    }

    public void setNeuerEintrag(Zeiteintrag neuerEintrag) {
        this.neuerEintrag = neuerEintrag;
    }

    public List<Zeiteintrag> getZeiteintraege() {
        return zeiteintraege;
    }

    public Long getAusgewaehltesProjektId() {
        return ausgewaehltesProjektId;
    }

    public void setAusgewaehltesProjektId(Long ausgewaehltesProjektId) {
        this.ausgewaehltesProjektId = ausgewaehltesProjektId;
    }

    /** Nur aktive Projekte fuer die Zeitbuchung anzeigen. */
    public List<Projekt> getAktiveProjekte() {
        return projektService.findByStatus(ProjektStatus.AKTIV);
    }

    public List<Berater> getAlleBerater() {
        return beraterService.alleBerater();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }
}
