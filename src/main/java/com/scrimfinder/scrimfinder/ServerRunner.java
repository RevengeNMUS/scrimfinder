package com.scrimfinder.scrimfinder;

import com.scrimfinder.SearchMethods.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ServerRunner {
    Main main = new Main();

    public ServerRunner(Main m) {
        main = m;
    }
    @RequestMapping(value = "/getScrims", method = RequestMethod.GET)
    String getScrims() {
        try {
            StringBuilder returnString = new StringBuilder();
            var scrimList = main.findScrims(ScrimSearch.ALWAYS_FOUND);
            for (Scrimmage scrim : scrimList) {
                returnString.append(getScrimInfo(scrim)).append("<br>");
            }
            return returnString.toString();
        } catch (ResourceNotFoundException e) {
            return new RuntimeException(e).getMessage();
        }
    }

    @RequestMapping(value = "/getTeams", method = RequestMethod.GET)
    String getTeams() {
        try {
            StringBuilder returnString = new StringBuilder();
            var teamList = main.findTeams(TeamSearch.ALWAYS_FOUND);
            for (Team team : teamList) {
                returnString.append(getTeamInfo(team)).append("<br>");
            }
            return returnString.toString();
        } catch (ResourceNotFoundException e) {
            return new RuntimeException(e).getMessage();
        }
    }

    @RequestMapping("/")
    String explode() {
        return "Explodes mind with MIND";
    }

    public static void main(String[] args) {
        try (var main = new Main()) {
            main.loadTeams();
            main.loadScrims();
            SpringApplication.run(ServerRunner.class, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getScrimInfo(Scrimmage scrim) {
        StringBuilder tempString = new StringBuilder();
        for (Team team : scrim.teamsInScrim()) {
            tempString.append(team.getTeamNum()).append(", ");
        }

        return
        "<p>" +
        (scrim.getIdentifier()) + "<br>" +
        "&emsp;" + scrim.regionOfScrim() + "<br>" +
        "&emsp;" + scrim.locationOfScrim() + "<br>" +
        "&emsp;" + scrim.appStatus() + "<br>" +
        "&emsp;" + scrim.startTime()  + "<br>" +
        "&emsp;" + scrim.endTime()  + "<br>" +
        "&emsp;" + scrim.isFull() + "<br>" +
        "&emsp;" + scrim.scrimOrganizer()  + "<br>" +
        "&emsp;{" + tempString + "}" +
        "</p>";
    }

    private static String getTeamInfo(Team team) {
        return
        "<p>" +
        (team.getTeamNum()) + ":" + (team.getTeamName()) + "<br>" +
        "&emsp;" + team.getActiveScrimmages()  + "<br>" +
        "&emsp;" + team.getOrganizedScrimmages() + "<br>" +
        "&emsp;" + (team.getRegion()) +
        "</p>";
    }
}