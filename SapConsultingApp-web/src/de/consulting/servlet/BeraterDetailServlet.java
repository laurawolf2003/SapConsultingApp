package de.consulting.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.consulting.model.Berater;
import de.consulting.model.SapModul;
import de.consulting.service.BeraterService;
import de.consulting.service.SkillService;

@WebServlet("/berater-detail")
public class BeraterDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/BeraterService!de.consulting.service.BeraterService")
    private BeraterService beraterService;

    @EJB(lookup = "java:global/SapConsultingApp/SapConsultingApp-ejb/SkillService!de.consulting.service.SkillService")
    private SkillService skillService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ServletUtil.ladFlashMsg(req);

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/berater");
            return;
        }

        Berater berater = beraterService.findById(Long.parseLong(idParam));
        if (berater == null) {
            resp.sendRedirect(req.getContextPath() + "/berater");
            return;
        }

        req.setAttribute("berater", berater);
        req.setAttribute("sapModule", SapModul.values());

        req.getRequestDispatcher("/berater-detail.xhtml").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String beraterId = req.getParameter("beraterId");

        try {
            if ("skillZuweisen".equals(action)) {
                SapModul modul = SapModul.valueOf(req.getParameter("modul"));
                int level = Integer.parseInt(req.getParameter("level"));
                boolean zertifiziert = "on".equals(req.getParameter("zertifiziert"));
                skillService.skillZuweisen(Long.parseLong(beraterId), modul, level, zertifiziert);
                ServletUtil.setFlashMsg(req, "Skill zugewiesen.", "info");

            } else if ("skillEntfernen".equals(action)) {
                skillService.skillEntfernen(Long.parseLong(req.getParameter("skillId")));
                ServletUtil.setFlashMsg(req, "Skill entfernt.", "info");
            }
        } catch (Exception e) {
            ServletUtil.setFlashMsg(req, "Fehler: " + e.getMessage(), "error");
        }

        resp.sendRedirect(req.getContextPath() + "/berater-detail?id=" + beraterId);
    }
}
