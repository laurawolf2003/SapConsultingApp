package de.consulting.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.model.Kunde;
import de.consulting.service.KundeService;

@WebServlet("/kunden")
public class KundeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/KundeService!de.consulting.service.KundeService")
    private KundeService kundeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ServletUtil.ladFlashMsg(req);

        req.setAttribute("kunden", kundeService.alleKunden());

        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                req.setAttribute("formKunde", kundeService.findById(Long.parseLong(idParam)));
            } catch (NumberFormatException e) {
                req.setAttribute("formKunde", new Kunde());
            }
        } else {
            req.setAttribute("formKunde", new Kunde());
        }

        req.getRequestDispatcher("/kunden.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("loeschen".equals(action)) {
                kundeService.loeschen(Long.parseLong(req.getParameter("id")));
                ServletUtil.setFlashMsg(req, "Kunde geloescht.", "info");

            } else if ("speichern".equals(action)) {
                Kunde kunde = new Kunde();
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    kunde = kundeService.findById(Long.parseLong(idParam));
                }
                kunde.setFirmenname(req.getParameter("firmenname"));
                kunde.setBranche(req.getParameter("branche"));
                kunde.setAnsprechpartner(req.getParameter("ansprechpartner"));
                kunde.setAdresse(req.getParameter("adresse"));
                kunde.setEmail(req.getParameter("email"));
                kundeService.speichern(kunde);
                ServletUtil.setFlashMsg(req, "Kunde gespeichert.", "info");
            }
        } catch (Exception e) {
            ServletUtil.setFlashMsg(req, "Fehler: " + e.getMessage(), "error");
        }

        resp.sendRedirect(req.getContextPath() + "/kunden");
    }
}
