package de.consulting.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.cdi.FlashMessage;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.service.BeraterService;
import de.consulting.service.ProjektService;

@WebServlet("/projekt-detail")
public class ProjektDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/ProjektService!de.consulting.service.ProjektService")
    private ProjektService projektService;

    @EJB(lookup = "java:global/SapConsultingApp1/SapConsultingApp1-ejb/BeraterService!de.consulting.service.BeraterService")
    private BeraterService beraterService;

    @Inject
    private FlashMessage flashMessage;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/projekte");
            return;
        }

        Projekt projekt = projektService.findById(Long.parseLong(idParam));
        if (projekt == null) {
            resp.sendRedirect(req.getContextPath() + "/projekte");
            return;
        }

        req.setAttribute("projekt", projekt);
        req.setAttribute("verfuegbareBerater", beraterService.verfuegbareBerater());

        req.getRequestDispatcher("/projekt-detail.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String projektId = req.getParameter("projektId");

        try {
            if ("statusAendern".equals(action)) {
                ProjektStatus neuerStatus = ProjektStatus.valueOf(req.getParameter("neuerStatus"));
                projektService.statusAendern(Long.parseLong(projektId), neuerStatus);
                flashMessage.setze("Status geaendert.", "info");

            } else if ("beraterZuweisen".equals(action)) {
                projektService.beraterZuweisen(
                        Long.parseLong(projektId),
                        Long.parseLong(req.getParameter("beraterId")));
                flashMessage.setze("Berater zugewiesen.", "info");

            } else if ("beraterEntfernen".equals(action)) {
                projektService.beraterEntfernen(
                        Long.parseLong(projektId),
                        Long.parseLong(req.getParameter("beraterId")));
                flashMessage.setze("Berater entfernt.", "info");
            }
        } catch (Exception e) {
            flashMessage.setze("Fehler: " + e.getMessage(), "error");
        }

        resp.sendRedirect(req.getContextPath() + "/projekt-detail?id=" + projektId);
    }
}
