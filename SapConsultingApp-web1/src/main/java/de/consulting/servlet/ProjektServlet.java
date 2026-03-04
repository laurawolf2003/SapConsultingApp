package de.consulting.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.cdi.FlashMessage;
import de.consulting.model.Kunde;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.SapModul;
import de.consulting.service.BeraterService;
import de.consulting.service.KundeService;
import de.consulting.service.ProjektService;

@WebServlet("/projekte")
public class ProjektServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/ProjektService!de.consulting.service.ProjektService")
    private ProjektService projektService;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/KundeService!de.consulting.service.KundeService")
    private KundeService kundeService;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/BeraterService!de.consulting.service.BeraterService")
    private BeraterService beraterService;

    @Inject
    private FlashMessage flashMessage;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        req.setAttribute("projekte", projektService.alleProjekte());
        req.setAttribute("alleKunden", kundeService.alleKunden());

        // Projekt zum Bearbeiten laden (optionaler id-Parameter)
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                req.setAttribute("formProjekt", projektService.findById(Long.parseLong(idParam)));
            } catch (NumberFormatException e) {
                req.setAttribute("formProjekt", new Projekt());
            }
        } else {
            req.setAttribute("formProjekt", new Projekt());
        }

        req.getRequestDispatcher("/projekte.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("loeschen".equals(action)) {
                projektService.loeschen(Long.parseLong(req.getParameter("id")));
                flashMessage.setze("Projekt geloescht.", "info");

            } else if ("speichern".equals(action)) {
                Projekt projekt = new Projekt();
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    projekt = projektService.findById(Long.parseLong(idParam));
                }
                projekt.setBezeichnung(req.getParameter("bezeichnung"));
                projekt.setBeschreibung(req.getParameter("beschreibung"));
                projekt.setSapModul(SapModul.valueOf(req.getParameter("sapModul")));

                String kundeIdParam = req.getParameter("kundeId");
                if (kundeIdParam != null && !kundeIdParam.isEmpty()) {
                    Kunde kunde = kundeService.findById(Long.parseLong(kundeIdParam));
                    projekt.setKunde(kunde);
                }

                String budgetStundenParam = req.getParameter("budgetStunden");
                if (budgetStundenParam != null && !budgetStundenParam.isEmpty()) {
                    projekt.setBudgetStunden(Integer.parseInt(budgetStundenParam));
                }

                String budgetEuroParam = req.getParameter("budgetEuro");
                if (budgetEuroParam != null && !budgetEuroParam.isEmpty()) {
                    projekt.setBudgetEuro(new BigDecimal(budgetEuroParam.replace(",", ".")));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String startDatumParam = req.getParameter("startDatum");
                if (startDatumParam != null && !startDatumParam.isEmpty()) {
                    projekt.setStartDatum(sdf.parse(startDatumParam));
                }
                String endDatumParam = req.getParameter("endDatum");
                if (endDatumParam != null && !endDatumParam.isEmpty()) {
                    projekt.setEndDatum(sdf.parse(endDatumParam));
                }

                projektService.speichern(projekt);
                flashMessage.setze("Projekt gespeichert.", "info");
            }
        } catch (Exception e) {
            flashMessage.setze("Fehler: " + e.getMessage(), "error");
        }

        resp.sendRedirect(req.getContextPath() + "/projekte");
    }
}
