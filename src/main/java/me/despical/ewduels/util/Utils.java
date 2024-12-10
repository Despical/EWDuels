package me.despical.ewduels.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class Utils {

    public static String listToString(List<String> list) {
        return String.join("\n", list);
    }

    public static boolean isArmor(Material material) {
        return material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE ||
            material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS ||
            material == Material.CHAINMAIL_HELMET || material == Material.CHAINMAIL_CHESTPLATE ||
            material == Material.CHAINMAIL_LEGGINGS || material == Material.CHAINMAIL_BOOTS ||
            material == Material.IRON_HELMET || material == Material.IRON_CHESTPLATE ||
            material == Material.IRON_LEGGINGS || material == Material.IRON_BOOTS ||
            material == Material.GOLDEN_HELMET || material == Material.GOLDEN_CHESTPLATE ||
            material == Material.GOLDEN_LEGGINGS || material == Material.GOLDEN_BOOTS ||
            material == Material.DIAMOND_HELMET || material == Material.DIAMOND_CHESTPLATE ||
            material == Material.DIAMOND_LEGGINGS || material == Material.DIAMOND_BOOTS ||
            material == Material.NETHERITE_HELMET || material == Material.NETHERITE_CHESTPLATE ||
            material == Material.NETHERITE_LEGGINGS || material == Material.NETHERITE_BOOTS;
    }

    public static void equipArmorToCorrectSlot(Player player, ItemStack armorItem) {
        Material material = armorItem.getType();
        if (isHelmet(material)) {
            player.getInventory().setHelmet(armorItem);
        } else if (isChestplate(material)) {
            player.getInventory().setChestplate(armorItem);
        } else if (isLeggings(material)) {
            player.getInventory().setLeggings(armorItem);
        } else if (isBoots(material)) {
            player.getInventory().setBoots(armorItem);
        }
    }

    public static boolean isHelmet(Material material) { //TODO: FIX 1.8 GOLDEN HELMET
        return material == Material.LEATHER_HELMET || material == Material.CHAINMAIL_HELMET ||
            material == Material.IRON_HELMET || material == Material.GOLDEN_HELMET ||
            material == Material.DIAMOND_HELMET || material == Material.NETHERITE_HELMET;
    }

    public static boolean isChestplate(Material material) {
        return material == Material.LEATHER_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE ||
            material == Material.IRON_CHESTPLATE || material == Material.GOLDEN_CHESTPLATE ||
            material == Material.DIAMOND_CHESTPLATE || material == Material.NETHERITE_CHESTPLATE;
    }

    public static boolean isLeggings(Material material) {
        return material == Material.LEATHER_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS ||
            material == Material.IRON_LEGGINGS || material == Material.GOLDEN_LEGGINGS ||
            material == Material.DIAMOND_LEGGINGS || material == Material.NETHERITE_LEGGINGS;
    }

    public static boolean isBoots(Material material) {
        return material == Material.LEATHER_BOOTS || material == Material.CHAINMAIL_BOOTS ||
            material == Material.IRON_BOOTS || material == Material.GOLDEN_BOOTS ||
            material == Material.DIAMOND_BOOTS || material == Material.NETHERITE_BOOTS;
    }

    public static boolean isLeather(Material material) {
        return material == Material.LEATHER_HELMET ||
            material == Material.LEATHER_CHESTPLATE ||
            material == Material.LEATHER_LEGGINGS ||
            material == Material.LEATHER_BOOTS;
    }

    public static ItemStack dyeLeatherArmor(ItemStack item, Color color) {
        ItemStack cloned = item.clone();

        if (isLeather(item.getType())) {
            LeatherArmorMeta meta = (LeatherArmorMeta) cloned.getItemMeta();
            if (meta != null) {
                meta.setColor(color);
                cloned.setItemMeta(meta);
            }
        }

        return cloned;
    }

}
