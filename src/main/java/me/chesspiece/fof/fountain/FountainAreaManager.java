package me.chesspiece.fof.fountain;

import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.Config;
import me.chesspiece.fof.util.location.CustomLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.UUID;

public class FountainAreaManager {

    private final FountainOfFortune plugin;
    private final Config fountains;
    private final HashMap<String, FountainArea> areas;
    private final HashMap<UUID, Builder> builders;

    public FountainAreaManager(FountainOfFortune plugin) {
        this.plugin = plugin;
        this.fountains = new Config("fountains", plugin);
        this.areas = new HashMap<>();
        this.builders = new HashMap<>();

        //Went overboard with this, could've kept it to one fountain since canonically there's only one fountain like this

        this.loadFountains();
    }

    /**
     * Checks if the event is inside a fountain area
     *
     * @param location the location to check against
     * @return true if it is inside a fountain area, otherwise false
     */
    public boolean isInFountainArea(Location location) {
        return this.areas
                .values()
                .stream()
                .anyMatch(fountainArea -> fountainArea.isInside(location));
    }

    /**
     * Adds a fountain area to the config
     *
     * @param area the fountain area
     */
    public void addFountain(FountainArea area) {
        Validate.notNull(area, "Arena cannot be null.");

        this.areas.put(area.getId(), area);
    }

    /**
     * Deletes an area using its ID
     *
     * @param id the id of the arena
     */
    public void deleteFountain(String id) {
        Validate.notNull(id, "ID cannot be null.");

        this.areas.remove(id);
    }

    public void loadFountains() {
        ConfigurationSection fountainSection = this.fountains.getConfig().getConfigurationSection("fountains");
        if (fountainSection == null) {
            this.plugin.getLogger().info("Fountains cannot be loaded!");
            return;
        }

        fountainSection.getKeys(false).forEach(id -> {
            String cornerA = fountainSection.getString(id + ".cornerA");
            String cornerB = fountainSection.getString(id + ".cornerB");
            if (cornerA == null || cornerB == null) {
                this.plugin.getLogger().info("Fountain " + id + " cannot load.");
                return;
            }

            CustomLocation locA = CustomLocation.stringToLocation(cornerA);
            CustomLocation locB = CustomLocation.stringToLocation(cornerB);

            FountainArea fountainArea = new FountainArea(id, locA, locB);
            this.areas.put(id, fountainArea);
        });

        this.plugin.getLogger().info("Loaded " + this.areas.size() + " fountains.");
    }

    public void saveFountains() {
        FileConfiguration config = this.fountains.getConfig();
        config.set("fountains", null);

        for (FountainArea area : this.areas.values()) {
            String id = "fountains." + area.getId();

            config.set(id + ".cornerA", area.getMin().locationToString());
            config.set(id + ".cornerB", area.getMax().locationToString());
        }

        this.fountains.save();
    }

    public void addBuilder(UUID uuid, Builder builder) {
        this.builders.put(uuid, builder);
    }

    public void removeBuilder(UUID uuid) {
        this.builders.remove(uuid);
    }

    public Builder getBuilder(UUID uuid) {
        return this.builders.get(uuid);
    }

    public static class Builder {

        private final String id;
        private CustomLocation a, b;

        public Builder(String id) {
            this.id = id;
        }

        public void setA(CustomLocation a) {
            this.a = a;
        }

        public void setB(CustomLocation b) {
            this.b = b;
        }

        public CustomLocation getA() {
            return a;
        }

        public CustomLocation getB() {
            return b;
        }

        public String getId() {
            return id;
        }
    }

    /* Getters */

    public HashMap<String, FountainArea> getAreas() {
        return areas;
    }
}
