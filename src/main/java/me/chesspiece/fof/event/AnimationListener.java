package me.chesspiece.fof.event;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.fountain.FountainManager;
import me.chesspiece.fof.task.ItemFortuneTask;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AnimationListener implements Listener {

    private final FountainManager fountainManager;

    public AnimationListener(FountainOfFortune plugin) {
        this.fountainManager = plugin.getFountainManager();
    }

    /**
     * Cancels the event damaging the ArmorStand mid-animation.
     *
     * @param event damaging the armorstand event
     */
    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof ArmorStand armorStand)) return;

        if (this.fountainManager.getItemTasks()
                .stream()
                .map(ItemFortuneTask::getEntity)
                .anyMatch(entity -> entity.equals(armorStand))) {
            event.setCancelled(true);
        }
    }
}
