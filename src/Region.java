import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public enum Region {
    NORCAL("norcal"),
    SOCAL("socal"),
    NA("N/A");

    //todo: add more places you chud this is SILLY

    private final String regionCode;
    private Region(String code) {
        regionCode = code;
    }

    public static Region fromCode(String code) {
        return switch (code) {
            case "norcal" -> NORCAL;
            case "socal" -> SOCAL;
            case "NA" -> NA;
            case null, default -> NA;
        };
    }

    public String getRegionCode() {
        return regionCode;
    }
}
