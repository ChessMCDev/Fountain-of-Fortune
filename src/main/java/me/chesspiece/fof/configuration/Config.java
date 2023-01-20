package me.chesspiece.fof.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {

	private final FileConfiguration config;
	private final File configFile;

	public Config(String name, JavaPlugin plugin) {
		this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");

		if (!this.configFile.exists()) {
			this.createFile();
		}

		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	/**
	 * Creates the config file if it does not exist
	 */
	private void createFile() {
		try {
			this.configFile.getParentFile().mkdirs();
			this.configFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the config file
	 */
	public void save() {
		try {
			this.config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Getters */

	public FileConfiguration getConfig() {
		return config;
	}
}