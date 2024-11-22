package me.despical.ewduels.util;

import me.despical.commons.string.StringUtils;

import java.util.Locale;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public enum GameLocation {

    LOBBY("lobby"),
    END("end"),
    FIRST_PLAYER("first-player"),
    SECOND_PLAYER("second-player"),
    FIRST_EGG("first-egg"),
    SECOND_EGG("second-egg");

    private final String name;

    GameLocation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return StringUtils.capitalize(name().toLowerCase(Locale.ENGLISH).replace("_", " "), ' ');
    }
}
