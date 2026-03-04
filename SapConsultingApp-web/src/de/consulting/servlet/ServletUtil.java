package de.consulting.servlet;

import javax.servlet.http.HttpServletRequest;

/** Hilfsmethoden fuer Flash-Nachrichten zwischen Redirect und naechstem GET. */
class ServletUtil {

    static void setFlashMsg(HttpServletRequest req, String msg, String severity) {
        req.getSession().setAttribute("flashMsg", msg);
        req.getSession().setAttribute("flashSeverity", severity);
    }

    static void ladFlashMsg(HttpServletRequest req) {
        String msg = (String) req.getSession().getAttribute("flashMsg");
        if (msg != null) {
            req.setAttribute("flashMsg", msg);
            req.setAttribute("flashSeverity", req.getSession().getAttribute("flashSeverity"));
            req.getSession().removeAttribute("flashMsg");
            req.getSession().removeAttribute("flashSeverity");
        }
    }
}
