package com.scrimfinder.scrimfinder;

import com.scrimfinder.SearchMethods.*;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import static com.scrimfinder.scrimfinder.MainConstants.SCRIM_PATH;

@Repository
public class Main implements AutoCloseable{
    private static final ReentrantLock rwlock = new ReentrantLock(true);
    public static final ArrayList<Scrimmage> scrims = new ArrayList<>();
    public static final ArrayList<Team> teams = new ArrayList<>();

    public Main() {
        try {
            loadScrims();
            loadTeams();
        } catch (Exception e) {
            //so... clouds am i right
        }
    }

    public boolean addTeam(Team team) throws IOException, InterruptedException, TimeoutException {
        if (!teams.contains(team)) {
            teams.add(team);
        }

        for (LimitedScrim limScrim : team.getActiveScrimmages()) {
            var scrim = ScrimmageImpl.fromLimitedScrim(limScrim);
            scrim.addTeam(team);
        }

        saveTeams();
        return true;
    }

    public boolean deleteTeam(Team team) throws IOException, InterruptedException, TimeoutException {
        boolean returnBool = teams.remove(team);
        for (LimitedScrim limScrim : team.getActiveScrimmages()) {
            var scrim = ScrimmageImpl.fromLimitedScrim(limScrim);
            scrim.removeTeam(team);
        }

        for (LimitedScrim limScrim : team.getOrganizedScrimmages()) {
            var scrim = ScrimmageImpl.fromLimitedScrim(limScrim);
            deleteScrim(scrim);
        }

        team.deleteFile();
        saveScrims();
        saveTeams();
        return returnBool;
    }

    public boolean addScrim(Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        if (!scrims.contains(scrim)) {
            scrims.add(scrim);
        }
        for (Team team : scrim.teamsInScrim()) {
            if (!teams.contains(team)) {
                teams.add(team);
            }
            team.attendeeFor(scrim.toLimitedScrim());
        }

        saveScrims();
        saveTeams();
        return true;
    }

    public boolean deleteScrim(ScrimmageImpl scrim) throws IOException, InterruptedException, TimeoutException {
        boolean returnBool = scrims.remove(scrim);
        for (Team team : scrim.teamsInScrim()) {
            team.notAttending(scrim.toLimitedScrim());
        }

        scrim.deleteFile();
        saveScrims();
        saveTeams();
        return returnBool;
    }

    public boolean updateScrim(Scrimmage scrim, Scrimmage updatedScrim) throws IOException, InterruptedException, TimeoutException {
        if (!scrims.contains(scrim)) {
            throw new ResourceNotFoundException();
        }

        scrims.remove(scrim);
        scrims.add(updatedScrim);

        for (Team team : scrim.teamsInScrim()) {
            team.notAttending(scrim.toLimitedScrim());
        }

        for (Team team : updatedScrim.teamsInScrim()) {
            team.attendeeFor(updatedScrim.toLimitedScrim());
        }

        saveScrims();
        saveTeams();
        return true;
    }

    public boolean updateTeam(Team oldTeam, Team newTeam) throws IOException, InterruptedException, TimeoutException {
        if (!teams.contains(oldTeam)) {
            throw new ResourceNotFoundException();
        }

        teams.remove(oldTeam);
        teams.add(newTeam);

        for (LimitedScrim scrim : oldTeam.getActiveScrimmages()) {
            ScrimmageImpl.fromFile(new File(SCRIM_PATH + scrim.identifier + ".txt")).removeTeam(oldTeam);
        }

        for (LimitedScrim scrim : newTeam.getActiveScrimmages()) {
            ScrimmageImpl.fromFile(new File(SCRIM_PATH + scrim.identifier + ".txt")).addTeam(newTeam);
        }

        saveScrims();
        saveTeams();
        return true;
    }

