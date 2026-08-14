package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.Region;
import com.scrimfinder.SearchMethods.SearchFactory;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * scrim data, but not scrim
 */
/*
public record TeamUpdate(@Nullable String addOrganizedScrim,
                         @Nullable String addAttendeeScrim,
                         @Nullable String deleteAttendeeScrim,
                         @Nullable String delOrganizedScrim)
{
    public Team update(Team team, Main main) throws IOException, InterruptedException, TimeoutException {
        Team returnTeam;

        ObjectMapper objectMapper = new ObjectMapper();

        Region reg = (region != null) ? (Region.valueOf(region)) : (team.getRegion());
        String name = (tName != null) ? (tName) : (team.getTeamName());
        ArrayList<LimitedScrim> aScrims = team.getActiveScrimmages();
        ArrayList<LimitedScrim> oScrims = team.getOrganizedScrimmages();

        returnTeam = new Team(team.getTeamNum(), team.getTeamName(), reg, aScrims, oScrims);

        returnTeam.attendeeFor(ScrimmageImpl.fromJNode(objectMapper.readTree(addAttendeeScrim)).toLimitedScrim());
        main.joinScrim(returnTeam, ScrimmageImpl.fromJNode(objectMapper.readTree(addAttendeeScrim)));
        returnTeam.notAttending(LimitedScrim.fromJNode(objectMapper.readTree(deleteAttendeeScrim)));
        main.leaveScrim(returnTeam, ScrimmageImpl.fromJNode(objectMapper.readTree(deleteAttendeeScrim)));


        return returnTeam;
    }
}

temporarily slimed bc AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
*/