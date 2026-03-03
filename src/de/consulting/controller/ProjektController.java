package de.consulting.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.consulting.model.Berater;
import de.consulting.model.Kunde;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.SapModul;
import de.consulting.service.BeraterService;
import de.consulting.service.KundeService;
import de.consulting.service.ProjektService;

@Named
@ViewScoped
public class ProjektController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjektService projektService;

    @Inject
    private KundeService kundeService;

    @Inject
    private BeraterService beraterService;

    private List<Projekt> projekte;
    private Projekt ausgewaehltesProjekt;
    private Long ausgewaehlterBeraterId;
    private String neuerStatus;

    @PostConstruct
    public void init() {
        projekte = projektService.alleProjekte();
        ausgewaehltesProjekt = new Projekt();
    }

    // ----- CRUD -----

    public String speichern() {
        try {
            projektService.speichern(ausgewaehltesProjekt);
            projekte = projektService.alleProjekte();
            ausgewaehltesProjekt = new Projekt();
            addMessage(FacesMessage.SEVERITY_INFO, "Projekt gespeichert.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public String loeschen(Long id) {
        projektService.loeschen(id);
        projekte = projektService.alleProjekte();
        addMessage(FacesMessage.SEVERITY_INFO, "Projekt geloescht.");
        return null;
    }

    public void bearbeiten(Projekt projekt) {
        this.ausgewaehltesProjekt = projekt;
    }

    public void neuesProjekt() {
        this.ausgewaehltesProjekt = new Projekt();
    }

    // ----- Status -----

    public String statusAendern(Long projektId, String neuerStatusStr) {
        try {
            ProjektStatus neuerStatus = ProjektStatus.valueOf(neuerStatusStr);
            projektService.statusAendern(projektId, neuerStatus);
            projekte = projektService.alleProjekte();
            addMessage(FacesMessage.SEVERITY_INFO, "Status geaendert.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    // ----- Berater-Zuweisung -----

    public String beraterZuweisen() {
        try {
            if (ausgewaehltesProjekt.getId() != null && ausgewaehlterBeraterId != null) {
                projektService.beraterZuweisen(ausgewaehltesProjekt.getId(), ausgewaehlterBeraterId);
                ausgewaehltesProjekt = projektService.findById(ausgewaehltesProjekt.getId());
                addMessage(FacesMessage.SEVERITY_INFO, "Berater zugewiesen.");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public String beraterEntfernen(Long beraterId) {
        try {
            projektService.beraterEntfernen(ausgewaehltesProjekt.getId(), beraterId);
            ausgewaehltesProjekt = projektService.findById(ausgewaehltesProjekt.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Berater entfernt.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    // ----- Getter / Setter -----

    public List<Projekt> getProjekte() {
        return projekte;
    }

    public Projekt getAusgewaehltesProjekt() {
        return ausgewaehltesProjekt;
    }

    public void setAusgewaehltesProjekt(Projekt ausgewaehltesProjekt) {
        this.ausgewaehltesProjekt = ausgewaehltesProjekt;
    }

    public Long getAusgewaehlterBeraterId() {
        return ausgewaehlterBeraterId;
    }

    public void setAusgewaehlterBeraterId(Long ausgewaehlterBeraterId) {
        this.ausgewaehlterBeraterId = ausgewaehlterBeraterId;
    }

    public String getNeuerStatus() {
        return neuerStatus;
    }

    public void setNeuerStatus(String neuerStatus) {
        this.neuerStatus = neuerStatus;
    }

    public List<Kunde> getAlleKunden() {
        return kundeService.alleKunden();
    }

    public SapModul[] getSapModule() {
        return SapModul.values();
    }

    public ProjektStatus[] getProjektStatusWerte() {
        return ProjektStatus.values();
    }

    public List<Berater> getVerfuegbareBerater() {
        return beraterService.verfuegbareBerater();
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }
}
