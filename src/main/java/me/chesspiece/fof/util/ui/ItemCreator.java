package me.chesspiece.fof.util.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemCreator {

    private final ItemStack itemStack;
    protected final Map<String, String> nbtData = new HashMap<>();

    public ItemCreator(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemCreator(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    /**
     * Set the name of this item to the given name.
     * Deprecated, use ItemCreator#name(Component)
     *
     * @param name The new name of this item.
     * @return This class instance.
     */
    @Deprecated
    public ItemCreator name(String name) {
        assert name != null;

        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the name of this item to the given name.
     *
     * @param name The new name of this item.
     * @return This class instance.
     */
    public ItemCreator name(Component name) {
        assert name != null;

        ItemMeta meta = this.itemStack.getItemMeta();
        meta.displayName(name);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the lore of this item to the given lore.
     * Deprecated, use ItemCreator#lore w/ Component List
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    @Deprecated
    public ItemCreator lore(List<String> lore) {
        assert lore != null;

        List<String> list = new ArrayList<>();
        lore.forEach(line -> list.add(ChatColor.translateAlternateColorCodes('&', line)));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(list);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the lore of this item to the given lore.
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    public ItemCreator loreComponents(List<Component> lore) {
        assert lore != null;

        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the lore of this item to the given lore.
     * Deprecated, use ItemCreator#lore(Component)
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    @Deprecated
    public ItemCreator lore(String lore) {
        assert lore != null;

        List<String> list = new ArrayList<>();
        Arrays.asList(lore.split("\n")).forEach(line -> list.add(ChatColor.translateAlternateColorCodes('&', line)));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(list);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the lore of this item to the given lore.
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    public ItemCreator lore(Component lore) {
        assert lore != null;

        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(Collections.singletonList(lore));
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the lore of this item to the given lore.
     * Deprecated, use ItemCreator#lore with Component array
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    @Deprecated
    public ItemCreator lore(String... lore) {
        assert lore != null;

        return lore(Arrays.asList(lore));
    }

    /**
     * Set the lore of this item to the given lore.
     *
     * @param lore The new lore of this item.
     * @return This class instance.
     */
    public ItemCreator lore(Component... lore) {
        assert lore != null;

        return loreComponents(Arrays.asList(lore));
    }

    /**
     * Add the given enchantment to this item.
     *
     * @param enchant The enchantment to add.
     * @param level   The enchantment level of the enchantment.
     * @return This class instance.
     */
    public ItemCreator enchant(Enchantment enchant, int level) {
        assert enchant != null;

        ItemMeta meta = itemStack.getItemMeta();

        if (meta instanceof EnchantmentStorageMeta enchMeta) {
            enchMeta.addStoredEnchant(enchant, level, true);
        } else {
            meta.addEnchant(enchant, level, true);
        }

        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Set the damage of this item to the given damage.
     *
     * @param durability The new damage of this item.
     * @return This class instance.
     */
    public ItemCreator durability(int durability) {
        this.itemStack.setDurability((short) durability);
        return this;
    }

    /**
     * Add all the given item flags to this item.
     *
     * @param flags The flags to add.
     * @return This class instance.
     */
    public ItemCreator setItemFlags(ItemFlag[] flags) {
        assert flags != null;

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(flags);

        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the item to glow
     *
     * @param glow whether the item is to glow
     * @return This class instance.
     */
    public ItemCreator glow(boolean glow) {
        if (!glow) return this;
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Set item to be unbreakable
     *
     * @return This class instance.
     */
    public ItemCreator unbreakable() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Get a skull of a player.
     *
     * @param wantedName The (possibility) colored name as display name of the skull item.
     * @param playerName The player to get the skull from.
     * @return The skull item.
     */
    public static ItemStack getPlayerSkull(String wantedName, String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        if (!Objects.isNull(wantedName) && !wantedName.isEmpty()) {
            headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', wantedName));
        }

        if (!Objects.isNull(playerName) && !playerName.isEmpty()) {
            headMeta.setOwner(playerName);
        }

        head.setItemMeta(headMeta);
        return head;
    }

    /**
     * Sets the item nbt data
     *
     * @param key the key
     * @param data the value
     * @return This class instance.
     */
    public ItemCreator nbtData(String key, String data) {
        nbtData.put(key, data);
        return this;
    }

    /**
     * Adds all item flag values to the item
     *
     * @return This class instance.
     */
    public ItemCreator flags() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.values());

        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Modifies the model data for custom items (important)
     *
     * @param modelData the model data number
     * @return This class instance.
     */
    public ItemCreator modelData(int modelData) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(modelData);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Get the ItemStack of this creator.
     *
     * @return The item.
     */
    public ItemStack get() {
        return itemStack;
    }

}
