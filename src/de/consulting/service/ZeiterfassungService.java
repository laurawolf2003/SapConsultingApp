package de.consulting.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.consulting.model.Berater;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.Zeiteintrag;

@ApplicationScoped
public class ZeiterfassungService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "SapConsultingPU")
    private EntityManager em;

    public List<Zeiteintrag> findByProjekt(Long projektId) {
        return em.createNamedQuery("Zeiteintrag.findByProjekt", Zeiteintrag.class)
                 .setParameter("projektId", projektId)
                 .getResultList();
    }

    public List<Zeiteintrag> findByBerater(Long beraterId) {
        return em.createNamedQuery("Zeiteintrag.findByBerater", Zeiteintrag.class)
                 .setParameter("beraterId", beraterId)
                 .getResultList();
    }

    /** Gebuchte Gesamtstunden fuer ein Projekt. */
    public BigDecimal gesamtStundenByProjekt(Long projektId) {
        Number result = em.createNamedQuery("Zeiteintrag.summeStundenByProjekt", Number.class)
                          .setParameter("projektId", projektId)
                          .getSingleResult();
        return new BigDecimal(result.toString());
    }

    /** Abrechenbare Stunden fuer ein Projekt. */
    public BigDecimal abrechenbareStundenByProjekt(Long projektId) {
        Number result = em.createNamedQuery("Zeiteintrag.summeAbrechenbarByProjekt", Number.class)
                          .setParameter("projektId", projektId)
                          .getSingleResult();
        return new BigDecimal(result.toString());
    }

    /**
     * Bucht einen Zeiteintrag auf ein Projekt.
     *
     * Business-Regeln:
     *  - BR-2: Zeitbuchung nur auf Projekte mit Status AKTIV
     *  - BR-1: Nur zugewiesene Berater duerfen buchen
     *  - BR-6: Budget-Warnung bei Ueberschreitung (Rueckgabe als String, nicht blockierend)
     *
     * @return Warnmeldung bei Budget-Ueberschreitung, sonst null
     */
    @Transactional
    public String buchen(Zeiteintrag eintrag) {
        Projekt projekt = em.find(Projekt.class, eintrag.getProjekt().getId());
        Berater berater = em.find(Berater.class, eintrag.getBerater().getId());

        if (projekt == null || berater == null) {
            throw new IllegalArgumentException("Projekt oder Berater nicht gefunden.");
        }

        // BR-2: Nur aktive Projekte
        if (projekt.getStatus() != ProjektStatus.AKTIV) {
            throw new IllegalStateException(
                "Zeitbuchung nur auf aktive Projekte moeglich. Aktueller Status: " + projekt.getStatus());
        }

        // BR-1: Nur zugewiesene Berater
        if (!projekt.getBerater().contains(berater)) {
            throw new IllegalStateException(
                "Berater " + berater.getVollerName() + " ist diesem Projekt nicht zugewiesen.");
        }

        eintrag.setProjekt(projekt);
        eintrag.setBerater(berater);
        em.persist(eintrag);

        // BR-6: Budget-Warnung
        String warnung = null;
        if (projekt.getBudgetStunden() > 0) {
            BigDecimal gesamtStunden = gesamtStundenByProjekt(projekt.getId());
            if (gesamtStunden.intValue() > projekt.getBudgetStunden()) {
                warnung = "WARNUNG: Budget-Stunden ueberschritten! Gebucht: " + gesamtStunden
                        + "h / Budget: " + projekt.getBudgetStunden() + "h";
            }
        }

        return warnung;
    }
}
