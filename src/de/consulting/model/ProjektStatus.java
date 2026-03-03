package de.consulting.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Projektstatus mit definierter Statusmaschine.
 *
 * Erlaubte Uebergaenge:
 *   ANGEBOT -> GENEHMIGT | STORNIERT
 *   GENEHMIGT -> AKTIV | STORNIERT
 *   AKTIV -> ABGESCHLOSSEN | STORNIERT
 *   ABGESCHLOSSEN -> (Endzustand)
 *   STORNIERT -> (Endzustand)
 */
public enum ProjektStatus {

    ANGEBOT,
    GENEHMIGT,
    AKTIV,
    ABGESCHLOSSEN,
    STORNIERT;

    private static final Map<ProjektStatus, Set<ProjektStatus>> ERLAUBTE_UEBERGAENGE;

    static {
        Map<ProjektStatus, Set<ProjektStatus>> map = new HashMap<ProjektStatus, Set<ProjektStatus>>();
        map.put(ANGEBOT, new HashSet<ProjektStatus>(Arrays.asList(GENEHMIGT, STORNIERT)));
        map.put(GENEHMIGT, new HashSet<ProjektStatus>(Arrays.asList(AKTIV, STORNIERT)));
        map.put(AKTIV, new HashSet<ProjektStatus>(Arrays.asList(ABGESCHLOSSEN, STORNIERT)));
        map.put(ABGESCHLOSSEN, Collections.<ProjektStatus>emptySet());
        map.put(STORNIERT, Collections.<ProjektStatus>emptySet());
        ERLAUBTE_UEBERGAENGE = Collections.unmodifiableMap(map);
    }

    /**
     * Prueft, ob der Uebergang vom aktuellen Status zum neuen Status erlaubt ist.
     */
    public boolean istUebergangErlaubt(ProjektStatus neuerStatus) {
        Set<ProjektStatus> erlaubt = ERLAUBTE_UEBERGAENGE.get(this);
        return erlaubt != null && erlaubt.contains(neuerStatus);
    }
}
