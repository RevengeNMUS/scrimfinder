import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
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
        teams.add(team);
        saveTeams();
        return true;
    }

    public boolean addScrim(Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        scrims.add(scrim);
        for (Team team : scrim.teamsInScrim()) {
            teams.add(team);
        }
        saveScrims();
        saveTeams();
        return true;
    }

    public boolean loadScrims() throws IOException, TimeoutException, InterruptedException {
        if (!rwlock.tryLock(10, TimeUnit.SECONDS)) {
            throw new TimeoutException();
        }

        try (var scrimFileStream = Files.list(Path.of(MainConstants.SCRIM_PATH))) {
            var pathsArray = scrimFileStream.toArray();
            var pathsList = List.of(pathsArray);
            scrims.clear();
            pathsList.forEach(path -> {
                scrims.add(ScrimmageImpl.fromFile(new File((String) path)));
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

        try (var teamFileStream = Files.list(Path.of(MainConstants.TEAM_PATH))) {

            var pathsArray = teamFileStream.toArray();
            var pathsList = List.of(pathsArray);
            scrims.clear();
            for (Object path : pathsList) {
                teams.add(new Team(new File((String) path)));
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
                team.saveTeam();
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

    public ArrayList<Team> findTeams(TeamSearch ts) throws ResourceNotFoundException {
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

    public ArrayList<Scrimmage> findScrims(ScrimSearch ss) throws ResourceNotFoundException {
        var returnList = new ArrayList<Scrimmage>();

        for (Scrimmage scrim : scrims) {
            if (ss.isFound(scrim)) {
                returnList.add(scrim);
            }
        }

        if (returnList.isEmpty()) {
            //resort to throwing rotten tomatoes (exception) at user
            throw new ResourceNotFoundException("No resources found for  " + ss.finderMethod());
        }

        return returnList;
    }

    public boolean joinScrim(Team team, Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        team.attendeeFor(scrim);
        scrim.addTeam(team);
        //if smth dies, it dies HERE :000
        saveTeams();
        saveScrims();
        return true;
    }

    public boolean leaveScrim(Team team, Scrimmage scrim) throws IOException, InterruptedException, TimeoutException {
        team.notAttending(scrim);
        scrim.removeTeam(team);
        //if smth dies, it dies HERE :000
        saveTeams();
        saveScrims();
        return true;
    }
}
