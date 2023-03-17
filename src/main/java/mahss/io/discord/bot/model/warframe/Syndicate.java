package mahss.io.discord.bot.model.warframe;

public enum Syndicate {
    ARBITERS_OF_HEXIS("Arbiters of Hexis", false),
    CEPHALON_SUDA("Cephalon Suda", false),
    STEEL_MERIDIAN("Steel Meridian", false),
    NEW_LOKA("New Loka", false),
    RED_VEIL("Red Veil", false),
    PERRIN_SEQUENCE("Perrin Sequence", false),
    ENTRATI("Entrati", true),
    VENT_KIDS("Vent Kids", false),
    SOLARIS_UNITED("Solaris United", true),
    VOX_SOLARIS("Vox Solaris", false),
    NECRALOID("Necraloid", false),
    OSTRON("Ostrons", true),
    OPERATIONS_SYNDICATE("Operations Syndicate", false),
    KAHLS_GARRISON("Kahl's Garrison", false),
    QUILLS("Quills", false),
    THE_HOLDFASTS("The Holdfasts", false),
    UNKNOWN("Unknown", false);

    private String name;
    private boolean hasJobs;

    Syndicate(String name, boolean hasJobs) {
        this.name = name;
        this.hasJobs = hasJobs;
    }

    public String getName() {
        return name;
    }

    public boolean isHasJobs() {
        return hasJobs;
    }

    public static Syndicate fromText(String name) {
        for (Syndicate e : Syndicate.values()) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
