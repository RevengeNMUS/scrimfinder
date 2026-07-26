package com.scrimfinder.SearchMethods;

import com.scrimfinder.scrimfinder.Scrimmage;

/**
 * TEAM FINDERRRRR
 * Allows easy team finding based on a certain criteria :0
 * isFound allows for a boolean to be passed, where only if IT is true
 * a certain team is returned when using {@link {main.java.scrimfinder.Main::findTeams}} :0
 *
 * Finder method enables more custom throws from said method above :>
 *
 * Look at findTeams to learn more (not a pyramid scheme)
 */
public interface ScrimSearch {
    public static ScrimSearch ALWAYS_FOUND = new ScrimSearch () {
        @Override
        public boolean isFound(Scrimmage scrimmage) {
            return true;
        }

        @Override
        public String finderMethod() {
            return "always found";
        }
    };

    public boolean isFound(Scrimmage team);

    public String finderMethod();
}
