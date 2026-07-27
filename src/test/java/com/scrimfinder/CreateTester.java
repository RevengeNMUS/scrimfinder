package com.scrimfinder;

import com.scrimfinder.EDC.ApplicationStatus;
import com.scrimfinder.EDC.Location;
import com.scrimfinder.EDC.Region;
import com.scrimfinder.scrimfinder.Main;
import com.scrimfinder.scrimfinder.ScrimmageImpl;
import com.scrimfinder.scrimfinder.Team;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class CreateTester {
    public static void main(String[] args) throws Exception {
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
        mane.saveScrims();
        mane.close();
    }
}
