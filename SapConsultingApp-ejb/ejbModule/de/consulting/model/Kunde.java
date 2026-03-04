package de.consulting.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "kunde")
@NamedQueries({
    @NamedQuery(name = "Kunde.findAll",
                query = "SELECT k FROM Kunde k ORDER BY k.firmenname"),
    @NamedQuery(name = "Kunde.findByFirmenname",
                query = "SELECT k FROM Kunde k WHERE k.firmenname LIKE :name")
})
public class Kunde implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "firmenname", nullable = false, length = 100)
    private String firmenname;

    @Size(max = 50)
    @Column(name = "branche", length = 50)
    private String branche;

    @Size(max = 100)
    @Column(name = "ansprechpartner", length = 100)
    private String ansprechpartner;

    @Size(max = 200)
    @Column(name = "adresse", length = 200)
    private String adresse;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "kunde", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Projekt> projekte = new ArrayList<Projekt>();

    public Kunde() {
    }

    // ----- Getter / Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirmenname() {
        return firmenname;
    }

    public void setFirmenname(String firmenname) {
        this.firmenname = firmenname;
    }

    public String getBranche() {
        return branche;
    }

    public void setBranche(String branche) {
        this.branche = branche;
    }

    public String getAnsprechpartner() {
        return ansprechpartner;
    }

    public void setAnsprechpartner(String ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Projekt> getProjekte() {
        return projekte;
    }

    public void setProjekte(List<Projekt> projekte) {
        this.projekte = projekte;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Kunde other = (Kunde) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return firmenname;
    }
}
