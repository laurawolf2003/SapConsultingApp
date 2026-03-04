package de.consulting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "zeiteintrag")
@NamedQueries({
    @NamedQuery(name = "Zeiteintrag.findByProjekt",
                query = "SELECT z FROM Zeiteintrag z WHERE z.projekt.id = :projektId ORDER BY z.datum DESC"),
    @NamedQuery(name = "Zeiteintrag.findByBerater",
                query = "SELECT z FROM Zeiteintrag z WHERE z.berater.id = :beraterId ORDER BY z.datum DESC"),
    @NamedQuery(name = "Zeiteintrag.summeStundenByProjekt",
                query = "SELECT COALESCE(SUM(z.stunden), 0) FROM Zeiteintrag z WHERE z.projekt.id = :projektId"),
    @NamedQuery(name = "Zeiteintrag.summeAbrechenbarByProjekt",
                query = "SELECT COALESCE(SUM(z.stunden), 0) FROM Zeiteintrag z WHERE z.projekt.id = :projektId AND z.abrechenbar = true")
})
public class Zeiteintrag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "datum", nullable = false)
    private Date datum;

    @NotNull
    @DecimalMin("0.25")
    @DecimalMax("24.00")
    @Column(name = "stunden", nullable = false, precision = 5, scale = 2)
    private BigDecimal stunden;

    @Size(max = 300)
    @Column(name = "beschreibung", length = 300)
    private String beschreibung;

    @Column(name = "abrechenbar", nullable = false)
    private boolean abrechenbar = true;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "projekt_id", nullable = false)
    private Projekt projekt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "berater_id", nullable = false)
    private Berater berater;

    public Zeiteintrag() {
    }

    // ----- Getter / Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public BigDecimal getStunden() {
        return stunden;
    }

    public void setStunden(BigDecimal stunden) {
        this.stunden = stunden;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public boolean isAbrechenbar() {
        return abrechenbar;
    }

    public void setAbrechenbar(boolean abrechenbar) {
        this.abrechenbar = abrechenbar;
    }

    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    public Berater getBerater() {
        return berater;
    }

    public void setBerater(Berater berater) {
        this.berater = berater;
    }

    @Override
    public String toString() {
        return datum + ": " + stunden + "h - " + beschreibung;
    }
}
