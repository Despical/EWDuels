package me.despical.ewduels.option;

/**
 * @author Despical
 * <p>
 * Created at 11.12.2024
 */
public enum Option {

    CLEAR_INVENTORY_ON_QUEUE("Clear-Inventory-On-Queue", false),
    LEAVE_QUEUE_ITEM("Leave-Queue-Item", true),
    SEND_TO_END_ON_QUEUE_LEAVE("Teleport-To-End-When-Leaving-Queue", false),
    SEND_TO_THE_START_POS_Y("Teleport-To-Start-If-Less-Than", 0),
    SEND_TO_LOBBY_ON_QUEUE("Teleport-To-Lobby-On-Queue", true),
    START_COUNTDOWN("Time-Settings.Start-Countdown", 5),
    ROUND_STARTING_COUNTDOWN("Time-Settings.Round-Starting-Countdown", 3),
    POINTS_TO_WIN("Points-To-Win", 5),
    FALL_DAMAGE_ENABLED("Fall-Damage-Enabled", false),
    EGG_PROTECTION_RADIUS("Egg-Protection-Radius", 2),
    ENDING_COUNTDOWN("Time-Settings.Ending", 5),
    SCOREBOARD_ENABLED("Scoreboard-Enabled", true);

    final String path;
    final Class<?> type;
    final Object defaultValue;

    Option(String path, Object defaultValue) {
        this.path = path;
        this.type = defaultValue.getClass();
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    <T> T parse(Object value) {
        if (type.isInstance(value)) {
            return (T) value;
        }

        throw new IllegalArgumentException("Invalid value type for " + name() + ": " + value);
    }
}
