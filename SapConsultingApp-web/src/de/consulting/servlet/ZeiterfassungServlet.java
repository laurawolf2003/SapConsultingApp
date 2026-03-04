package de.consulting.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.model.Berater;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.Zeiteintrag;
import de.consulting.service.BeraterService;
import de.consulting.service.ProjektService;
import de.consulting.service.ZeiterfassungService;

@WebServlet("/zeitbuchung")
public class ZeiterfassungServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/ZeiterfassungService!de.consulting.service.ZeiterfassungService")
    private ZeiterfassungService zeitService;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/ProjektService!de.consulting.service.ProjektService")
    private ProjektService projektService;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/BeraterService!de.consulting.service.BeraterService")
    private BeraterService beraterService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ServletUtil.ladFlashMsg(req);

        req.setAttribute("aktiveProjekte", projektService.findByStatus(ProjektStatus.AKTIV));
        req.setAttribute("alleBerater", beraterService.alleBerater());

        // Zeiteintraege fuer ausgewaehltes Projekt laden
        String projektIdParam = req.getParameter("projektId");
        if (projektIdParam != null && !projektIdParam.isEmpty()) {
            try {
                Long projektId = Long.parseLong(projektIdParam);
                req.setAttribute("zeiteintraege", zeitService.findByProjekt(projektId));
                req.setAttribute("ausgewaehltesProjektId", projektId);
            } catch (NumberFormatException e) {
                req.setAttribute("zeiteintraege", Collections.emptyList());
            }
        } else {
            req.setAttribute("zeiteintraege", Collections.emptyList());
        }

        req.getRequestDispatcher("/zeitbuchung.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String projektIdParam = req.getParameter("projektId");

        try {
            Zeiteintrag eintrag = new Zeiteintrag();

            Projekt projekt = new Projekt();
            projekt.setId(Long.parseLong(projektIdParam));
            eintrag.setProjekt(projekt);

            Berater berater = new Berater();
            berater.setId(Long.parseLong(req.getParameter("beraterId")));
            eintrag.setBerater(berater);

            Date datum = new SimpleDateFormat("dd.MM.yyyy").parse(req.getParameter("datum"));
            eintrag.setDatum(datum);
            eintrag.setStunden(new BigDecimal(req.getParameter("stunden").replace(",", ".")));
            eintrag.setBeschreibung(req.getParameter("beschreibung"));
            eintrag.setAbrechenbar("on".equals(req.getParameter("abrechenbar")));

            String warnung = zeitService.buchen(eintrag);
            if (warnung != null) {
                ServletUtil.setFlashMsg(req, warnung, "warn");
            } else {
                ServletUtil.setFlashMsg(req, "Zeiteintrag gebucht.", "info");
            }
        } catch (Exception e) {
            ServletUtil.setFlashMsg(req, "Fehler: " + e.getMessage(), "error");
        }

        String redirect = req.getContextPath() + "/zeitbuchung";
        if (projektIdParam != null && !projektIdParam.isEmpty()) {
            redirect += "?projektId=" + projektIdParam;
        }
        resp.sendRedirect(redirect);
    }
}
