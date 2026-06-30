import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Basically, a Scrimmage!<p>
 * Has a<br>
 * list of teams<br>
 * Date<br>
 * Location<br>
 * Region<br>
 * Application Status (open, closed)<br>
 * Organizer<br>
 * A Size Limit<br>
 * A Start/End Time<br>
 */
public interface Scrimmage {
    /**
     * Tries to add a team to this scrimmage.
     * Might fail if the scrimmage is full, or applications are closed
     * @return whether the attempt was successful
     */
    public boolean addTeam(Team team);

    /**
     * Tries to remove a team from this scrimmage.
     * @return whether the team was removed (whether they existed)
     */
    public boolean removeTeam(Team team);

    /**
     * Sets the start time for the event
     */
    public void setStartTime(LocalDateTime st);

    /**
     * Sets the end time for the event
     */
    public void setEndTime(LocalDateTime et);

    /**
     * @return the start time for the event
     */
    public LocalDateTime startTime();

    /**
     * @return the end time for the event
     */
    public LocalDateTime endTime();

    /**
     * Sets the size limit for the event
     */
    public void setSizeLimit(int sl);

    /**
     * Sets the Location for the event
     */
    public void setLocation(Location location);

    /**
     * Sets the Region for the event
     */
    public void setRegion(Region region);

    /**
     * Sets the ApplicationStatus for the event
     */
    public void setApplicationStatus(ApplicationStatus appStatus);

    /**
     * @return an ArrayList of teams signed up/participating in this scrimmage
     */
    public ArrayList<Team> teamsInScrim();

    /**
     * @return the {@link Location} of the scrimmage
     */
    public Location locationOfScrim();

    /**
     * @return the region that the Scrimmage will happen in
     * Used for filtering results?
     */
    public Region regionOfScrim();

    /**
     * @return this scrimmage's {@link ApplicationStatus} (closed, open, etc)
     */
    public ApplicationStatus appStatus();

    /**
     * @return the {@link Team} organizing this scrimmage
     */
    public Team scrimOrganizer();

    /**
     * checks with the size limit of the scrimmage
     * and makes sure that it has not been reached
     * (if size limit is -1, unlimited)
     * @return if the scrimmage is full
     */
    public boolean isFull();

    /**
     * checks if this scrimmage already has team t signed up
     */
    public boolean hasTeam(Team t);

    /**
     * saves the scrimmages data into a file wawwww
     * when implementation is made, MAKE SURE CONSTRUCTOR CAN TAKE A FILE AS PARAM
     * you RAT
     */
    public File saveToFile() throws IOException;

    /**
     * Returns this scrims (hopefully :>) unique identifier<br>
     * if it isnt unique<br>
     * we are screwed
     */
    public String getIdentifier();
}
