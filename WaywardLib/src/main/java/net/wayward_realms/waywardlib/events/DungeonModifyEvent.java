package net.wayward_realms.waywardlib.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a dungeon is modified
 * @author Lucariatias
 *
 */
public class DungeonModifyEvent extends DungeonEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private boolean cancel;

    public DungeonModifyEvent(final Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the dungeon being modified
     *
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
