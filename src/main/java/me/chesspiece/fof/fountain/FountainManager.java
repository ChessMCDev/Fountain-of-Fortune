package me.chesspiece.fof.fountain;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.ConfigManager;
import me.chesspiece.fof.cooldown.CooldownManager;
import me.chesspiece.fof.task.ItemFortuneTask;
import me.chesspiece.fof.task.MonitorItemTask;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FountainManager {

    private static final String ID_KEY = "fountain_item";

    private final FountainOfFortune plugin;
    private final ConfigManager configManager;
    private final CooldownManager cooldownManager;

    private final List<MonitorItemTask> monitorItemTasks;
    private final List<ItemFortuneTask> itemTasks;

    private ItemStack knuckle;

    public FountainManager(FountainOfFortune plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.cooldownManager = plugin.getCooldownManager();

        this.monitorItemTasks = new ArrayList<>();
        this.itemTasks = new ArrayList<>();

        this.loadKnuckle();
    }

    /**
     * Loads the knuckle from the config and adds the relevant data to it
     */
    public void loadKnuckle() {
        this.knuckle = this.configManager.getItem();

        ItemMeta meta = this.knuckle.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING, ID_KEY);
        this.knuckle.setItemMeta(meta);
    }

    /**
     * Checks if an item is a knuckle against the loaded item
     *
     * @param itemStack the item stack
     * @return true if it's a knuckle
     */
    public boolean isNotKnuckle(ItemStack itemStack) {
        String id = this.getIdByItemStack(itemStack);
        if (id == null) return true;

        return !id.equalsIgnoreCase(ID_KEY);
    }

    /**
     * Gives a knuckle to a player
     *
     * @param player the player
     */
    public void giveKnuckle(Player player) {
        player.getInventory().addItem(this.knuckle);
    }

    /**
     * When a player gets lucky, this event is called
     *
     * @param uuid the UUID of the player that won
     */
    public void fountainEvent(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null) return;

        String randomCommand = this.configManager.getCommands().get(ThreadLocalRandom.current().nextInt(this.configManager.getCommands().size()));
        player.performCommand(randomCommand.replace("<player>", player.getName()));
    }

    /**
     * Sets the task in play to animate the item when rewards are due
     *
     * @param uuid the uuid of the player
     * @param item the item being animated
     * @param startLocation the start location for the animation to take place
     */
    public void itemRising(UUID uuid, Item item, Location startLocation) {
        Validate.notNull(uuid, "UUID cannot be null.");
        Validate.notNull(item, "Item cannot be null.");
        Validate.notNull(startLocation, "Start Location cannot be null.");

        this.cooldownManager.addCooldown(uuid);

        startLocation.add(0, -1.1, 0);

        ItemFortuneTask itemFortuneTask = new ItemFortuneTask(plugin, uuid, startLocation, startLocation.getY() + 2);
        itemFortuneTask.runTaskTimer(plugin, 0L, 1L);

        this.itemTasks.add(itemFortuneTask);
    }

    /**
     * Checks if the event should happen
     *
     * @return true if they win the chance
     */
    public boolean doesEventHappen() {
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) <= this.configManager.getChance();
    }

    /**
     * Checks if the item is a knuckle beyond the item stack prima facie
     *
     * @param item the item to check against
     * @return the ID if it contains an Item ID, null if it does not
     */
    @Nullable
    private String getIdByItemStack(ItemStack item) {
        if (isEmpty(item) || !hasData(item)) {
            return null;
        }

        return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING);
    }

    /**
     * Checks if an item is null or air
     *
     * @param item the item to check against
     * @return true if it's empty
     */
    private boolean isEmpty(ItemStack item) {
        return (item == null || item.getType().isAir());
    }

    /**
     * Checks if the item has data to check if it's a knuckle
     *
     * @param item the item to check against
     * @return true if it contains data
     */
    private boolean hasData(ItemStack item) {
        return (item.hasItemMeta() && !item.getItemMeta().getPersistentDataContainer().isEmpty());
    }

    /**
     * Gets the actual knuckle item with all relevant methods applied to identify the item
     *
     * @return the knuckle
     */
    public ItemStack getKnuckle() {
        return knuckle;
    }

    public void setKnuckle(ItemStack knuckle) {
        this.knuckle = knuckle;
    }

    public List<ItemFortuneTask> getItemTasks() {
        return itemTasks;
    }

    public List<MonitorItemTask> getMonitorItemTasks() {
        return monitorItemTasks;
    }
}
