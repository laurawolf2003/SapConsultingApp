package de.consulting.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.consulting.model.Projekt;
import de.consulting.service.ProjektService;
import de.consulting.service.ZeiterfassungService;

@Named
@ViewScoped
public class DashboardController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjektService projektService;

    @Inject
    private ZeiterfassungService zeitService;

    private List<Projekt> projekte;

    @PostConstruct
    public void init() {
        projekte = projektService.alleProjekte();
    }

    public List<Projekt> getProjekte() {
        return projekte;
    }

    /** Gebuchte Gesamtstunden fuer ein Projekt. */
    public BigDecimal getGebuchteStunden(Projekt projekt) {
        return zeitService.gesamtStundenByProjekt(projekt.getId());
    }

    /** Abrechenbare Stunden fuer ein Projekt. */
    public BigDecimal getAbrechenbareStunden(Projekt projekt) {
        return zeitService.abrechenbareStundenByProjekt(projekt.getId());
    }

    /** Budget-Auslastung in Prozent (0 falls kein Budget definiert). */
    public BigDecimal getAuslastungProzent(Projekt projekt) {
        if (projekt.getBudgetStunden() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal gebucht = zeitService.gesamtStundenByProjekt(projekt.getId());
        return gebucht.multiply(new BigDecimal("100"))
                      .divide(new BigDecimal(projekt.getBudgetStunden()), 1, RoundingMode.HALF_UP);
    }
}