    public boolean loadScrims() throws IOException, TimeoutException, InterruptedException {
        if (!rwlock.tryLock(10, TimeUnit.SECONDS)) {
            throw new TimeoutException();
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(SCRIM_PATH))) {
            scrims.clear();
            stream.forEach(path -> {
                var scrim = ScrimmageImpl.fromFile(new File(path.toUri()));
                scrims.add(scrim);
            });
            return true;
        } finally {
            rwlock.unlock();
        }
    }

    public boolean loadTeams() throws IOException, InterruptedException, TimeoutException {
        if (!rwlock.tryLock(10, TimeUnit.SECONDS)) {
            throw new TimeoutException();
        }

        teams.clear();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(MainConstants.TEAM_PATH))) {
            for (Path path : stream) {
                var team = Team.of(new File(path.toUri()));
                teams.add(team);
            }
            return true;
        } finally {
            rwlock.unlock();
        }
    }

    public boolean saveScrims() throws IOException, InterruptedException, TimeoutException {
        if (!rwlock.tryLock(10, TimeUnit.SECONDS)) {
            throw new TimeoutException();
        }

        try {
            for (Scrimmage scrim : scrims) {
                scrim.saveToFile();
            }
        } finally {
            rwlock.unlock();
        }

        return true;
    }

    public boolean saveTeams() throws IOException, TimeoutException, InterruptedException {
        if (!rwlock.tryLock(10, TimeUnit.SECONDS)) {
            throw new TimeoutException();
        }
        try {
            for (Team team : teams) {
                team.saveToFile();
            }
        } finally {
            rwlock.unlock();
        }

        return true;
    }

    public Team findTeam(int tNum) throws ResourceNotFoundException {
        for (Team team : teams) {
            if (team.getTeamNum() == tNum) {
                return team;
            }
        }

        throw new ResourceNotFoundException("Resource not found for team number " + tNum);
    }

    public Team findTeam(String tName) throws ResourceNotFoundException {
        for (Team team : teams) {
            if (team.getTeamName().equals(tName)) {
                return team;
            }
        }

        throw new ResourceNotFoundException("Resource not found for team name " + tName);
    }

    public static ArrayList<Team> findTeams(ArrayList<Team> teams, TeamSearch ts) throws ResourceNotFoundException {
        var returnList = new ArrayList<Team>();
        for (Team team : teams) {
            if (ts.isFound(team)) {
                returnList.add(team);
            }
        }

        if (returnList.isEmpty()) {
            //resort to throwing rotten tomatoes (exception) at user
            throw new ResourceNotFoundException("No resources found for  " + ts.finderMethod());
        }

        return returnList;
    }

    public ArrayList<Team> findTeams(TeamSearch ts) throws ResourceNotFoundException {
        return findTeams(teams, ts);
    }



    public ArrayList<ScrimmageImpl> findScrims(ScrimSearch ss) throws ResourceNotFoundException {
        var returnList = new ArrayList<ScrimmageImpl>();

        for (Scrimmage scrim : scrims) {
            if (ss.isFound(scrim)) {
                returnList.add((ScrimmageImpl) scrim); //SUCH SLOP OMG anweuifawuekfuawuilefkjhawilefjio
            }
        }

        if (returnList.isEmpty()) {
            //resort to throwing rotten tomatoes (exception) at user
            throw new ResourceNotFoundException("No resources found for  " + ss.finderMethod());
        }

        return returnList;
    }

    public static ArrayList<ScrimmageImpl> findScrims(ArrayList<ScrimmageImpl> scrims, ScrimSearch ss) throws ResourceNotFoundException {
        var returnList = new ArrayList<ScrimmageImpl>();

        for (ScrimmageImpl scrim : scrims) {
            if (ss.isFound(scrim)) {
                returnList.add(scrim); //SUCH SLOP OMG anweuifawuekfuawuilefkjhawilefjio
            }
        }

        if (returnList.isEmpty()) {
            //resort to throwing rotten tomatoes (exception) at user
            throw new ResourceNotFoundException("No resources found for  " + ss.finderMethod());
        }

        return returnList;
    }

    public boolean joinScrim(Team team, Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        var rBool = team.attendeeFor(scrim.toLimitedScrim());
        scrim.addTeam(team);
        //if smth dies, it dies HERE :000
        saveTeams();
        saveScrims();
        return rBool;
    }

    public boolean leaveScrim(Team team, Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        var rBool = team.notAttending(scrim.toLimitedScrim());
        scrim.removeTeam(team);
        //if smth dies, it dies HERE :000
        saveTeams();
        saveScrims();
        return rBool;
    }

    @Override
    public void close() throws Exception {
        saveScrims();
        saveTeams();
    }
}
