package me.despical.ewduels.handler.chat;

import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.util.Strings;
import me.despical.ewduels.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 21.11.2024
 */
public class ChatManager {

    private FileConfiguration config;

    private final Main plugin;
    private final Map<String, String> cachedMessages;

    public ChatManager(Main plugin) {
        this.plugin = plugin;
        this.cachedMessages = new HashMap<>();

        config = ConfigUtils.getConfig(plugin, "messages");
    }

    public String getMessage(String path) {
        String message = cachedMessages.get(path);

        if (message == null) {
            message = config.getString(path);

            cachedMessages.put(path, message);
        }

        return Strings.format(message);
    }

    public String getFormattedMessage(String path, Object... params) {
        String message = cachedMessages.get(path);

        if (message == null) {
            message = config.getString(path);

            cachedMessages.put(path, message);
        }

        message = Strings.format(message);
        return MessageFormat.format(message, params);
    }

    public String getFormattedRawMessage(String message, Object... params) {
        message = Strings.format(message);
        message = MessageFormat.format(message, params);
        return message;
    }
}
