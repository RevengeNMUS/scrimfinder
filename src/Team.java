import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * A Team! <p>
 * Has a: <br>
 * Team number<br>
 * Team name<br>
 * Region<br>
 * Active Scrims<br>
 * Scrims Organized<br>
 */
public class Team {
    private final int teamNum;
    private final String teamName;
    private final Region region;
    private ArrayList<Scrimmage> activeScrimmages;
    private ArrayList<Scrimmage> organizedScrimmages;

    public static Team NULL_TEAM = new Team(0, "null", Region.NA);

    public int getTeamNum() {
        return teamNum;
    }

    public Region getRegion() {
        return region;
    }

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Scrimmage> getActiveScrimmages() {
        return activeScrimmages;
    }

    public ArrayList<Scrimmage> getOrganizedScrimmages() {
        return organizedScrimmages;
    }

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

    public Team(int tNum) {
        teamNum = tNum;
        teamName = "N/A";
        region = Region.NA;
        activeScrimmages = new ArrayList<>();
        organizedScrimmages = new ArrayList<>();
    }

    public Team(File file) throws FileNotFoundException {
        StringBuilder stingerBuilder = new StringBuilder();
        try (Scanner scammer = new Scanner(file)) {
            while (scammer.hasNextLine()) {
                stingerBuilder.append(scammer.nextLine()).append("\n");
            }
        }

        var stinger = stingerBuilder.toString();
        var stingerArray = stinger.split("\n");

        this.teamNum = Integer.parseInt(stingerArray[0]);
        this.teamName = stingerArray[1];
        this.region = Region.fromCode(stingerArray[2]);

        var scrimListString = stingerArray[3].substring(1, stingerArray[3].length() - 1);
        activeScrimmages = new ArrayList<Scrimmage>();
        if (!scrimListString.isBlank()) {
            var scrimList = scrimListString.split(", ");
            for (String scrim : scrimList) {
                activeScrimmages.add(ScrimmageImpl.fromFile(new File("scrimmages/" + scrim + ".txt")));
            }
        }

        scrimListString = stingerArray[4].substring(1, stingerArray[4].length() - 1);
        organizedScrimmages = new ArrayList<Scrimmage>();
        if (!scrimListString.isBlank()) {
            var scrimList = scrimListString.split(", ");
            for (String scrim : scrimList) {
                organizedScrimmages.add(ScrimmageImpl.fromFile(new File("scrimmages/" + scrim + ".txt")));
            }
        }
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

    /**
     * IMPLIMENT IT YOUBHB KJSGFVSG<FU
     * saves team data in a file!
     */
    public File saveTeam() {
        try (FileWriter rwefrrn = new FileWriter("team/" + teamNum + ".txt")) {
            var writeString =
                    teamNum + "\n" +
                    teamName + "\n" +
                    region.getRegionCode() + "\n" +
                    activeScrimmages.toString() + "\n" +
                    organizedScrimmages.toString();

            rwefrrn.write(writeString);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File("team/" + teamNum + ".txt");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team team)) return false;
        return teamNum == team.teamNum &&
                Objects.equals(teamName, team.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamNum, teamName, region, activeScrimmages, organizedScrimmages);
    }

    @Override
    public String toString() {
        return String.valueOf(teamNum);
    }
}