package me.despical.ewduels.arena;

import me.despical.ewduels.util.GameLocation;
import org.bukkit.Color;

/**
 * @author Despical
 * <p>
 * Created at 12.12.2024
 */
public enum Team {

    BLUE(GameLocation.FIRST_PLAYER, GameLocation.FIRST_EGG, Color.BLUE),
    RED(GameLocation.SECOND_PLAYER, GameLocation.SECOND_EGG, Color.RED);

    private final GameLocation startLocation;
    private final GameLocation eggLocation;
    private final Color color;

    Team(GameLocation startLocation, GameLocation eggLocation, Color color) {
        this.startLocation = startLocation;
        this.eggLocation = eggLocation;
        this.color = color;
    }

    public GameLocation getStartLocation() {
        return startLocation;
    }

    public GameLocation getEggLocation() {
        return eggLocation;
    }

    public Color getColor() {
        return color;
    }

    public Team getOpposite() {
        return this == BLUE ? RED : BLUE;
    }
}
