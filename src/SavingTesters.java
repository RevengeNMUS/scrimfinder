import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SavingTesters {
    public static void nonMainSavingTester() throws IOException {
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

        Scrimmage savedScrim = ScrimmageImpl.fromFile(new File(MainConstants.SCRIM_PATH + "12635-San Jose-25_06_2026.txt"));
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

    public static void viaMainSavingTester() throws IOException, InterruptedException, TimeoutException {
        Main mane = new Main();
        Team organizer = new Team(12635, "Kuriosity Robotics", Region.NORCAL);
        mane.addTeam(organizer);
        Location loc = new Location(-121.8863, 37.3382, "123 Main St", "San Jose", "CA", "USA");
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(organizer);
        mane.addScrim(new ScrimmageImpl(
                teams,
                loc,
                Region.NORCAL,
                ApplicationStatus.OPEN,
                organizer,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(3)
        ));

        for (Scrimmage scrim : Main.scrims) {
            printScrimInfo(scrim);
        }

        System.out.println("___________________________________ \n ___________________________________");

        Scrimmage savedScrim = ScrimmageImpl.fromFile(new File(MainConstants.SCRIM_PATH + "12635-San Jose-25_06_2026.txt"));
        printScrimInfo(savedScrim);

        System.out.println("___________________________________ \n _____ \n ___________________________________");
        printTeamInfo(mane.findTeam(12635));
        System.out.println("___________________________________ \n _____ \n ___________________________________");
        printTeamInfo(mane.findTeam("Kuriosity Robotics"));
        System.out.println("___________________________________ \n _____ \n ___________________________________");

        for (Scrimmage scrim : mane.findScrims(new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage team) {
                return team.regionOfScrim() == Region.NORCAL;
            }

            @Override
            public String finderMethod() {
                return "Region, NORCAL";
            }
        })) {
            printScrimInfo(scrim);
        }

        System.out.println("___________________________________ \n _____ \n ___________________________________");

        for (Scrimmage scrim : mane.findScrims(new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage team) {
                return team.regionOfScrim() == Region.SOCAL;
            }

            @Override
            public String finderMethod() {
                return "Region, SOCAL";
            }
        })) {
            printScrimInfo(scrim);
        }
    }
}
