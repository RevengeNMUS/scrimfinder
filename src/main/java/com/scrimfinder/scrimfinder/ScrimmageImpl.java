package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;


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
    public static final ScrimmageImpl NULL_SCRIM = new ScrimmageImpl(new ArrayList<>(), Location.NULL_LOCATION, Region.KNOWHERE, ApplicationStatus.CLOSED, Team.NULL_TEAM, 0, LocalDateTime.of(0, 1, 1, 0, 0), LocalDateTime.of(0, 1, 1, 0, 0));

    public ArrayList<Team> teams;
    public Location location;
    public Region region;
    public ApplicationStatus applicationStatus;
    public Team organizer;
    public int sizeLimit;
    public LocalDateTime startTime;
    public LocalDateTime endTime;

    public ScrimmageImpl(ArrayList<Team> teams, Location location, Region region, ApplicationStatus applicationStatus, Team organizer, int sizeLimit, LocalDateTime startTime, LocalDateTime endTime) {
        this.teams = teams;
        this.location = location;
        this.region = region;
        this.applicationStatus = applicationStatus;
        this.organizer = organizer;
        this.sizeLimit = sizeLimit;
        this.startTime = startTime.plusMinutes(startTime.getMinute()%15 < 8 ? -(startTime.getMinute()%15) : (15-startTime.getMinute()%15)).withSecond(0).withNano(0); //round to 67
        this.endTime = endTime.plusMinutes(endTime.getMinute()%15 < 8 ? -(endTime.getMinute()%15) : (15-endTime.getMinute()%15)).withSecond(0).withNano(0);
    }

    public static ScrimmageImpl fromFile(File file) {
        ObjectMapper oMapper = new ObjectMapper();
        String fileString;
        //todo impl it actually maybe :0
        try (Scanner reeder = new Scanner(file)) {
            StringBuilder string = new StringBuilder();
            while (reeder.hasNextLine()) {
                string.append(reeder.nextLine()).append(" ");
            }
            JsonNode jsonNode = oMapper.readTree(file);

            return fromJNode(jsonNode);
        } catch (IOException e) {
            return NULL_SCRIM;
        }
    }

    public static ScrimmageImpl fromJNode(JsonNode jsonNode) throws IOException {

        ArrayList<Team> teams = new ArrayList<>();
        var tArrNode = jsonNode.get("teams").asArray();
        for (JsonNode tNode : tArrNode.elements()) {
            teams.add(Team.of(new File(MainConstants.TEAM_PATH + tNode.get("teamNum").asInt(0) + ".txt"))); // mark all teams as atendees for ts srim you drat
        }

        var locNode = jsonNode.get("location");
        var location = new Location(locNode.get("longitude").asDouble(0.0), locNode.get("latitude").asDouble(0.0), locNode.get("address").asString(" "), locNode.get("city").asString(" "), locNode.get("state").asString(" "), locNode.get("country").asString(" "));

        var region = Region.fromCode(jsonNode.get("region").asString());

        var applicationStatus = ApplicationStatus.valueOf(jsonNode.get("appStatus").asString("CLOSED"));

        Team organizer;
        try {
            organizer = Team.of(new File(MainConstants.TEAM_PATH + jsonNode.get("organizer").get("teamNum").asInt(0) + ".txt"));
        } catch (FileNotFoundException e) {
            organizer = new Team(jsonNode.get("organizer").get("teamNum").asInt(0));
        }

        var sizeLimit = jsonNode.get("size").asInt(1);

        var parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        var startDT = jsonNode.get("startTime").asString("0000-00-00'T'00:00:00");
        var startTime = LocalDateTime.parse(startDT, parser);
        var endDT = jsonNode.get("endTime").asString("0000-00-00'T'00:00:00");
        var endTime = LocalDateTime.parse(endDT, parser);

        var returnScrim = new ScrimmageImpl(teams, location, region, applicationStatus, organizer, sizeLimit, startTime, endTime);

        for (Team team : teams) {
            team.attendeeFor(returnScrim);
        }

        return returnScrim;
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
        return teams.stream().mapToInt(new ToIntFunction<Team>() {
            @Override
            public int applyAsInt(Team value) {
                return value.getTeamNum();
            }
        }).anyMatch(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value == t.getTeamNum();
            }
        });
    }

    @Override
    public String getIdentifier() {
        var identifier = organizer.getTeamNum() + "-" + location.city + "-" + startTime.format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
        return identifier;
    }

    @Override
    public ObjectNode getLimitedONode(ObjectMapper oMapper) {
        var oNode = oMapper.createObjectNode();
        oNode.put("identifier", getIdentifier());
        oNode.put("appStatus", applicationStatus.name());
        oNode.putPOJO("location", location);
        oNode.put("region", region.getRegionCode());
        return oNode;
    }

    @Override
    public ObjectNode getONode(ObjectMapper oMapper) {
        var oNode = oMapper.createObjectNode();
        oNode.put("identifier", getIdentifier());
        oNode.put("appStatus", applicationStatus.name());
        oNode.putPOJO("location", location);
        oNode.put("region", region.getRegionCode());
        oNode.putPOJO("organizer", organizer.getLimitedONode(oMapper));
        var tempArrN = oMapper.createArrayNode();
        for (Team team : teams) {
            tempArrN.add(team.getLimitedONode(oMapper));
        };
        oNode.putIfAbsent("teams", tempArrN);
        var parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        oNode.put("startTime", startTime.format(parser));
        oNode.putPOJO("endTime", endTime.format(parser));
        oNode.put("size", sizeLimit);

        return oNode;
    }

    /**
     * saves the scrimmages data into a file wawwww
     * when implementation is made, MAKE SURE CONSTRUCTOR CAN TAKE A FILE AS PARAM
     * you RAT
     *
     */
    @Override
    public File saveToFile() throws IOException {
        ObjectMapper om = new ObjectMapper();
        var uri = MainConstants.SCRIM_PATH + getIdentifier()  + ".txt";
        om.writeValue(new File(uri), this.getONode(om));
        return new File(uri);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
