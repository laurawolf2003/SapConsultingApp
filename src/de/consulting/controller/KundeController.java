package de.consulting.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.consulting.model.Kunde;
import de.consulting.service.KundeService;

@Named
@ViewScoped
public class KundeController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private KundeService kundeService;

    private List<Kunde> kunden;
    private Kunde ausgewaehlterKunde;

    @PostConstruct
    public void init() {
        kunden = kundeService.alleKunden();
        ausgewaehlterKunde = new Kunde();
    }

    public String speichern() {
        try {
            kundeService.speichern(ausgewaehlterKunde);
            kunden = kundeService.alleKunden();
            ausgewaehlterKunde = new Kunde();
            addMessage(FacesMessage.SEVERITY_INFO, "Kunde gespeichert.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public String loeschen(Long id) {
        kundeService.loeschen(id);
        kunden = kundeService.alleKunden();
        addMessage(FacesMessage.SEVERITY_INFO, "Kunde geloescht.");
        return null;
    }

    public void bearbeiten(Kunde kunde) {
        this.ausgewaehlterKunde = kunde;
    }

    public void neuerKunde() {
        this.ausgewaehlterKunde = new Kunde();
    }

    // ----- Getter / Setter -----

    public List<Kunde> getKunden() {
        return kunden;
    }

    public Kunde getAusgewaehlterKunde() {
        return ausgewaehlterKunde;
    }

    public void setAusgewaehlterKunde(Kunde ausgewaehlterKunde) {
        this.ausgewaehlterKunde = ausgewaehlterKunde;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }
}
