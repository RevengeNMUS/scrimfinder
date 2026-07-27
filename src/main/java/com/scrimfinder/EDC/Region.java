package com.scrimfinder.EDC;

public enum Region {
    NORCAL("NORCAL"),
    SOCAL("SOCAL"),
    KNOWHERE("KNOWHERE");

    //todo: add more places you chud this is SILLY

    private final String regionCode;
    private Region(String code) {
        regionCode = code;
    }

    public static Region fromCode(String code) {
        return switch (code) {
            case "NORCAL" -> NORCAL;
            case "SOCAL" -> SOCAL;
            case "KNOWHERE" -> KNOWHERE;
            case null, default -> KNOWHERE;
        };
    }

    public String getRegionCode() {
        return regionCode;
    }
}
