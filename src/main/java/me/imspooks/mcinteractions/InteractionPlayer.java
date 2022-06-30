package me.imspooks.mcinteractions;

import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class InteractionPlayer {

    private final Player player;
    private final Map<InteractionType, List<Interaction<?>>> interactions = new ConcurrentHashMap<>();

    public InteractionPlayer(Player player) {
        this.player = player;

        for (InteractionType value : InteractionType.CACHE) {
            this.interactions.put(value, Collections.synchronizedList(new ArrayList<>()));
        }
    }

    public CompletableFuture<String> interactionChat() {
        return this.createInteraction(InteractionType.CHAT);
    }

    public CompletableFuture<Block> interactionLeftClickBlock() {
        return this.createInteraction(InteractionType.LEFT_CLICK_BLOCK);
    }

    public CompletableFuture<Block> interactionRightClickBlock() {
        return this.createInteraction(InteractionType.RIGHT_CLICK_BLOCK);
    }

    private <T> CompletableFuture<T> createInteraction(InteractionType type) {
        Interaction<T> future = new Interaction<>();
        this.interactions.get(type).add(future);
        return future;
    }

    public void removeExpired(InteractionType type) {
        this.interactions.get(type).removeIf(CompletableFuture::isDone);
    }

    public void removeExpired() {
        for (InteractionType type : this.interactions.keySet()) {
            this.removeExpired(type);
        }
    }
}
