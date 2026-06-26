public enum ApplicationStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED");

    String statusString;
    private ApplicationStatus(String statusString) {
        this.statusString = statusString;
    }

    /*
    todo think of way to do dis without copy pasta de code :0
     */
    public static ApplicationStatus fromStatusString(String statusString) {
        return switch (statusString) {
            case "OPEN" -> OPEN;
            case "CLOSED" -> CLOSED;
            case null, default -> CLOSED;
        };
    }

    @Override
    public String toString() {
        return statusString;
    }
}
