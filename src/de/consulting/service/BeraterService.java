package de.consulting.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.consulting.model.Berater;
import de.consulting.model.SapModul;

@ApplicationScoped
public class BeraterService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "SapConsultingPU")
    private EntityManager em;

    public List<Berater> alleBerater() {
        return em.createNamedQuery("Berater.findAll", Berater.class).getResultList();
    }

    public Berater findById(Long id) {
        return em.find(Berater.class, id);
    }

    public List<Berater> verfuegbareBerater() {
        return em.createNamedQuery("Berater.findVerfuegbare", Berater.class).getResultList();
    }

    public List<Berater> findBySapModul(SapModul modul) {
        return em.createNamedQuery("Berater.findBySapModul", Berater.class)
                 .setParameter("modul", modul)
                 .getResultList();
    }

    @Transactional
    public Berater speichern(Berater berater) {
        if (berater.getId() == null) {
            em.persist(berater);
            return berater;
        } else {
            return em.merge(berater);
        }
    }

    @Transactional
    public void loeschen(Long id) {
        Berater berater = em.find(Berater.class, id);
        if (berater != null) {
            em.remove(berater);
        }
    }
}
