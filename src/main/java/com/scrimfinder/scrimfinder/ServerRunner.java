package com.scrimfinder.scrimfinder;

import com.scrimfinder.SearchMethods.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@RestController
@SpringBootApplication
public class ServerRunner {
    Main main;
    ObjectMapper oMapper;

    @Autowired
    public ServerRunner(ObjectMapper objectMapper, Main m) {
        oMapper = objectMapper;
        main = m;
    }
    @RequestMapping(value = "/getScrims", method = RequestMethod.GET)
    ResponseEntity<JsonNode> getScrims() {
        try {
            var arNode = oMapper.createArrayNode();
            for (ScrimmageImpl scrim : main.findScrims(ScrimSearch.ALWAYS_FOUND)) {
                arNode.add(scrim.getONode(oMapper));
            }

            return ResponseEntity.ok(arNode);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value = "/getTeams", method = RequestMethod.GET)
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
    }

    @RequestMapping(value = "/getTeam/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getTeam(@PathVariable int id) {
        try {
            StringBuilder returnString = new StringBuilder();
            Team team = main.findTeams(new TeamSearch() {
                @Override
                public boolean isFound(Team team) {
                    return team.getTeamNum() == id;
                }

                @Override
                public String finderMethod() {
                    return "team equality";
                }
            }).get(0); //slop but get owned ig
            return ResponseEntity.ok(team.getONode(oMapper));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
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
}