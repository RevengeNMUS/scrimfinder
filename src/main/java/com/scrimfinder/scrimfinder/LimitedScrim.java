package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.ApplicationStatus;
import com.scrimfinder.EDC.Location;
import com.scrimfinder.EDC.Region;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class LimitedScrim {
    public static final com.scrimfinder.scrimfinder.ScrimmageImpl NULL_SCRIM = new com.scrimfinder.scrimfinder.ScrimmageImpl(new ArrayList<>(), Location.NULL_LOCATION, Region.KNOWHERE, ApplicationStatus.CLOSED, Team.NULL_TEAM, 0, LocalDateTime.of(0, 1, 1, 0, 0), LocalDateTime.of(0, 1, 1, 0, 0));
    private static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public String identifier;
    public Location location;
    public Region region;
    public ApplicationStatus applicationStatus;
    public LocalDateTime startTime;

    public LimitedScrim(String identifier, Location location, Region region, ApplicationStatus applicationStatus, LocalDateTime startTime) {
        this.identifier = identifier;
        this.location = location;
        this.region = region;
        this.applicationStatus = applicationStatus;
        this.startTime = startTime.plusMinutes(startTime.getMinute()%15 < 8 ? -(startTime.getMinute()%15) : (15-startTime.getMinute()%15)).withSecond(0).withNano(0); //round to 67
    }

    public LimitedScrim(Scrimmage scrim) {
        this.identifier = scrim.getIdentifier();
        this.location = scrim.locationOfScrim();
        this.region = scrim.regionOfScrim();
        this.applicationStatus = scrim.appStatus();
        this.startTime = scrim.startTime();
        startTime = startTime.plusMinutes(startTime.getMinute()%15 < 8 ? -(startTime.getMinute()%15) : (15-startTime.getMinute()%15)).withSecond(0).withNano(0); //round to 67
    }

    public static LimitedScrim fromJNode(JsonNode jsonNode) throws IOException {

        var identifier = jsonNode.get("identifier").asString("");
        var locNode = jsonNode.get("location");
        var location = new Location(locNode.get("longitude").asDouble(0.0), locNode.get("latitude").asDouble(0.0), locNode.get("address").asString(" "), locNode.get("city").asString(" "), locNode.get("state").asString(" "), locNode.get("country").asString(" "));
        var region = Region.fromCode(jsonNode.get("region").asString("KNOWHERE"));
        var applicationStatus = ApplicationStatus.valueOf(jsonNode.get("appStatus").asString("CLOSED"));

        var startDT = jsonNode.get("startTime").asString("0000-00-00'T'00:00:00");
        var startTime = LocalDateTime.parse(startDT, parser);

        return new LimitedScrim(identifier, location, region, applicationStatus, startTime);
    }

    /**
     * Sets the start time for the event
     *
     * @param st start time
     */
    public void setStartTime(LocalDateTime st) {
        startTime = st;
    }

    /**
     * Sets the Location for the event
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets the Region for the event
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * Sets the ApplicationStatus for the event
     */
    public void setApplicationStatus(ApplicationStatus appStatus) {
        this.applicationStatus = appStatus;
    }

    /**
     * @return the {@link Location} of the scrimmage
     */
    public Location locationOfScrim() {
        return location;
    }

    /**
     * @return the region that the Scrimmage will happen in
     * Used for filtering results?
     */
    public Region regionOfScrim() {
        return region;
    }

    /**
     * @return this scrimmage's {@link ApplicationStatus} (closed, open, etc)
     */
    public ApplicationStatus appStatus() {
        return applicationStatus;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ObjectNode getONode(ObjectMapper oMapper) {
        var oNode = oMapper.createObjectNode();
        oNode.put("identifier", getIdentifier());
        oNode.put("appStatus", applicationStatus.name());
        oNode.putPOJO("location", location);
        oNode.put("region", region.getRegionCode());

        var parser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        oNode.put("startTime", startTime.format(parser));

        return oNode;
    }

    /**
     * saves the scrimmages data into a file wawwww
     * when implementation is made, MAKE SURE CONSTRUCTOR CAN TAKE A FILE AS PARAM
     * you RAT
     *
     */
    public File saveToFile() throws IOException {
        ObjectMapper om = new ObjectMapper();
        var uri = MainConstants.SCRIM_PATH + getIdentifier()  + ".txt";
        om.writeValue(new File(uri), this.getONode(om));
        return new File(uri);
    }

    public String toString() {
        return getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LimitedScrim that)) return false;
        return Objects.equals(identifier, that.identifier) && Objects.equals(location, that.location) && region == that.region && applicationStatus == that.applicationStatus && Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, location, region, applicationStatus, startTime);
    }
}