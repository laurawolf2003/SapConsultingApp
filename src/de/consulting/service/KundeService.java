package de.consulting.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.consulting.model.Kunde;

@ApplicationScoped
public class KundeService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "SapConsultingPU")
    private EntityManager em;

    public List<Kunde> alleKunden() {
        return em.createNamedQuery("Kunde.findAll", Kunde.class).getResultList();
    }

    public Kunde findById(Long id) {
        return em.find(Kunde.class, id);
    }

    public List<Kunde> sucheNachFirmenname(String name) {
        return em.createNamedQuery("Kunde.findByFirmenname", Kunde.class)
                 .setParameter("name", "%" + name + "%")
                 .getResultList();
    }

    @Transactional
    public Kunde speichern(Kunde kunde) {
        if (kunde.getId() == null) {
            em.persist(kunde);
            return kunde;
        } else {
            return em.merge(kunde);
        }
    }

    @Transactional
    public void loeschen(Long id) {
        Kunde kunde = em.find(Kunde.class, id);
        if (kunde != null) {
            em.remove(kunde);
        }
    }
}
