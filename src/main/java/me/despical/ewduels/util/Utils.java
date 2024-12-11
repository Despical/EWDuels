package me.despical.ewduels.util;

import me.despical.commons.compat.XMaterial;
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
        return isHelmet(material) || isChestplate(material) || isLeggings(material) || isBoots(material);
    }

    public static void equipArmorToCorrectSlot(Player player, ItemStack item, Color color) {
        item = dyeLeatherArmor(item, color);

        Material material = item.getType();

        if (isHelmet(material)) {
            player.getInventory().setHelmet(item);
        } else if (isChestplate(material)) {
            player.getInventory().setChestplate(item);
        } else if (isLeggings(material)) {
            player.getInventory().setLeggings(item);
        } else if (isBoots(material)) {
            player.getInventory().setBoots(item);
        }
    }

    public static boolean isHelmet(Material material) {
        return XMaterial.matchXMaterial(material).name().endsWith("HELMET");
    }

    public static boolean isChestplate(Material material) {
        return XMaterial.matchXMaterial(material).name().endsWith("CHESTPLATE");
    }

    public static boolean isLeggings(Material material) {
        return XMaterial.matchXMaterial(material).name().endsWith("LEGGINGS");
    }

    public static boolean isBoots(Material material) {
        return XMaterial.matchXMaterial(material).name().endsWith("BOOTS");
    }

    public static boolean isLeather(Material material) {
        return XMaterial.matchXMaterial(material).name().startsWith("LEATHER");
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
