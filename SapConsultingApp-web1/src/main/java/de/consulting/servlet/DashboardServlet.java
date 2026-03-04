package de.consulting.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.cdi.FlashMessage;
import de.consulting.model.Projekt;
import de.consulting.service.ProjektService;
import de.consulting.service.ZeiterfassungService;

/** Einfaches Daten-Objekt fuer Projekt-Statistiken auf dem Dashboard. */
class ProjektStatistik {
    private final Projekt projekt;
    private final BigDecimal gebuchteStunden;
    private final BigDecimal abrechenbareStunden;
    private final BigDecimal auslastungProzent;

    ProjektStatistik(Projekt projekt, BigDecimal gebucht, BigDecimal abrechenbar) {
        this.projekt = projekt;
        this.gebuchteStunden = gebucht;
        this.abrechenbareStunden = abrechenbar;
        if (projekt.getBudgetStunden() > 0) {
            this.auslastungProzent = gebucht.multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(projekt.getBudgetStunden()), 1, RoundingMode.HALF_UP);
        } else {
            this.auslastungProzent = BigDecimal.ZERO;
        }
    }

    public Projekt getProjekt() { return projekt; }
    public BigDecimal getGebuchteStunden() { return gebuchteStunden; }
    public BigDecimal getAbrechenbareStunden() { return abrechenbareStunden; }
    public BigDecimal getAuslastungProzent() { return auslastungProzent; }
}

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/ProjektService!de.consulting.service.ProjektService")
    private ProjektService projektService;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/ZeiterfassungService!de.consulting.service.ZeiterfassungService")
    private ZeiterfassungService zeitService;

    @Inject
    private FlashMessage flashMessage;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Projekt> projekte = projektService.alleProjekte();
        List<ProjektStatistik> statistiken = new ArrayList<ProjektStatistik>();

        for (Projekt p : projekte) {
            BigDecimal gebucht = zeitService.gesamtStundenByProjekt(p.getId());
            BigDecimal abrechenbar = zeitService.abrechenbareStundenByProjekt(p.getId());
            statistiken.add(new ProjektStatistik(p, gebucht, abrechenbar));
        }

        req.setAttribute("projektStatistiken", statistiken);
        req.getRequestDispatcher("/index.xhtml").forward(req, resp);
    }
}
