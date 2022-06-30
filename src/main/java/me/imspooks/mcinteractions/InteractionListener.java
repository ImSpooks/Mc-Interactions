package me.imspooks.mcinteractions;

import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

@Data
class InteractionListener implements Listener {

    private final Interactions plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayers().put(player.getUniqueId(), new InteractionPlayer(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getPlayers().remove(event.getPlayer().getUniqueId());
    }

    // ----------------------
    //      Interactions
    // ----------------------

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (this.complete(event.getPlayer(), InteractionType.LEFT_CLICK_BLOCK, event.getClickedBlock())) {
                    event.setCancelled(true);
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (this.complete(event.getPlayer(), InteractionType.RIGHT_CLICK_BLOCK, event.getClickedBlock())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            if (this.complete(event.getPlayer(), InteractionType.LEFT_CLICK_BLOCK, event.getBlock())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (this.complete(event.getPlayer(), InteractionType.CHAT, event.getMessage())) {
            event.setCancelled(true);
        }
    }

    private boolean complete(Player player, InteractionType type, Object object) {
        InteractionPlayer interactionPlayer = this.plugin.getPlayer(player);
        if (interactionPlayer == null) {
            return false;
        }

        boolean found = false;

        interactionPlayer.removeExpired(type);
        for (Interaction<?> completableFuture : new ArrayList<>(interactionPlayer.getInteractions().get(type))) {
            completableFuture.completeUnsafe(object);
            found = true;
        }
        return found;
    }
}
