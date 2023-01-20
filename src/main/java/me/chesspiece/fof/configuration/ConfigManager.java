package me.chesspiece.fof.configuration;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.util.ui.ItemCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConfigManager {

    private final FountainOfFortune plugin;
    private final Config config;

    private int cooldown;
    private int maxTicksToCheck;
    private int checkEveryTicks;
    private double chance;
    private ItemStack item;
    private List<String> commands;

    public ConfigManager(FountainOfFortune plugin) {
        this.plugin = plugin;

        this.config = new Config("config", plugin);
        this.loadValues();
    }

    public void loadValues() {
        this.cooldown = this.config.getConfig().getInt("cooldown", 300);
        this.maxTicksToCheck = this.config.getConfig().getInt("max-ticks-to-check", 3);
        this.checkEveryTicks = this.config.getConfig().getInt("check-every-ticks", 100);
        this.chance  = this.config.getConfig().getDouble("chance", 0.5);

        ItemStack item = new ItemCreator(Material.GOLD_INGOT)
                .name(Component.text("Knuckle", NamedTextColor.GOLD))
                .flags().get();
        if (this.config.getConfig().contains("item")) {
            this.item = this.config.getConfig().getItemStack("item", item);
        } else {
            this.item = item;
            this.config.getConfig().set("item", item);
        }

        //Usually I'd make an API, but I'm not aware of the plugin structure of the server
        this.commands = this.config.getConfig().getStringList("commands");

        this.config.save();
    }

    public void saveItem() {
        ItemStack knuckle = plugin.getFountainManager().getKnuckle();
        if (knuckle == null) return;

        this.config.getConfig().set("item", knuckle);
        this.config.save();
    }

    /* Getters */

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxTicksToCheck() {
        return maxTicksToCheck;
    }

    public int getCheckEveryTicks() {
        return checkEveryTicks;
    }

    public double getChance() {
        return chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public List<String> getCommands() {
        return commands;
    }
}
