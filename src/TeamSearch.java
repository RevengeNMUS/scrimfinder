/**
 * TEAM FINDERRRRR
 * Allows easy team finding based on a certain criteria :0
 * isFound allows for a boolean to be passed, where only if IT is true
 * a certain team is returned when using {@link {Main::findTeams}} :0
 *
 * Finder method enables more custom throws from said method above :>
 *
 * Look at findTeams to learn more (not a pyramid scheme)
 */
public interface TeamSearch {
    public boolean isFound(Team team);

    public String finderMethod();
}
