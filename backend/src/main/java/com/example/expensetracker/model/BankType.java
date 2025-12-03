package com.example.expensetracker.model;

public enum BankType {
    HDFC("HDFC Bank", new String[]{"HDFC", "HDFC BANK"}),
    ICICI("ICICI Bank", new String[]{"ICICI", "ICICI BANK"}),
    SBI("State Bank of India", new String[]{"SBI", "STATE BANK", "STATE BANK OF INDIA"}),
    AXIS("Axis Bank", new String[]{"AXIS", "AXIS BANK"}),
    KOTAK("Kotak Mahindra Bank", new String[]{"KOTAK", "KOTAK MAHINDRA", "KOTAK BANK"}),
    UNKNOWN("Unknown Bank", new String[]{});

    private final String displayName;
    private final String[] identifiers;

    BankType(String displayName, String[] identifiers) {
        this.displayName = displayName;
        this.identifiers = identifiers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }
}

