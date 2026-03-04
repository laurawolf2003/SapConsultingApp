package de.consulting.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.cdi.FlashMessage;
import de.consulting.model.Berater;
import de.consulting.service.BeraterService;

@WebServlet("/berater")
public class BeraterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/BeraterService!de.consulting.service.BeraterService")
    private BeraterService beraterService;

    @Inject
    private FlashMessage flashMessage;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        req.setAttribute("beraterListe", beraterService.alleBerater());

        // Berater zum Bearbeiten laden (optionaler id-Parameter)
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                req.setAttribute("formBerater", beraterService.findById(Long.parseLong(idParam)));
            } catch (NumberFormatException e) {
                req.setAttribute("formBerater", new Berater());
            }
        } else {
            req.setAttribute("formBerater", new Berater());
        }

        req.getRequestDispatcher("/berater.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("loeschen".equals(action)) {
                beraterService.loeschen(Long.parseLong(req.getParameter("id")));
                flashMessage.setze("Berater geloescht.", "info");

            } else if ("speichern".equals(action)) {
                Berater berater = new Berater();
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    berater = beraterService.findById(Long.parseLong(idParam));
                }
                berater.setVorname(req.getParameter("vorname"));
                berater.setNachname(req.getParameter("nachname"));
                berater.setEmail(req.getParameter("email"));
                berater.setSenioritaet(Berater.Senioritaet.valueOf(req.getParameter("senioritaet")));
                berater.setStundensatz(new BigDecimal(req.getParameter("stundensatz").replace(",", ".")));
                berater.setVerfuegbar("on".equals(req.getParameter("verfuegbar")));
                beraterService.speichern(berater);
                flashMessage.setze("Berater gespeichert.", "info");
            }
        } catch (Exception e) {
            flashMessage.setze("Fehler: " + e.getMessage(), "error");
        }

        resp.sendRedirect(req.getContextPath() + "/berater");
    }
}
