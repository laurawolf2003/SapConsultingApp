package de.consulting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "berater")
@NamedQueries({
    @NamedQuery(name = "Berater.findAll",
                query = "SELECT b FROM Berater b ORDER BY b.nachname, b.vorname"),
    @NamedQuery(name = "Berater.findVerfuegbare",
                query = "SELECT b FROM Berater b WHERE b.verfuegbar = true ORDER BY b.nachname"),
    @NamedQuery(name = "Berater.findBySapModul",
                query = "SELECT DISTINCT b FROM Berater b JOIN b.skills s WHERE s.sapModul = :modul ORDER BY b.nachname")
})
public class Berater implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Senioritaet {
        JUNIOR, SENIOR, PRINCIPAL, PARTNER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "vorname", nullable = false, length = 50)
    private String vorname;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nachname", nullable = false, length = 50)
    private String nachname;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "senioritaet", nullable = false, length = 20)
    private Senioritaet senioritaet;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "stundensatz", nullable = false, precision = 8, scale = 2)
    private BigDecimal stundensatz;

    @Column(name = "verfuegbar", nullable = false)
    private boolean verfuegbar = true;

    @OneToMany(mappedBy = "berater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<Skill>();

    @ManyToMany(mappedBy = "berater")
    private List<Projekt> projekte = new ArrayList<Projekt>();

    @OneToMany(mappedBy = "berater", cascade = CascadeType.ALL)
    private List<Zeiteintrag> zeiteintraege = new ArrayList<Zeiteintrag>();

    public Berater() {
    }

    // ----- Getter / Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Senioritaet getSenioritaet() {
        return senioritaet;
    }

    public void setSenioritaet(Senioritaet senioritaet) {
        this.senioritaet = senioritaet;
    }

    public BigDecimal getStundensatz() {
        return stundensatz;
    }

    public void setStundensatz(BigDecimal stundensatz) {
        this.stundensatz = stundensatz;
    }

    public boolean isVerfuegbar() {
        return verfuegbar;
    }

    public void setVerfuegbar(boolean verfuegbar) {
        this.verfuegbar = verfuegbar;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Projekt> getProjekte() {
        return projekte;
    }

    public void setProjekte(List<Projekt> projekte) {
        this.projekte = projekte;
    }

    public List<Zeiteintrag> getZeiteintraege() {
        return zeiteintraege;
    }

    public void setZeiteintraege(List<Zeiteintrag> zeiteintraege) {
        this.zeiteintraege = zeiteintraege;
    }

    /** Vollstaendiger Name des Beraters. */
    public String getVollerName() {
        return vorname + " " + nachname;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Berater other = (Berater) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return vorname + " " + nachname + " (" + senioritaet + ")";
    }
}
