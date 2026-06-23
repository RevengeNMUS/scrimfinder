import java.util.ArrayList;

/**
 * A Team!
 * Has a:
 * Team number
 * Team name
 * Region
 * Active Scrims
 * Active Scrims Organized
 */
public class Team {
    private final int teamNum;
    private final String teamName;
    private final Region region;
    private ArrayList<Scrimmage> activeScrimmages;
    private ArrayList<Scrimmage> organizedScrimmages;

    public Team(int tNum, String tName, Region reg, ArrayList<Scrimmage> aScrims, ArrayList<Scrimmage> oScrims) {
        teamNum = tNum;
        teamName = tName;
        region = reg;
        activeScrimmages = aScrims;
        organizedScrimmages = oScrims;
    }

    public Team(int tNum, String tName, Region reg) {
        teamNum = tNum;
        teamName = tName;
        region = reg;
        activeScrimmages = new ArrayList<>();
        organizedScrimmages = new ArrayList<>();
    }

    public void attendeeFor(Scrimmage scrimmage) {
        activeScrimmages.add(scrimmage);
    }

    public boolean notAttending(Scrimmage scrimmage) {
        return activeScrimmages.remove(scrimmage);
    }

    public void organizerFor(Scrimmage scrimmage) {
        organizedScrimmages.add(scrimmage);
    }
}