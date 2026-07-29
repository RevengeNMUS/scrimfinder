package com.scrimfinder.SearchMethods;

import com.scrimfinder.EDC.ApplicationStatus;
import com.scrimfinder.EDC.Location;
import com.scrimfinder.EDC.Region;
import com.scrimfinder.scrimfinder.Scrimmage;
import com.scrimfinder.scrimfinder.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SearchFactory {
    public static final DateTimeFormatter PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static ScrimSearch SCRIM_DEFAULT = new ScrimSearch () {
        @Override
        public boolean isFound(Scrimmage scrim) {
            return true;
        }

        @Override
        public String finderMethod() {
            return "always found";
        }
    };

    public static TeamSearch TEAM_DEFAULT = new TeamSearch () {
        @Override
        public boolean isFound(Team team) {
            return true;
        }

        @Override
        public String finderMethod() {
            return "always found";
        }
    };

    public static TeamSearch buildTeamSearch() {
        return TEAM_DEFAULT;
    }

    public static TeamSearch buildTeamSearch(Region region) {
        if (region == null)
            return TEAM_DEFAULT;

        return new TeamSearch() {
            @Override
            public boolean isFound(Team team) {return team.getRegion().getRegionCode().equals(region.getRegionCode());}
            @Override
            public String finderMethod() {return "region search";}
        };
    }

    public static TeamSearch buildTeamSearch(String name) {
        if (name == null)
            return TEAM_DEFAULT;

        return new TeamSearch() {
            @Override
            public boolean isFound(Team team) {return team.getTeamName().equals(name);}
            @Override
            public String finderMethod() {return "name search";}
        };
    }

    public static TeamSearch buildTeamSearch(Integer number) {
        if (number == null)
            return TEAM_DEFAULT;

        return new TeamSearch() {
            @Override
            public boolean isFound(Team team) {return team.getTeamNum() == number;}
            @Override
            public String finderMethod() {return "number search";}
        };
    }

    public static TeamSearch buildTeamSearch(String scrimmage, boolean org) {
        if (scrimmage == null)
            return TEAM_DEFAULT;

        return new TeamSearch() {
            @Override
            public boolean isFound(Team team) {
                return org ? team.isOrganizing(scrimmage) : team.isAttending(scrimmage);
            }
            @Override
            public String finderMethod() {return "scrim search";}
        };
    }

    public static ScrimSearch buildScrimSearch() {
        return SCRIM_DEFAULT;
    }

    public static ScrimSearch buildScrimSearch(String identifier) {
        if (identifier == null)
            return SCRIM_DEFAULT;

        return new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage scrim) {return scrim.getIdentifier().equals(identifier);}
            @Override
            public String finderMethod() {return "region search";}
        };
    }

    public static ScrimSearch buildScrimSearch(Region region) {
        if (region == null)
            return SCRIM_DEFAULT;

        return new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage scrim) {return scrim.regionOfScrim().getRegionCode().equals(region.getRegionCode());}
            @Override
            public String finderMethod() {return "region search";}
        };
    }
    public static ScrimSearch buildScrimSearch(Team team) {
        if (team == null)
            return SCRIM_DEFAULT;

        return new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage scrim) {
                return scrim.hasTeam(team);
            }
            @Override
            public String finderMethod() {return "team search";}
        };
    }
    public static ScrimSearch buildScrimSearch(LocalDateTime sTime) {
        if (sTime == null)
            return SCRIM_DEFAULT;

        return new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage scrim) {return scrim.startTime().equals(sTime);}
            @Override
            public String finderMethod() {return "datetime search";}
        };
    }

    public static ScrimSearch buildScrimSearch(LocalDate sDay) {
        if (sDay != null) {
            return new ScrimSearch() {
                @Override
                public boolean isFound(Scrimmage scrim) {
                    return scrim.startTime().toLocalDate().equals(sDay);
                }

                @Override
                public String finderMethod() {
                    return "date search";
                }
            };
        } else {
            return SCRIM_DEFAULT;
        }
    }

    public static ScrimSearch buildScrimSearch(LocalDateTime sTime, LocalDateTime eTime) {
        if (sTime != null && eTime != null) {
            return new ScrimSearch() {
                @Override
                public boolean isFound(Scrimmage scrim) {return scrim.startTime().isBefore(eTime) && scrim.startTime().isAfter(sTime);}
                @Override
                public String finderMethod() {return "range search";}
            };
        } else {
            return SCRIM_DEFAULT;
        }
    }

    public static ScrimSearch buildScrimSearch(ApplicationStatus applicationStatus) {
        if (applicationStatus != null) {
            return new ScrimSearch() {
                @Override
                public boolean isFound(Scrimmage scrim) {
                    return scrim.appStatus() == applicationStatus;
                }

                @Override
                public String finderMethod() {
                    return "appstatus search";
                }
            };
        } else {
            return SearchFactory.SCRIM_DEFAULT;
        }
    }

    public static ScrimSearch buildScrimSearch(Location loc) {
        if (loc == null)
            return SCRIM_DEFAULT;

        return new ScrimSearch() {
            @Override
            public boolean isFound(Scrimmage scrim) {
                return scrim.locationOfScrim().toString().equals(loc.toString()); //i forglorb equals emthod
            }

            @Override
            public String finderMethod() {
                return "loc search";
            }
        };
    }
}
