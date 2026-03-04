package de.consulting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "projekt")
@NamedQueries({
    @NamedQuery(name = "Projekt.findAll",
                query = "SELECT p FROM Projekt p ORDER BY p.bezeichnung"),
    @NamedQuery(name = "Projekt.findByStatus",
                query = "SELECT p FROM Projekt p WHERE p.status = :status ORDER BY p.bezeichnung"),
    @NamedQuery(name = "Projekt.findByKunde",
                query = "SELECT p FROM Projekt p WHERE p.kunde.id = :kundeId ORDER BY p.bezeichnung")
})
public class Projekt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 150)
    @Column(name = "bezeichnung", nullable = false, length = 150)
    private String bezeichnung;

    @Size(max = 500)
    @Column(name = "beschreibung", length = 500)
    private String beschreibung;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sap_modul", nullable = false, length = 10)
    private SapModul sapModul;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjektStatus status = ProjektStatus.ANGEBOT;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_datum")
    private Date startDatum;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_datum")
    private Date endDatum;

    @Min(0)
    @Column(name = "budget_stunden")
    private int budgetStunden;

    @Column(name = "budget_euro", precision = 10, scale = 2)
    private BigDecimal budgetEuro;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @ManyToMany
    @JoinTable(
        name = "projekt_berater",
        joinColumns = @JoinColumn(name = "projekt_id"),
        inverseJoinColumns = @JoinColumn(name = "berater_id")
    )
    private List<Berater> berater = new ArrayList<Berater>();

    @OneToMany(mappedBy = "projekt", cascade = javax.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Zeiteintrag> zeiteintraege = new ArrayList<Zeiteintrag>();

    public Projekt() {
    }

    // ----- Getter / Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public SapModul getSapModul() {
        return sapModul;
    }

    public void setSapModul(SapModul sapModul) {
        this.sapModul = sapModul;
    }

    public ProjektStatus getStatus() {
        return status;
    }

    public void setStatus(ProjektStatus status) {
        this.status = status;
    }

    public Date getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(Date startDatum) {
        this.startDatum = startDatum;
    }

    public Date getEndDatum() {
        return endDatum;
    }

    public void setEndDatum(Date endDatum) {
        this.endDatum = endDatum;
    }

    public int getBudgetStunden() {
        return budgetStunden;
    }

    public void setBudgetStunden(int budgetStunden) {
        this.budgetStunden = budgetStunden;
    }

    public BigDecimal getBudgetEuro() {
        return budgetEuro;
    }

    public void setBudgetEuro(BigDecimal budgetEuro) {
        this.budgetEuro = budgetEuro;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public List<Berater> getBerater() {
        return berater;
    }

    public void setBerater(List<Berater> berater) {
        this.berater = berater;
    }

    public List<Zeiteintrag> getZeiteintraege() {
        return zeiteintraege;
    }

    public void setZeiteintraege(List<Zeiteintrag> zeiteintraege) {
        this.zeiteintraege = zeiteintraege;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Projekt other = (Projekt) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return bezeichnung + " [" + status + "]";
    }
}
