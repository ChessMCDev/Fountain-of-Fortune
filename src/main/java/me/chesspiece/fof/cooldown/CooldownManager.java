package me.chesspiece.fof.cooldown;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.ConfigManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final FountainOfFortune plugin;
    private final ConfigManager configManager;
    private final HashMap<UUID, Instant> cooldown;

    public CooldownManager(FountainOfFortune plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();

        this.cooldown = new HashMap<>();
    }

    /**
     * Adds a cooldown for getting fortunate in the fortune
     *
     * @param uuid the UUID of the player
     */
    public void addCooldown(UUID uuid) {
        this.cooldown.put(uuid, Instant.now());

        //Would rather have a task to remove cooldown than removing it on the next check
        //So data will not be stored unnecessarily
        this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> this.cooldown.remove(uuid), this.configManager.getCooldown() * 20L);
    }

    /**
     * Checks if the player is on a cooldown
     *
     * @param uuid the UUID of the player
     * @return -1 if they are not on cooldown, and the time left in seconds if they are.
     */
    public int isOnCooldown(UUID uuid) {
        boolean onCooldown = this.cooldown.containsKey(uuid);

        if (onCooldown) return (int) (plugin.getConfigManager().getCooldown() - ChronoUnit.SECONDS.between(this.cooldown.get(uuid), Instant.now()));
        else return -1;
    }

}
