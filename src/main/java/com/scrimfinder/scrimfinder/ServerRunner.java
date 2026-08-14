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
import static com.scrimfinder.scrimfinder.MainConstants.SCRIM_PATH;

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
    }

    /**
     * WEIRDLY NAMED
     * but updates team to attend/notattend a scrim
     *
     * @param id
     * @return
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


    @RequestMapping(value = "/createScrim", method = RequestMethod.POST)
    ResponseEntity<Boolean> createScrim(@RequestBody JsonNode jNode) {
        try {
            var scrim = ScrimmageImpl.fromJNode(jNode);
            scrim.saveToFile();
        } catch (IOException e) {
            return ResponseEntity.status(418).build();
        }

        return ResponseEntity.ok(true);
    }

    /*
    🛏️🛏️🛏️🛌🛌🛌😴😴😴
     */
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
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
    }

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