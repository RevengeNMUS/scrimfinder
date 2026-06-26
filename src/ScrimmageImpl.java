import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Basically, a Scrimmage!<br>
 * Has a
 * <li>list of teams</li>
 * <li>Location</li>
 * <li>Region</li>
 * <li>Application Status (open, closed)</li>
 * <li>Organizer</li>
 * <li>A Size Limit</li>
 * <li>A Start/End Time</li>
 */
public class ScrimmageImpl implements Scrimmage {
    public static final ScrimmageImpl NULL_SCRIM = new ScrimmageImpl(new ArrayList<>(), Location.NULL_LOCATION, Region.NA, ApplicationStatus.CLOSED, Team.NULL_TEAM, 0, LocalDateTime.of(0, 1, 1, 0, 0), LocalDateTime.of(0, 1, 1, 0, 0));

    private ArrayList<Team> teams;
    private Location location;
    private Region region;
    private ApplicationStatus applicationStatus;
    private Team organizer;
    private int sizeLimit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScrimmageImpl(ArrayList<Team> teams, Location location, Region region, ApplicationStatus applicationStatus, Team organizer, int sizeLimit, LocalDateTime startTime, LocalDateTime endTime) {
        this.teams = teams;
        this.location = location;
        this.region = region;
        this.applicationStatus = applicationStatus;
        this.organizer = organizer;
        this.sizeLimit = sizeLimit;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScrimmageImpl fromFile(File file) {
        String fileString;
        //todo impl it actually maybe :0
        try (Scanner reeder = new Scanner(file)) {
            StringBuilder string = new StringBuilder();
            while (reeder.hasNextLine()) {
                string.append(reeder.nextLine()).append("\n");
            }
            fileString = string.toString();
        } catch (IOException e) {
            return NULL_SCRIM;
        }

        var stringSpmaxlitByNewline = fileString.split("\n");
        var teamsString = stringSpmaxlitByNewline[0].substring(1, stringSpmaxlitByNewline[0].length() - 1);
        var teams = new ArrayList<Team>();
        if (!teamsString.isBlank()) {
            var teamsList = teamsString.split(", ");
            for (String team : teamsList) {
                try {
                    teams.add(new Team(new File("team/" + team + ".txt")));
                } catch (FileNotFoundException e) {
                    teams.add(new Team(Integer.parseInt(team)));
                }
            }
        }

        var locList = stringSpmaxlitByNewline[1].split("_");
        var location = new Location(Double.parseDouble(locList[5]), Double.parseDouble(locList[4]), locList[0], locList[1], locList[2], locList[3]);

        var region = Region.fromCode(stringSpmaxlitByNewline[2]);

        var applicationStatus = ApplicationStatus.fromStatusString(stringSpmaxlitByNewline[3]);

        Team organizer;

        try {
            organizer = new Team(new File("team/" + stringSpmaxlitByNewline[4] + ".txt"));
        } catch (FileNotFoundException e) {
            organizer = new Team(Integer.parseInt(stringSpmaxlitByNewline[4]));
        }

        var sizeLimit = Integer.parseInt(stringSpmaxlitByNewline[5]);

        var parser = DateTimeFormatter.ofPattern("MM_dd_yyyy_HH");
        var startDT = stringSpmaxlitByNewline[6];
        var startTime = LocalDateTime.parse(startDT, parser);
        var endDT = stringSpmaxlitByNewline[7];
        var endTime = LocalDateTime.parse(endDT, parser);

        return new ScrimmageImpl(teams, location, region, applicationStatus, organizer, sizeLimit, startTime, endTime);
    }

    /**
     * Tries to add a team to this scrimmage.
     * Might fail if the scrimmage is full, applications are closed, or team is already in scrimmage
     *
     * @param team the team to be added
     * @return whether the attempt was successful
     */
    @Override
    public boolean addTeam(Team team) {
        if (teams.size() >= sizeLimit || applicationStatus == ApplicationStatus.CLOSED || hasTeam(team)) //aPPARENTLY size WORKS?!?!?!? waw
            return false;
        teams.add(team);
        return true;
    }

    /**
     * Tries to remove a team (that isnt organizing this scrimmage) from this scrimmage.
     *
     * @param team the team to be removed
     * @return whether the team was removed <br>(whether they existed or whether the removal was successful (they weren't the organizer team))
     */
    @Override
    public boolean removeTeam(Team team) {
        return !team.equals(organizer) && teams.remove(team);
    }

    /**
     * Sets the start time for the event
     *
     * @param st start time
     */
    @Override
    public void setStartTime(LocalDateTime st) {
        startTime = st;
    }

    /**
     * Sets the end time for the event
     *
     * @param et end time
     */
    @Override
    public void setEndTime(LocalDateTime et) {
        endTime = et;
    }

    /**
     * @return the start time for the event
     */
    @Override
    public LocalDateTime startTime() {
        return startTime;
    }

    /**
     * @return the end time for the event
     */
    @Override
    public LocalDateTime endTime() {
        return endTime;
    }

    /**
     * Sets the size limit for the event
     *
     * @param sl
     */
    @Override
    public void setSizeLimit(int sl) {
        sizeLimit = sl;
    }

    /**
     * Sets the Location for the event
     *
     * @param location
     */
    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets the Region for the event
     *
     * @param region
     */
    @Override
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * Sets the ApplicationStatus for the event
     *
     * @param appStatus
     */
    @Override
    public void setApplicationStatus(ApplicationStatus appStatus) {
        this.applicationStatus = appStatus;
    }

    /**
     * @return an ArrayList of teams signed up/participating in this scrimmage
     */
    @Override
    public ArrayList<Team> teamsInScrim() {
        return teams;
    }

    /**
     * @return the {@link Location} of the scrimmage
     */
    @Override
    public Location locationOfScrim() {
        return location;
    }

    /**
     * @return the region that the Scrimmage will happen in
     * Used for filtering results?
     */
    @Override
    public Region regionOfScrim() {
        return region;
    }

    /**
     * @return this scrimmage's {@link ApplicationStatus} (closed, open, etc)
     */
    @Override
    public ApplicationStatus appStatus() {
        return applicationStatus;
    }

    /**
     * @return the {@link Team} organizing this scrimmage
     */
    @Override
    public Team scrimOrganizer() {
        return organizer;
    }

    /**
     * checks with the size limit of the scrimmage
     * and makes sure that it has not been reached
     * (if size limit is -1, unlimited)
     *
     * @return if the scrimmage is full
     */
    @Override
    public boolean isFull() {
        return sizeLimit != -1 && teams.size() > sizeLimit;
    }

    /**
     * checks if this scrimmage already has team t signed up
     */
    @Override
    public boolean hasTeam(Team t) {
        return teams.contains(t);
    }

    @Override
    public String getIdentifier() {
        var identifier = organizer.getTeamNum() + "-" + location.city + "-" + startTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        return identifier;
    }

    /**
     * saves the scrimmages data into a file wawwww
     * when implementation is made, MAKE SURE CONSTRUCTOR CAN TAKE A FILE AS PARAM
     * you RAT
     *
     */
    @Override
    public File saveToFile() {
        var uri = "scrimmages/" + getIdentifier()  + ".txt";

        try (var fileizer = new FileWriter(uri))
        {
            var startTimeString = startTime.getMinute() >= 30 ? startTime.plusHours(1).format(DateTimeFormatter.ofPattern("MM_dd_yyyy_HH")) : startTime.format(DateTimeFormatter.ofPattern("MM_dd_yyyy_HH"));
            var endTimeString = endTime.getMinute() >= 30 ? endTime.plusHours(1).format(DateTimeFormatter.ofPattern("MM_dd_yyyy_HH")) : endTime.format(DateTimeFormatter.ofPattern("MM_dd_yyyy_HH"));

            var string = teams.toString() + "\n" +
                    location.address + "_" + location.city + "_" + location.state + "_" + location.country + "_" + location.latitude + "_" + location.longitude + "\n" +
                    region.getRegionCode() + "\n" +
                    applicationStatus.toString() + "\n" +
                    organizer.getTeamNum() + "\n" +
                    sizeLimit + "\n" +
                    startTimeString + "\n" +
                    endTimeString;
            fileizer.write(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File(uri);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
