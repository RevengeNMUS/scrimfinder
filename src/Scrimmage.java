import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Basically, a Scrimmage
 * Has a
 * list of teams
 * Date
 * Location
 * Region
 * Application Status (open, closed)
 * Organizer
 * A Size Limit
 * A Start/End Time
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
    public void setEndTime(LocalDateTime sl);

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
     * Sets the Date for the event
     */
    public void setDate(Date date);

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
     * @return the {@link Date} that the scrimmage will happen on
     */
    public Date dateOfScrim();

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

}
