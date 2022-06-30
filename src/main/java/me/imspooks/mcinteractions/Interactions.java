package me.imspooks.mcinteractions;

import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Interactions implements Listener {

    @Getter private static Interactions instance;

    public static void initialize(JavaPlugin plugin) {
        if (instance != null) {
            return;
        }

        instance = new Interactions(plugin);
    }

    private final JavaPlugin plugin;

    Interactions(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new InteractionListener(this), plugin);
    }

    private final Map<UUID, InteractionPlayer> players = new HashMap<>();

    public InteractionPlayer getPlayer(UUID player) {
        return this.players.get(player);
    }

    public InteractionPlayer getPlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }
}
