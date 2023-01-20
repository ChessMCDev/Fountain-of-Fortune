package me.chesspiece.fof.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.chesspiece.fof.FountainOfFortune;
import me.chesspiece.fof.configuration.ConfigManager;
import me.chesspiece.fof.fountain.FountainArea;
import me.chesspiece.fof.fountain.FountainAreaManager;
import me.chesspiece.fof.fountain.FountainManager;
import me.chesspiece.fof.util.location.CustomLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FountainCommand implements CommandExecutor {

    private final FountainOfFortune plugin;
    private final ConfigManager configManager;
    private final FountainManager fountainManager;
    private final FountainAreaManager areaManager;

    public FountainCommand(FountainOfFortune plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.fountainManager = plugin.getFountainManager();
        this.areaManager = plugin.getFountainAreaManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to run this command.");
            return false;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.GRAY + """
                    Wrong Argument!
                    /fountain create <id> - Creates a fountain with a name
                    /fountain delete <id> - Deletes a pre-existing fountain by it's name
                    /fountain reset - Resets your active fountain builder
                    /fountain set-region - Uses the WorldEdit selection for the area
                    /fountain set-a - Manually sets Corner A of the Region
                    /fountain set-b - Manually sets Corner B of the Region
                    /fountain finish - Finishes and saves the fountain to the config
                    /fountain giveknuckle [optional: player]
                    /fountain setknuckle - Sets the knuckle item to the item in your main hand
                    """);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Wrong Argument! /fountain create <id>");
                    return false;
                }

                FountainAreaManager.Builder builder = new FountainAreaManager.Builder(args[1]);
                this.areaManager.addBuilder(player.getUniqueId(), builder);
                player.sendMessage(ChatColor.GREEN + "Fountain Created! Use /fountain <set-a/set-b/set-region> to set the limits of your fountain.");
                return true;
            }
            case "set-a" -> {
                FountainAreaManager.Builder builder = this.areaManager.getBuilder(player.getUniqueId());
                if (builder == null) {
                    player.sendMessage(ChatColor.RED + "You do not have an active fountain builder.");
                    return false;
                }

                Location location = player.getLocation();
                builder.setA(new CustomLocation(location.getX(), location.getY(), location.getZ()));
                player.sendMessage(ChatColor.GREEN + "Set area A on your fountain.");
                return true;
            }
            case "set-b" -> {
                FountainAreaManager.Builder builder = this.areaManager.getBuilder(player.getUniqueId());
                if (builder == null) {
                    player.sendMessage(ChatColor.RED + "You do not have an active fountain builder.");
                    return false;
                }

                Location location = player.getLocation();
                builder.setB(new CustomLocation(location.getX(), location.getY(), location.getZ()));
                player.sendMessage(ChatColor.GREEN + "Set area B on your fountain.");
                return true;
            }
            case "set-region" -> { //WorldEdit alternative to set-a / set-b
                FountainAreaManager.Builder builder = this.areaManager.getBuilder(player.getUniqueId());
                if (builder == null) {
                    player.sendMessage(ChatColor.RED + "You do not have an active fountain builder.");
                    return false;
                }

                WorldEdit worldEdit = WorldEdit.getInstance();
                LocalSession session = worldEdit.getSessionManager().get(BukkitAdapter.adapt(player));

                try {
                    Region region = session.getSelection();
                    if (!(region instanceof CuboidRegion cuboidRegion)) {
                        player.sendMessage(ChatColor.RED + "You have not selected a region with world edit. (No CuboidRegion)");
                        return false;
                    }

                    if (cuboidRegion.getPos1() == null || cuboidRegion.getPos2() == null) {
                        player.sendMessage(ChatColor.RED + "You have not selected a region with world edit.");
                        return false;
                    }

                    BlockVector3 min = cuboidRegion.getPos1(), max = cuboidRegion.getPos2();
                    CustomLocation locationA = new CustomLocation(min.getX(), min.getY(), min.getZ());
                    CustomLocation locationB = new CustomLocation(max.getX(), max.getY(), max.getZ());

                    builder.setA(locationA);
                    builder.setB(locationB);
                    player.sendMessage(ChatColor.GREEN + "Set area for the fountain.");
                } catch (IncompleteRegionException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            case "delete" -> {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Wrong Argument! /fountain delete <id>");
                    return false;
                }

                FountainArea area = this.areaManager.getAreas().get(args[1]);
                if (area == null) {
                    player.sendMessage(ChatColor.RED + "This fountain does not exist.");
                    return false;
                }

                this.areaManager.getAreas().remove(area.getId());
                player.sendMessage("Deleted arena " + area.getId() + ".");
            }
            case "reset" -> {
                FountainAreaManager.Builder builder = this.areaManager.getBuilder(player.getUniqueId());
                if (builder == null) {
                    player.sendMessage(ChatColor.RED + "You do not have an active fountain builder.");
                    return false;
                }

                this.areaManager.removeBuilder(player.getUniqueId());
                player.sendMessage("Reset your fountain builder.");
                return true;
            }
            case "finish" -> {
                FountainAreaManager.Builder builder = this.areaManager.getBuilder(player.getUniqueId());
                if (builder == null) {
                    player.sendMessage(ChatColor.RED + "You do not have an active fountain builder.");
                    return false;
                }

                if (builder.getA() == null) {
                    player.sendMessage(ChatColor.RED + "Your fountain does not have section A. (fountain set-a)");
                    return false;
                }

                if (builder.getB() == null) {
                    player.sendMessage(ChatColor.RED + "Your fountain does not have section B. (/fountain set-b)");
                    return false;
                }

                this.areaManager.addFountain(new FountainArea(builder.getId(), builder.getA(), builder.getB()));
                this.areaManager.removeBuilder(player.getUniqueId());
                this.areaManager.saveFountains();
                player.sendMessage(ChatColor.GREEN + "Fountain created successfully! Use /fountain delete <id> to remove it.");
                return true;
            }
            case "giveknuckle" -> {
                if (args.length != 2) {
                    fountainManager.giveKnuckle(player);
                    player.sendMessage(ChatColor.GREEN + "Successfully gave yourself a knuckle.");
                    return false;
                }

                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + args[1] + " is not online.");
                    return false;
                }

                fountainManager.giveKnuckle(target);
                player.sendMessage(ChatColor.GREEN + "Successfully gave " + target.getName() + " a knuckle.");
                return true;
            }
            case "reload" -> {
                this.configManager.loadValues();
                this.fountainManager.loadKnuckle();
                player.sendMessage(ChatColor.GREEN + "Reloaded the config!");
            }
            case "set-knuckle" -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "You do not have a valid item in your hand.");
                    return false;
                }

                this.fountainManager.setKnuckle(item);
                this.configManager.saveItem();
                player.sendMessage(ChatColor.GREEN + "Successfully updated knuckle item.");
            }
        }

        return true;
    }
}
