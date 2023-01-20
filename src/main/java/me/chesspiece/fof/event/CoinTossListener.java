package me.chesspiece.fof.event;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.ConfigManager;
import me.chesspiece.fof.cooldown.CooldownManager;
import me.chesspiece.fof.fountain.FountainManager;
import me.chesspiece.fof.task.MonitorItemTask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CoinTossListener implements Listener {

    private final FountainOfFortune plugin;
    private final FountainManager fountainManager;
    private final ConfigManager configManager;
    private final CooldownManager cooldownManager;

    public CoinTossListener(FountainOfFortune plugin) {
        this.plugin = plugin;
        this.fountainManager = plugin.getFountainManager();
        this.configManager = plugin.getConfigManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();

        ItemStack stack = item.getItemStack();
        if (this.fountainManager.isNotKnuckle(stack)) return;

        int cooldown = this.cooldownManager.isOnCooldown(event.getPlayer().getUniqueId());
        if (cooldown != -1) {
            player.sendMessage(ChatColor.RED + "You are on cooldown for " + cooldown + " more seconds.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            event.setCancelled(true);
            return;
        }

        //Monitor item if it is a knuckle when dropped.
        MonitorItemTask monitorItemTask = new MonitorItemTask(plugin, item);
        monitorItemTask.runTaskTimer(plugin, 0L, this.configManager.getCheckEveryTicks());

        this.fountainManager.getMonitorItemTasks().add(monitorItemTask);
    }

    @EventHandler
    public void on(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (item.getThrower() == null) return;

        ItemStack stack = item.getItemStack();
        if (this.fountainManager.isNotKnuckle(stack)) return; //if coin is not knuckle

        if (this.fountainManager.getMonitorItemTasks()
                .stream()
                .map(MonitorItemTask::getItem)
                .noneMatch(entity -> entity.equals(item))) return; //If coin is no longer being checked for

        UUID thrower = item.getThrower();
        //Allow thrower to pick their own coin back up
        if (player.getUniqueId().equals(thrower)) return;

        event.setCancelled(true);
    }
}
