package me.chesspiece.fof.task;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.ConfigManager;
import me.chesspiece.fof.fountain.FountainAreaManager;
import me.chesspiece.fof.fountain.FountainManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MonitorItemTask extends BukkitRunnable {

    private final FountainOfFortune plugin;
    private final ConfigManager configManager;
    private final FountainManager fountainManager;
    private final FountainAreaManager fountainAreaManager;
    private final Item item;
    private int tick;

    public MonitorItemTask(FountainOfFortune plugin, Item item) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.fountainManager = plugin.getFountainManager();
        this.fountainAreaManager = plugin.getFountainAreaManager();

        this.item = item;
    }

    @Override
    public void run() {
        this.tick++;

        //Could make that it detects up until the item is on the ground, but want it to be as least intensive as possible
        if (item == null || item.isDead() || tick >= this.configManager.getMaxTicksToCheck()) {
            this.cancel();
            return;
        }

        if (!item.isInWater()) return;

        Location location = item.getLocation();
        if (!this.fountainAreaManager.isInFountainArea(item.getLocation())) return;
        //Item in fountain

        this.cancel();

        UUID thrower = this.item.getThrower();
        if (thrower == null) {
            this.fountainManager.getMonitorItemTasks().remove(this);
            return;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            item.remove();

            if (this.fountainManager.doesEventHappen()) {
                this.fountainManager.itemRising(thrower, item, item.getLocation().clone());
            } else {
                Player player = this.plugin.getServer().getPlayer(thrower);
                if (player == null) return;

                player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.2f, 1.0f);
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 6);
            }
        }, 10L);
    }

    public Item getItem() {
        return item;
    }
}
