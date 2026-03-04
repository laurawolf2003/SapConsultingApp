package de.consulting.model;

/**
 * SAP-Module, die im Beratungsumfeld relevant sind.
 */
public enum SapModul {

    FI("Financial Accounting"),
    CO("Controlling"),
    MM("Materials Management"),
    SD("Sales & Distribution"),
    PP("Production Planning"),
    HR("Human Resources"),
    BASIS("SAP Basis / Administration");

    private final String bezeichnung;

    SapModul(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }
}
