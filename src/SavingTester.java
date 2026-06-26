import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SavingTester {
    public static void main(String[] args) throws FileNotFoundException {
        Team organizer = new Team(12635, "Kuriosity Robotics", Region.NORCAL);
        organizer.saveTeam();
        Location loc = new Location(-121.8863, 37.3382, "123 Main St", "San Jose", "CA", "USA");
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(organizer);

        Scrimmage scrim = new ScrimmageImpl(
                teams,
                loc,
                Region.NORCAL,
                ApplicationStatus.OPEN,
                organizer,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(3)
        );
        File savedFile = scrim.saveToFile();
        printScrimInfo(scrim);

        System.out.println("___________________________________ \n ___________________________________");

        Scrimmage savedScrim = ScrimmageImpl.fromFile(new File("scrimmages/12635-San Jose-25_06_2026.txt"));
        printScrimInfo(savedScrim);
    }

    public static void printTeamInfo(Team team) {
        System.out.println(team.getActiveScrimmages());
        System.out.println(team.getOrganizedScrimmages());
        System.out.println(team.getRegion());
        System.out.println(team.getTeamName());
        System.out.println(team.getTeamNum());
    }

    public static void printScrimInfo(Scrimmage scrim) {
        System.out.println(scrim.locationOfScrim());
        System.out.println(scrim.regionOfScrim());
        System.out.println(scrim.getIdentifier());
        System.out.println(scrim.appStatus());
        System.out.println(scrim.startTime());
        System.out.println(scrim.endTime());
        System.out.println(scrim.isFull());
        System.out.println("___________________");
        printTeamInfo(scrim.scrimOrganizer());
        System.out.println("___________________");
        for (Team team : scrim.teamsInScrim()) {
            printTeamInfo(team);
        }
    }
}
