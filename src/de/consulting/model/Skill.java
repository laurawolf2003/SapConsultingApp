package de.consulting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "skill")
@NamedQueries({
    @NamedQuery(name = "Skill.findByBerater",
                query = "SELECT s FROM Skill s WHERE s.berater.id = :beraterId ORDER BY s.sapModul"),
    @NamedQuery(name = "Skill.findByModulAndBerater",
                query = "SELECT s FROM Skill s WHERE s.berater.id = :beraterId AND s.sapModul = :modul")
})
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sap_modul", nullable = false, length = 10)
    private SapModul sapModul;

    /** Skill-Level: 1 = Grundkenntnisse, 5 = Experte */
    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "skill_level", nullable = false)
    private int level;

    @Column(name = "zertifiziert", nullable = false)
    private boolean zertifiziert = false;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "berater_id", nullable = false)
    private Berater berater;

    public Skill() {
    }

    // ----- Getter / Setter -----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SapModul getSapModul() {
        return sapModul;
    }

    public void setSapModul(SapModul sapModul) {
        this.sapModul = sapModul;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isZertifiziert() {
        return zertifiziert;
    }

    public void setZertifiziert(boolean zertifiziert) {
        this.zertifiziert = zertifiziert;
    }

    public Berater getBerater() {
        return berater;
    }

    public void setBerater(Berater berater) {
        this.berater = berater;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skill other = (Skill) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return sapModul + " (Level " + level + (zertifiziert ? ", zertifiziert" : "") + ")";
    }
}
