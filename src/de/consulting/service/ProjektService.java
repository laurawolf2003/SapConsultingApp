package de.consulting.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.consulting.model.Berater;
import de.consulting.model.Projekt;
import de.consulting.model.ProjektStatus;
import de.consulting.model.Skill;

@ApplicationScoped
public class ProjektService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "SapConsultingPU")
    private EntityManager em;

    public List<Projekt> alleProjekte() {
        return em.createNamedQuery("Projekt.findAll", Projekt.class).getResultList();
    }

    public Projekt findById(Long id) {
        return em.find(Projekt.class, id);
    }

    public List<Projekt> findByStatus(ProjektStatus status) {
        return em.createNamedQuery("Projekt.findByStatus", Projekt.class)
                 .setParameter("status", status)
                 .getResultList();
    }

    @Transactional
    public Projekt speichern(Projekt projekt) {
        if (projekt.getId() == null) {
            em.persist(projekt);
            return projekt;
        } else {
            return em.merge(projekt);
        }
    }

    @Transactional
    public void loeschen(Long id) {
        Projekt projekt = em.find(Projekt.class, id);
        if (projekt != null) {
            em.remove(projekt);
        }
    }

    /**
     * Aendert den Projektstatus gemaess der definierten Statusmaschine.
     * Wirft IllegalStateException bei ungueltigem Uebergang.
     */
    @Transactional
    public Projekt statusAendern(Long projektId, ProjektStatus neuerStatus) {
        Projekt projekt = em.find(Projekt.class, projektId);
        if (projekt == null) {
            throw new IllegalArgumentException("Projekt mit ID " + projektId + " nicht gefunden.");
        }
        if (!projekt.getStatus().istUebergangErlaubt(neuerStatus)) {
            throw new IllegalStateException(
                "Statusuebergang von " + projekt.getStatus() + " nach " + neuerStatus + " ist nicht erlaubt.");
        }
        projekt.setStatus(neuerStatus);
        return em.merge(projekt);
    }

    /**
     * Weist einen Berater einem Projekt zu.
     * Business-Regeln: Berater muss verfuegbar sein und den passenden SAP-Modul-Skill besitzen.
     */
    @Transactional
    public Projekt beraterZuweisen(Long projektId, Long beraterId) {
        Projekt projekt = em.find(Projekt.class, projektId);
        Berater berater = em.find(Berater.class, beraterId);

        if (projekt == null || berater == null) {
            throw new IllegalArgumentException("Projekt oder Berater nicht gefunden.");
        }

        // BR-4: Verfuegbarkeitspruefung
        if (!berater.isVerfuegbar()) {
            throw new IllegalStateException("Berater " + berater.getVollerName() + " ist nicht verfuegbar.");
        }

        // BR-3: Skill-Matching
        boolean hatPassendenSkill = false;
        for (Skill skill : berater.getSkills()) {
            if (skill.getSapModul() == projekt.getSapModul()) {
                hatPassendenSkill = true;
                break;
            }
        }
        if (!hatPassendenSkill) {
            throw new IllegalStateException(
                "Berater " + berater.getVollerName() + " besitzt keinen Skill fuer " + projekt.getSapModul() + ".");
        }

        // Doppelte Zuweisung verhindern
        if (!projekt.getBerater().contains(berater)) {
            projekt.getBerater().add(berater);
        }

        return em.merge(projekt);
    }

    /**
     * Entfernt einen Berater von einem Projekt.
     */
    @Transactional
    public Projekt beraterEntfernen(Long projektId, Long beraterId) {
        Projekt projekt = em.find(Projekt.class, projektId);
        Berater berater = em.find(Berater.class, beraterId);

        if (projekt != null && berater != null) {
            projekt.getBerater().remove(berater);
        }

        return em.merge(projekt);
    }
}
