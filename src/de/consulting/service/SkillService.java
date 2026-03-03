package de.consulting.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.consulting.model.Berater;
import de.consulting.model.SapModul;
import de.consulting.model.Skill;

@ApplicationScoped
public class SkillService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "SapConsultingPU")
    private EntityManager em;

    public List<Skill> findByBerater(Long beraterId) {
        return em.createNamedQuery("Skill.findByBerater", Skill.class)
                 .setParameter("beraterId", beraterId)
                 .getResultList();
    }

    /**
     * Weist einem Berater einen neuen Skill zu.
     * Prueft, ob der Berater den Skill bereits besitzt.
     */
    @Transactional
    public Skill skillZuweisen(Long beraterId, SapModul modul, int level, boolean zertifiziert) {
        Berater berater = em.find(Berater.class, beraterId);
        if (berater == null) {
            throw new IllegalArgumentException("Berater mit ID " + beraterId + " nicht gefunden.");
        }

        // Pruefen, ob Skill fuer dieses Modul bereits existiert
        List<Skill> vorhandene = em.createNamedQuery("Skill.findByModulAndBerater", Skill.class)
                                   .setParameter("beraterId", beraterId)
                                   .setParameter("modul", modul)
                                   .getResultList();
        if (!vorhandene.isEmpty()) {
            throw new IllegalStateException(
                "Berater besitzt bereits einen Skill fuer " + modul + ". Bitte aktualisieren.");
        }

        Skill skill = new Skill();
        skill.setSapModul(modul);
        skill.setLevel(level);
        skill.setZertifiziert(zertifiziert);
        skill.setBerater(berater);

        em.persist(skill);
        return skill;
    }

    /** Aktualisiert Level und Zertifizierung eines vorhandenen Skills. */
    @Transactional
    public Skill skillAktualisieren(Long skillId, int level, boolean zertifiziert) {
        Skill skill = em.find(Skill.class, skillId);
        if (skill == null) {
            throw new IllegalArgumentException("Skill nicht gefunden.");
        }
        skill.setLevel(level);
        skill.setZertifiziert(zertifiziert);
        return em.merge(skill);
    }

    /** Entfernt einen Skill. */
    @Transactional
    public void skillEntfernen(Long skillId) {
        Skill skill = em.find(Skill.class, skillId);
        if (skill != null) {
            em.remove(skill);
        }
    }
}
