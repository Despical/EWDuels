package me.despical.ewduels.arena;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public enum ArenaState {

    WAITING("waiting"),
    STARTING("starting"),
    IN_GAME("in-game"),
    ENDING("ending"),
    RESTARTING("restarting"),
    INACTIVE("inactive");

    private String formattedName;
    private final String defaultName;

    ArenaState(String path) {
        this.defaultName = path;
    }

    public String getPath() {
        return "arena-states." + defaultName;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }
}
