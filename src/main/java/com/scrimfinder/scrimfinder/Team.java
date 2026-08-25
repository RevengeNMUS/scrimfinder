package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A Team! <p>
 * Has a: <br>
 * Team number<br>
 * Team name<br>
 * Region<br>
 * Email<br>
 * Active Scrims<br>
 * Scrims Organized<br>
 */
public class Team {
    //todo: add desc/contact
    private final int teamNum;
    private String teamName;
    private String email;
    private Region region;
    private final ArrayList<LimitedScrim> activeScrimmages;
    private final ArrayList<LimitedScrim> organizedScrimmages;

    public static Team NULL_TEAM = new Team(-1);

    public int getTeamNum() {
        return teamNum;
    }

    public Region getRegion() {
        return region;
    }
    public void setRegion(Region newregion) {region = newregion;}

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String newtName) {teamName = newtName;}

    public ArrayList<LimitedScrim> getActiveScrimmages() {
        return activeScrimmages;
    }

    public ArrayList<LimitedScrim> getOrganizedScrimmages() {
        return organizedScrimmages;
    }

    public Team(int tNum, String tName, Region reg, ArrayList<LimitedScrim> aScrims, ArrayList<LimitedScrim> oScrims, String em) {
        teamNum = tNum;
        teamName = tName;
        region = reg;
        activeScrimmages = aScrims;
        organizedScrimmages = oScrims;
        email = em;
    }

    public Team(int tNum, String tName, String em, Region reg) {
        teamNum = tNum;
        teamName = tName;
        region = reg;
        email = em;
        activeScrimmages = new ArrayList<>();
        organizedScrimmages = new ArrayList<>();
    }

    public Team(int tNum) {
        teamNum = tNum;
        teamName = "N/A";
        email= "amongus@gmail.com";
        region = Region.KNOWHERE;
        activeScrimmages = new ArrayList<>();
        organizedScrimmages = new ArrayList<>();
    }

    public Team(Team team) {
        teamNum = team.teamNum;
        teamName = team.teamName;
        region = team.region;
        activeScrimmages = team.activeScrimmages;
        organizedScrimmages = team.organizedScrimmages;
        email = team.email;
    }

    public static Team of(File file) throws FileNotFoundException {
        ObjectMapper oMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = oMapper.readTree(file);
            return fromJNode(jsonNode);
        } catch (IOException e) {
            return NULL_TEAM;
        }
    }

    public static Team fromJNode(JsonNode jsonNode) throws IOException {
        int teamNum = jsonNode.get("tNum").asInt(0);
        String email = jsonNode.get("email").asString("amongus@gmail.com");
        String teamName = jsonNode.get("tName").asString("");
        Region region = Region.fromCode(jsonNode.get("region").asString("KNOWHERE"));

        ArrayNode arNo = jsonNode.get("activeScrims").asArray();
        ArrayList<LimitedScrim> activeScrimmages = new ArrayList<>();
        for (JsonNode node : arNo) {
            activeScrimmages.add(LimitedScrim.fromJNode(node));
        }

        arNo = jsonNode.get("organizedScrims").asArray();
        ArrayList<LimitedScrim> organizedScrims = new ArrayList<>();
        for (JsonNode node : arNo) {
            organizedScrims.add(LimitedScrim.fromJNode(node));
        }

        /*scrimListString = stingerArray[4].substring(1, stingerArray[4].length() - 1);
        organizedScrimmages = new ArrayList<Scrimmage>();
        if (!scrimListString.isBlank()) {
            var scrimList = scrimListString.split(", ");
            for (String scrim : scrimList) {
                organizedScrimmages.add(ScrimmageImpl.fromFile(new File(MainConstants.SCRIM_PATH + scrim + ".txt")));
            }
        }*/

        return new Team(teamNum, teamName, region, activeScrimmages, organizedScrims, email);
    }

    public static Team handleCreation(JsonNode jsonNode) throws IOException {
        int teamNum = jsonNode.get("tNum").asInt(0);
        String teamName = jsonNode.get("tName").asString("");
        String email = jsonNode.get("email").asString("amongus@gmail.com");
        Region region = Region.fromCode(jsonNode.get("region").asString("KNOWHERE"));

        ArrayList<LimitedScrim> activeScrimmages = new ArrayList<>();
        if(jsonNode.has("activeScrims")) {
            ArrayNode arNo = jsonNode.get("activeScrims").asArray();
            for (JsonNode node : arNo) {
                activeScrimmages.add(LimitedScrim.fromJNode(node));
            }
        }

        ArrayList<LimitedScrim> organizedScrims = new ArrayList<>();
        if(jsonNode.has("organizedScrims")) {
            ArrayNode arNo = jsonNode.get("organizedScrims").asArray();
            for (JsonNode node : arNo) {
                organizedScrims.add(LimitedScrim.fromJNode(node));
            }
        }

        /*scrimListString = stingerArray[4].substring(1, stingerArray[4].length() - 1);
        organizedScrimmages = new ArrayList<Scrimmage>();
        if (!scrimListString.isBlank()) {
            var scrimList = scrimListString.split(", ");
            for (String scrim : scrimList) {
                organizedScrimmages.add(ScrimmageImpl.fromFile(new File(MainConstants.SCRIM_PATH + scrim + ".txt")));
            }
        }*/

        return new Team(teamNum, teamName, region, activeScrimmages, organizedScrims, email);
    }

    public boolean isAttending(String scrimmage) {
        return activeScrimmages.stream().map(
                limitedScrim -> limitedScrim.identifier
        ).anyMatch(
                s -> s.equals(scrimmage)
        ) || isOrganizing(scrimmage);
    }
    public boolean isOrganizing(String scrimmage) {
        return organizedScrimmages.stream().map(
            limitedScrim -> limitedScrim.identifier
        ).anyMatch(
            s -> s.equals(scrimmage)
        );
    }

    public boolean attendeeFor(LimitedScrim scrimmage) {
        if (!isAttending(scrimmage.identifier)) {
            activeScrimmages.add(scrimmage);
            return true;
        }

        return false;
    }

    public boolean notAttending(LimitedScrim scrimmage) {
        if (isAttending(scrimmage.identifier)) {
            activeScrimmages.remove(scrimmage);
            return true;
        }

        return false;
    }

    public boolean notOrganizing(LimitedScrim scrimmage) {
        if (isOrganizing(scrimmage.identifier)) {
            organizedScrimmages.remove(scrimmage);
            return true;
        }

        return false;
    }

    public void organizerFor(LimitedScrim scrimmage) {
        if(!isOrganizing(scrimmage.identifier))
            organizedScrimmages.add(scrimmage);
    }

    /**
     * IMPLIMENT IT YOUBHB KJSGFVSG<FU
     * saves team data in a file!
     */
    public File saveToFile() throws IOException {
        var oMap = new ObjectMapper();
        var uri = MainConstants.TEAM_PATH + teamNum  + ".json";
        oMap.writeValue(new File(uri), this.getONode(oMap));
        return new File(uri);
    }

    public boolean deleteFile() {
        var uri = MainConstants.TEAM_PATH + teamNum  + ".json";
        File file = new File(uri);
        return file.delete();
        //goonbye team :>
    }

    public ObjectNode getONode(ObjectMapper objectMapper) {
        var oNode = objectMapper.createObjectNode();


        oNode.put("tNum", teamNum);
        oNode.put("tName", teamName);
        oNode.put("email", email);
        oNode.putPOJO("region", getRegion());
        var tempArrN = objectMapper.createArrayNode();
        for (LimitedScrim aScrim : activeScrimmages.stream().distinct().toList()) {
            tempArrN.add(aScrim.getONode(objectMapper));
        }
        oNode.putIfAbsent("activeScrims", tempArrN);

        var anothertempArrN = objectMapper.createArrayNode();
        for (LimitedScrim oScrim : organizedScrimmages.stream().distinct().toList()) {
            anothertempArrN.add(oScrim.getONode(objectMapper));
        }
        oNode.putIfAbsent("organizedScrims", anothertempArrN);

        return oNode;
    }

    public ObjectNode getLimitedONode(ObjectMapper objectMapper) {
        var tNode = objectMapper.createObjectNode();
        tNode.putPOJO("teamName", getTeamName());
        tNode.putPOJO("teamNum", getTeamNum());
        tNode.putPOJO("region", getRegion());
        tNode.putPOJO("email", getEmail());
        return tNode;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team team)) return false;
        return teamNum == team.teamNum &&
                Objects.equals(teamName, team.teamName);
    }

    @Override
    public String toString() {
        return String.valueOf(teamNum);
    }

    public void setEmail(String email) {
        this.email = email;
    }
}