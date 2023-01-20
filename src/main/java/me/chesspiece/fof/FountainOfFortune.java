package me.chesspiece.fof;

import me.chesspiece.fof.command.FountainCommand;
import me.chesspiece.fof.configuration.ConfigManager;
import me.chesspiece.fof.cooldown.CooldownManager;
import me.chesspiece.fof.event.AnimationListener;
import me.chesspiece.fof.event.CoinTossListener;
import me.chesspiece.fof.fountain.FountainAreaManager;
import me.chesspiece.fof.fountain.FountainManager;
import me.chesspiece.fof.task.ItemFortuneTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FountainOfFortune extends JavaPlugin {

    private ConfigManager configManager;
    private FountainAreaManager fountainAreaManager;
    private FountainManager fountainManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
    }

    private void registerManagers() {
        this.configManager = new ConfigManager(this);
        this.fountainAreaManager = new FountainAreaManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.fountainManager = new FountainManager(this);
    }

    private void registerCommands() {
        this.getCommand("fountain").setExecutor(new FountainCommand(this));
    }

    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new CoinTossListener(this), this);
        pluginManager.registerEvents(new AnimationListener(this), this);
    }

    @Override
    public void onDisable() {
        for (ItemFortuneTask itemTask : this.fountainManager.getItemTasks()) {
            itemTask.getEntity().remove();
        }
    }

    /* Getters */

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FountainAreaManager getFountainAreaManager() {
        return fountainAreaManager;
    }

    public FountainManager getFountainManager() {
        return fountainManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
