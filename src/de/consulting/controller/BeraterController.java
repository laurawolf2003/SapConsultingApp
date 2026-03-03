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
import de.consulting.model.SapModul;
import de.consulting.model.Skill;
import de.consulting.service.BeraterService;
import de.consulting.service.SkillService;

@Named
@ViewScoped
public class BeraterController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private BeraterService beraterService;

    @Inject
    private SkillService skillService;

    private List<Berater> beraterListe;
    private Berater ausgewaehlterBerater;

    // Felder fuer Skill-Zuweisung
    private SapModul neuerSkillModul;
    private int neuerSkillLevel = 3;
    private boolean neuerSkillZertifiziert = false;

    @PostConstruct
    public void init() {
        beraterListe = beraterService.alleBerater();
        ausgewaehlterBerater = new Berater();
    }

    // ----- CRUD -----

    public String speichern() {
        try {
            beraterService.speichern(ausgewaehlterBerater);
            beraterListe = beraterService.alleBerater();
            ausgewaehlterBerater = new Berater();
            addMessage(FacesMessage.SEVERITY_INFO, "Berater gespeichert.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public String loeschen(Long id) {
        beraterService.loeschen(id);
        beraterListe = beraterService.alleBerater();
        addMessage(FacesMessage.SEVERITY_INFO, "Berater geloescht.");
        return null;
    }

    public void bearbeiten(Berater berater) {
        this.ausgewaehlterBerater = berater;
    }

    public void neuerBerater() {
        this.ausgewaehlterBerater = new Berater();
    }

    // ----- Skills -----

    public String skillZuweisen() {
        try {
            if (ausgewaehlterBerater.getId() != null && neuerSkillModul != null) {
                skillService.skillZuweisen(ausgewaehlterBerater.getId(),
                        neuerSkillModul, neuerSkillLevel, neuerSkillZertifiziert);
                ausgewaehlterBerater = beraterService.findById(ausgewaehlterBerater.getId());
                addMessage(FacesMessage.SEVERITY_INFO, "Skill zugewiesen.");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    public String skillEntfernen(Long skillId) {
        try {
            skillService.skillEntfernen(skillId);
            if (ausgewaehlterBerater.getId() != null) {
                ausgewaehlterBerater = beraterService.findById(ausgewaehlterBerater.getId());
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Skill entfernt.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
        }
        return null;
    }

    // ----- Getter / Setter -----

    public List<Berater> getBeraterListe() {
        return beraterListe;
    }

    public Berater getAusgewaehlterBerater() {
        return ausgewaehlterBerater;
    }

    public void setAusgewaehlterBerater(Berater ausgewaehlterBerater) {
        this.ausgewaehlterBerater = ausgewaehlterBerater;
    }

    public Berater.Senioritaet[] getSenioritaetWerte() {
        return Berater.Senioritaet.values();
    }

    public SapModul[] getSapModule() {
        return SapModul.values();
    }

    public SapModul getNeuerSkillModul() {
        return neuerSkillModul;
    }

    public void setNeuerSkillModul(SapModul neuerSkillModul) {
        this.neuerSkillModul = neuerSkillModul;
    }

    public int getNeuerSkillLevel() {
        return neuerSkillLevel;
    }

    public void setNeuerSkillLevel(int neuerSkillLevel) {
        this.neuerSkillLevel = neuerSkillLevel;
    }

    public boolean isNeuerSkillZertifiziert() {
        return neuerSkillZertifiziert;
    }

    public void setNeuerSkillZertifiziert(boolean neuerSkillZertifiziert) {
        this.neuerSkillZertifiziert = neuerSkillZertifiziert;
    }

    private void addMessage(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }
}
