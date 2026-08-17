package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.ApplicationStatus;
import com.scrimfinder.EDC.Region;
import com.scrimfinder.SearchMethods.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

import static com.scrimfinder.SearchMethods.SearchFactory.PARSER;
import static com.scrimfinder.scrimfinder.MainConstants.*;

@RestController
@SpringBootApplication
//@RequestMapping("srimfinder/api/v1")
public class ServerRunner {
    Main main;
    ObjectMapper oMapper;

    @Autowired
    public ServerRunner(ObjectMapper objectMapper, Main m) throws IOException, InterruptedException, TimeoutException {
        oMapper = objectMapper;
        main = m;
        main.loadScrims();
        main.loadTeams();
    }

    /**
     * WEIRDLY NAMED
     * but updates team to attend a scrim
     *
     * @param id the id of the team to be added to a scrim
     * @param scrimID the scrimmage id to be added (formatted as identifier param)
     */
    @PutMapping("/teamJoinScrim/{id}")
    ResponseEntity<Boolean> tJoinScrim(
            @NonNull @PathVariable int id,
            @NonNull @RequestParam(value = "scrimID") String scrimID
    ) {
        try {
            main.loadTeams();
            main.loadScrims();

            ScrimmageImpl scrim = main.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            Team team = main.findTeams(SearchFactory.buildTeamSearch(id)).getFirst(); //slop but get owned ig


            return ResponseEntity.ok(main.joinScrim(team, scrim));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * WEIRDLY NAMED
     * but updates team to not attend a scrim
     *
     * @param id the id of the team to be removed from a scrim
     * @param scrimID the scrimmage id to be removed (formatted as identifier param)
     */
    @PutMapping("/teamLeaveScrim/{id}")
    ResponseEntity<Boolean> tLeaveScrim(
            @NonNull @PathVariable int id,
            @NonNull @RequestParam(value = "scrimID") String scrimID
    ) {
        try {
            main.loadTeams();
            main.loadScrims();

            ScrimmageImpl scrim = main.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            Team team = main.findTeams(SearchFactory.buildTeamSearch(id)).getFirst(); //slop but get owned ig

            return ResponseEntity.ok(main.leaveScrim(team, scrim));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/createTeam")
    ResponseEntity<Boolean> createTeam(@RequestBody JsonNode jNode) {
        try {
            var team = Team.handleCreation(jNode);
            team.saveToFile();
        } catch (IOException e) {
            return ResponseEntity.status(418).build();
        }

        return ResponseEntity.ok(true);
    }


    /**
     * CREATES A SCRIM
     *
     * @param jNode json version of a scrim to be created
     * @return whether it was successfully created (or an error)
     */
    @PostMapping(value = "/createScrim")
    ResponseEntity<Boolean> createScrim(@RequestBody JsonNode jNode) {
        try {
            var scrim = ScrimmageImpl.fromJNode(jNode);
            scrim.saveToFile();
        } catch (IOException e) {
            return ResponseEntity.status(418).build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Modify a certain scrimmage
     *
     * @param scrimID REQUIRED scrimmage identifier of scrim to be modified
     * @param region denoted by "region" in the request header, defines a new region
     * @param appStatus denoted by appStatus in the request header, defines a new app status
     * @param size denoted by size in the request header, defines a new size
     * @return whether twas successful (or an error)
     */
    @PutMapping(value = "/modifyScrim/{scrimID}")
    ResponseEntity<Boolean> modScrim(
            @NonNull @PathVariable String scrimID,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "appStatus", required = false) String appStatus,
            @RequestParam(value = "size", required = false) Integer size) {
        try {
            main.loadScrims();
//          scrimID = scrimID.replace("%20", " ");
            ScrimmageImpl scrim = main.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            ScrimmageImpl updatedScrim = new ScrimmageImpl(scrim);

            if (region != null) {
                scrim.setRegion(Region.fromCode(region));
            }

            if (appStatus != null) {
                scrim.setApplicationStatus(ApplicationStatus.fromStatusString(appStatus));
            }

            if (size != null) {
                scrim.setSizeLimit(size);
            }

            main.updateScrim(scrim, updatedScrim);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     *
     * @param id
     * @param name
     * @param region
     * @return
     */
    @PutMapping(value = "/modifyTeam/{id}")
    ResponseEntity<Boolean> modTeam(
            @NonNull @PathVariable int id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "region", required = false) String region) {
        try {
            main.loadTeams();
            Team oldTeam = main.findTeam(id);
            Team newTeam = new Team(oldTeam);

            if (region != null) {
                newTeam.setRegion(Region.fromCode(region));
            }

            if (name != null) {
                newTeam.setTeamName(name);
            }

            main.updateTeam(oldTeam, newTeam);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(true);
    }

    /*
    🛏️🛏️🛏️🛌🛌🛌😴😴😴
     */

    //TODO HOLY CRIMES YOU NEED TO DOCUMENT
    @RequestMapping(value = "/getScrims", method = RequestMethod.GET)
    ResponseEntity<JsonNode> getScrims(
            @RequestParam(value = "identifier", required = false) String identifier,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "startTime", required = false) String sdatetime,
            @RequestParam(value = "endTime", required = false) String edatetime,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "teamInScrim", required = false) String teamInScrim,
            @RequestParam(value = "appStatus", required = false) String appStatus)
            //add loc?
    {
        try {
            var fullList = main.findScrims(SearchFactory.SCRIM_DEFAULT);
            Region reg = region != null ? Region.valueOf(region) : null;
            var regionList = Main.findScrims(fullList, SearchFactory.buildScrimSearch(reg));

            LocalDateTime sdt = null;
            LocalDateTime edt = null;
            if (!(sdatetime == null && edatetime == null)) {
                sdt = sdatetime != null ? LocalDateTime.parse(sdatetime, PARSER) : LocalDateTime.of(1,1,1,1,1);
                edt = edatetime != null ? LocalDateTime.parse(edatetime, PARSER) : LocalDateTime.of(3000,12,31,23,59);
            }
            var rangedList = Main.findScrims(regionList, SearchFactory.buildScrimSearch(sdt, edt));

            LocalDate ld = date != null ? LocalDateTime.parse(date, PARSER).toLocalDate() : null;
            var datedList = Main.findScrims(rangedList, SearchFactory.buildScrimSearch(ld));

            Team team = teamInScrim != null ? new Team(Integer.parseInt(teamInScrim)) : null;
            var teamList = Main.findScrims(datedList, SearchFactory.buildScrimSearch(team));

            var identifierList = Main.findScrims(teamList, SearchFactory.buildScrimSearch(identifier));

            var fullyFilteredList = Main.findScrims(identifierList, SearchFactory.buildScrimSearch(ApplicationStatus.fromStatusString(appStatus)));

            var arNode = oMapper.createArrayNode();
            for (Scrimmage scrim : fullyFilteredList) {
                arNode.add(scrim.getONode(oMapper));
            }

            return ResponseEntity.ok(arNode);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
    }

    //TODO HOLY CRIMES YOU NEED TO DOCUMENT
    @RequestMapping(value = "/getTeams", method = RequestMethod.GET)
    ResponseEntity<JsonNode> getTeams(
            @RequestParam(value = "tNum", required = false) String tNum,
            @RequestParam(value = "tName", required = false) String tName,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "organizedScrims", required = false) String oScrim,
            @RequestParam(value = "activeScrims", required = false) String aScrim)
    {
        try {
            main.saveTeams();

            var fullList = main.findTeams(SearchFactory.TEAM_DEFAULT);
            Region reg = region != null ? Region.valueOf(region) : null;
            var regionList = Main.findTeams(fullList, SearchFactory.buildTeamSearch(reg));
            Integer team = tNum != null ? Integer.parseInt(tNum) : null;
            var numberedList = Main.findTeams(regionList, SearchFactory.buildTeamSearch(team));
            var namedList = Main.findTeams(numberedList, SearchFactory.buildTeamSearch(tName));
            var oScrimmedList  = Main.findTeams(namedList, SearchFactory.buildTeamSearch(oScrim, true));
            var fullFilterList = Main.findTeams(oScrimmedList, SearchFactory.buildTeamSearch(aScrim, false));

            var arNode = oMapper.createArrayNode();
            for (Team scrim : fullFilterList) {
                arNode.add(scrim.getONode(oMapper));
            }

            return ResponseEntity.ok(arNode);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        } catch (IOException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    /*@RequestMapping(value = "/getTeams", method = RequestMethod.GET)
    ResponseEntity<JsonNode> getTeams() {
        try {
            var arNode = oMapper.createArrayNode();
            for (Team team : main.findTeams(TeamSearch.ALWAYS_FOUND)) {
                arNode.add(team.getONode(oMapper));
            }

            return ResponseEntity.ok(arNode);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }*/

    @RequestMapping("/")
    String explode() {
        return "Explodes mind with MIND";
    }

/*
    @RequestMapping(value = "/error")
    ResponseEntity<byte[]> error() {
        try {
            Path path = Paths.get("src/main/resources/plsnolook/cope.png");
            byte[] imageBytes = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

TS CODE EMBARRESED ME INFRONT OF A META DEV AIFHEiuAFNHEOEFBouoIAEFbuhEHfiWPFEHu
*/

    //TODO ADD DEL METHOD
    @DeleteMapping(value = "/deleteScrim/{scrimID}")
    ResponseEntity<Boolean> delScrim(
            @NonNull @PathVariable String scrimID
    ) {
        try {
            return ResponseEntity.ok(main.deleteScrim(ScrimmageImpl.fromFile(new File(SCRIM_PATH + scrimID + ".txt"))));
        } catch (IOException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    @DeleteMapping(value = "/deleteTeam/{teamID}")
    ResponseEntity<Boolean> delTeam(
            @NonNull @PathVariable int team
    ) {
        try {
            return ResponseEntity.ok(main.deleteTeam(Team.of(new File(TEAM_PATH + team + ".txt"))));
        } catch (IOException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
    }

    @RequestMapping(value = "/wSpeed")
    ResponseEntity<byte[]> wSpeed() {
        try {
            Path path = Paths.get("src/main/resources/plsnolook/wspeed.png");
            byte[] imageBytes = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public static void main(String[] args) {
        try (var main = new Main()) {
            SpringApplication.run(ServerRunner.class, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}