package com.scrimfinder.scrimfinder;

import com.scrimfinder.EDC.ApplicationStatus;
import com.scrimfinder.EDC.Region;
import com.scrimfinder.SearchMethods.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.scrimfinder.SearchMethods.SearchFactory.PARSER;
import static com.scrimfinder.scrimfinder.MainConstants.*;

@Configuration
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableScheduling
@Controller
//@RequestMapping("srimfinder/api/v1")
public class ServerRunner {
    Main mane;
    ObjectMapper oMapper;
    ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();

    @Scheduled(cron = "59 59 * * * *")
    public void hourlyUpdate() {
        try{
            mane.removeOutdatedScrims();
        } catch (Exception e) {
            //cope :sob: go get a blt twin go rn rn go GO
        }
    }

    @Autowired
    public ServerRunner(ObjectMapper objectMapper, Main m) throws IOException, InterruptedException, TimeoutException {
        oMapper = objectMapper;
        mane = m;
        mane.loadScrims();
        mane.loadTeams();
    }

    @GetMapping("/homepage")
    public String homepage(Model model) {
        try {
            mane.loadTeams();
            mane.loadScrims();

            ArrayList<ScrimmageImpl> scrims = mane.findScrims(SearchFactory.buildScrimSearch());
            scrims.sort(Comparator.comparing(o -> o.endTime));
            List<ScrimmageImpl> top_scrims = scrims.subList(0, Math.min(6, scrims.size()));

            var cities = new ArrayList<String>();
            var temp = "";
            for (ScrimmageImpl scrim : scrims) {
                temp = scrim.location.city + ", " + scrim.location.state;
                if (!cities.contains(temp))
                    cities.add(temp);
            }

            model.addAttribute("scrims", top_scrims);
            model.addAttribute("total_scrims", scrims);
            model.addAttribute("cities_represented", cities.size());
            model.addAttribute("teams", mane.findTeams(SearchFactory.buildTeamSearch()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

        return "homepage";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            mane.loadTeams();
            mane.loadScrims();

            ArrayList<ScrimmageImpl> scrims = mane.findScrims(SearchFactory.buildScrimSearch());
            scrims.sort(Comparator.comparing(o -> o.endTime));
            List<ScrimmageImpl> top_scrims = scrims.subList(0, Math.min(6, scrims.size()));

            var cities = new ArrayList<String>();
            var temp = "";
            for (ScrimmageImpl scrim : scrims) {
                temp = scrim.location.city + ", " + scrim.location.state;
                if (!cities.contains(temp))
                    cities.add(temp);
            }

            model.addAttribute("scrims", top_scrims);
            model.addAttribute("total_scrims", scrims);
            model.addAttribute("cities_represented", cities.size());
            model.addAttribute("teams", mane.findTeams(SearchFactory.buildTeamSearch()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

        return "dashboard";
    }

    @GetMapping("/searchScrims")
    public String sScrims(Model model,
                          @RequestParam(value = "identifier", required = false) String identifier,
                          @RequestParam(value = "region", required = false) String region,
                          @RequestParam(value = "startTime", required = false) String sdatetime,
                          @RequestParam(value = "endTime", required = false) String edatetime,
                          @RequestParam(value = "date", required = false) String date,
                          @RequestParam(value = "teamInScrim", required = false) String teamInScrim,
                          @RequestParam(value = "appStatus", required = false) String appStatus)
    {
        try {
            mane.loadScrims();
            mane.loadTeams();

            var fullList = mane.findScrims(SearchFactory.SCRIM_DEFAULT);
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

            model.addAttribute("scrims", fullyFilteredList);
            model.addAttribute("jsonScrims", arNode.toString());

            return "fscrims";
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

    }

    @GetMapping("/team/{id}")
    public String team(Model model,
                       @NonNull @PathVariable int id) {
        try {
            mane.loadTeams();
            mane.loadScrims();

            Team team = null;
            team = mane.findTeam(id);
            model.addAttribute("team", team);

            ArrayList<ScrimmageImpl> otemp = new ArrayList<>();
            for (LimitedScrim organizedScrimmage : team.getOrganizedScrimmages()) {
                otemp.add(ScrimmageImpl.fromLimitedScrim(organizedScrimmage));
            }

            ArrayList<ScrimmageImpl> atemp = new ArrayList<>();
            for (LimitedScrim activeScrim : team.getActiveScrimmages()) {
                atemp.add(ScrimmageImpl.fromLimitedScrim(activeScrim));
            }

            model.addAttribute("orgscrims", otemp);
            model.addAttribute("attscrims", atemp);
            model.addAttribute("team", team);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

        return "team";
    }

    @GetMapping("/scrim/{idUnprocessed}")
    public String scrim(Model model,
                       @NonNull @PathVariable String idUnprocessed) {
        try {
            mane.loadScrims();
            mane.loadTeams();

            String id = URLDecoder.decode(idUnprocessed, StandardCharsets.UTF_8);

            ScrimmageImpl scrim = mane.findScrims(SearchFactory.buildScrimSearch(id)).getFirst();
            model.addAttribute("scrim", scrim);

            model.addAttribute("tInScrim", scrim.teamsInScrim());
            model.addAttribute("orgTeam", scrim.organizer);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

        return "scrim";
    }

    @GetMapping("/auth-page")
    public String login(Model model) {
        return "auth-page";
    }


    @GetMapping("/homepage-redir")
    String homepageredir(Model model) {
        return "homepage-redir";
    }

    @GetMapping("/manageScrims")
    public String mScrims(Model model) {
        try {
            mane.loadScrims();
            mane.loadTeams();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().toString();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().toString();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).toString();
        }

        return "manageScrims";
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
            @NonNull @RequestParam(value = "scrimID") String scrimID,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {

            if (getUserInfo0(principal).getTeamNum() != (id))
                return ResponseEntity.status(403).build();


            mane.loadTeams();
            mane.loadScrims();

            ScrimmageImpl scrim = mane.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            Team team = mane.findTeams(SearchFactory.buildTeamSearch(id)).getFirst(); //slop but get owned ig


            return ResponseEntity.ok(mane.joinScrim(team, scrim));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
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
            @NonNull @RequestParam(value = "scrimID") String scrimID,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            mane.loadTeams();
            mane.loadScrims();

            ScrimmageImpl scrim = mane.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            Team team = mane.findTeams(SearchFactory.buildTeamSearch(id)).getFirst(); //slop but get owned ig

            if (getUserInfo0(principal).getTeamNum() != (id))
                return ResponseEntity.status(403).build();

            return ResponseEntity.ok(mane.leaveScrim(team, scrim));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping(value = "/createUser")
    ResponseEntity<Boolean> createUser(@RequestBody JsonNode jNode) {
        try {
            if (!rrwl.writeLock().tryLock(1000, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException("Timeout");
            }

            File file = new File("src/main/resources/plsnolook/users.json");

            var team = Team.handleCreation(jNode);
            ObjectNode smthsmth = oMapper.readTree(file).asObject();
            smthsmth.put(jNode.get("loginUser").asString(), team.getTeamNum());

            oMapper.writerWithDefaultPrettyPrinter().writeValue(file, smthsmth);
            team.saveToFile();
        } catch (IOException e) {
            return ResponseEntity.status(418).build();
        } catch (InterruptedException | TimeoutException e) {
            return ResponseEntity.status(500).build();
        } finally {
            rrwl.writeLock().unlock();
        }

        return ResponseEntity.ok(true);
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
    ResponseEntity<Boolean> createScrim(
            @RequestBody JsonNode jNode,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            mane.loadScrims();
            mane.loadTeams();

            var scrim = ScrimmageImpl.fromJNode(jNode);
            if (getUserInfo0(principal).equals(scrim.organizer))
                return ResponseEntity.status(403).build();

            scrim.saveToFile();

            mane.loadTeams();
            mane.loadScrims();
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | TimeoutException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
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
            @RequestParam(value = "size", required = false) Integer size,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            mane.loadScrims();
//          scrimID = scrimID.replace("%20", " ");
            ScrimmageImpl scrim = mane.findScrims(SearchFactory.buildScrimSearch(scrimID)).getFirst();
            ScrimmageImpl updatedScrim = new ScrimmageImpl(scrim);

            if (!getUserInfo0(principal).equals(updatedScrim.organizer))
                return ResponseEntity.status(403).build();

            if (region != null) {
                scrim.setRegion(Region.fromCode(region));
            }

            if (appStatus != null) {
                scrim.setApplicationStatus(ApplicationStatus.fromStatusString(appStatus));
            }

            if (size != null) {
                scrim.setSizeLimit(size);
            }

            mane.updateScrim(scrim, updatedScrim);
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
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
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "region", required = false) String region,
            @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (getUserInfo0(principal).getTeamNum() != (id))
                return ResponseEntity.status(403).build();


            mane.loadTeams();
            Team oldTeam = mane.findTeam(id);
            Team newTeam = new Team(oldTeam);

            if (region != null) {
                newTeam.setRegion(Region.fromCode(region));
            }

            if (name != null) {
                newTeam.setTeamName(name);
            }

            if (email != null) {
                newTeam.setEmail(email);
            }

            mane.updateTeam(oldTeam, newTeam);
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
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
            var fullList = mane.findScrims(SearchFactory.SCRIM_DEFAULT);
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

    @GetMapping("/authName")
    ResponseEntity<JsonNode> authName(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(oMapper.createObjectNode().putPOJO("name", principal.getAttribute("name")));
    }

    @GetMapping("/authcheck")
    ResponseEntity<JsonNode> checkLogin(@AuthenticationPrincipal OAuth2User principal) {
        try {
            principal.getAttribute("name"); //scuffed, but should trhwo error
            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", true));
        } catch (NullPointerException e) {
            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", false));
        }
    }

    @GetMapping("/new-user-check")
    ResponseEntity<JsonNode> checkNewUser (@AuthenticationPrincipal OAuth2User principal) {
        try {
            rrwl.readLock().lock();
            String name = principal.getAttribute("name"); //scuffed, but should trhwo error

            JsonNode jsonNode = oMapper.readTree("src/main/resources/plsnolook/users.json");

            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", true).put("new-user", !jsonNode.has(name)));
        } catch (NullPointerException e) {
            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", false));
        } finally {
            rrwl.readLock().unlock();
        }
    }

    @GetMapping("/getUserInfo")
    ResponseEntity<JsonNode> getUserInfo (@AuthenticationPrincipal OAuth2User principal) {
        try {
            mane.loadTeams();
            mane.loadScrims();

            rrwl.readLock().lock();

            String name = principal.getAttribute("name"); //scuffed, but should trhwo error
            if (name == null || name.isEmpty()) {
                return ResponseEntity.ok(oMapper.createObjectNode().put("authed", false));
            }

            JsonNode jsonNode = oMapper.readTree("src/main/resources/plsnolook/users.json");

            Team team = mane.findTeam(jsonNode.get(name).asInt());

            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", true).putPOJO("user", team));
        } catch (NullPointerException e) {
            return ResponseEntity.ok(oMapper.createObjectNode().put("authed", false));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        } catch (TimeoutException e) {
            return ResponseEntity.status(418).build();
        } finally {
            rrwl.readLock().unlock();
        }
    }

    private Team getUserInfo0 (@AuthenticationPrincipal OAuth2User principal) throws AuthorizationDeniedException, RuntimeException{
        try {

            mane.loadTeams();
            mane.loadScrims();

            rrwl.readLock().lock();

            String name = principal.getAttribute("name"); //scuffed, but should trhwo error
            if (name == null || name.isEmpty()) {
                throw new AuthorizationDeniedException("User not authenticated");
            }
            JsonNode jsonNode = oMapper.readTree("src/main/resources/plsnolook/users.json");

            return mane.findTeam(jsonNode.get(name).asInt());

        } catch (NullPointerException e) {
            throw new AuthorizationDeniedException(e.toString());
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        } finally {
            rrwl.readLock().unlock();
        }
    }

    @Value("${app.custom.api.gmap}")
    private String apiUrl;
    @GetMapping("/gmapApi")
    ResponseEntity<JsonNode> gmapApi() {
        return ResponseEntity.ok(oMapper.createObjectNode().putPOJO("url", apiUrl));
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
            mane.saveTeams();

            var fullList = mane.findTeams(SearchFactory.TEAM_DEFAULT);
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
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
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
        return "ExplodesmindwithMIND";
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
            @NonNull @PathVariable String scrimID,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            ScrimmageImpl scimpl = ScrimmageImpl.fromFile(new File(SCRIM_PATH + scrimID + ".json"));

            if(getUserInfo0(principal).equals(scimpl.organizer))
                return ResponseEntity.ok(mane.deleteScrim(scimpl));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (IOException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        //SHOULD NEVER HAPPEN :0
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping(value = "/deleteTeam/{teamID}")
    ResponseEntity<Boolean> delTeam(
            @NonNull @PathVariable int teamID,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            if(getUserInfo0(principal).getTeamNum() == (teamID))
                return ResponseEntity.ok(mane.deleteTeam(Team.of(new File(TEAM_PATH + teamID + ".json"))));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (IOException | InterruptedException | TimeoutException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }

        return ResponseEntity.status(500).build();

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
        SpringApplication.run(ServerRunner.class, args);
    }
}