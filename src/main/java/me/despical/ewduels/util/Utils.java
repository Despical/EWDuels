package me.despical.ewduels.util;

import me.despical.commons.compat.XMaterial;
import me.despical.commons.reflection.XReflection;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class Utils {

    private static Class<?> NBT_TAG_COMPOUND_CLASS;

    static {
        try {
            NBT_TAG_COMPOUND_CLASS = XReflection.getNMSClass("NBTTagCompound");
        } catch (Exception ignored) {
        }
    }

    public static void disableEntityAI(LivingEntity entity) {
        try {
            Object nmsEntity = entity.getClass().getMethod("getHandle").invoke(entity);
            Object tag = nmsEntity.getClass().getMethod("getNBTTag").invoke(nmsEntity);

            if (tag == null) {
                tag = NBT_TAG_COMPOUND_CLASS.newInstance();
            }

            nmsEntity.getClass().getMethod("c", NBT_TAG_COMPOUND_CLASS).invoke(nmsEntity, tag);
            NBT_TAG_COMPOUND_CLASS.getMethod("setInt", String.class, int.class).invoke(tag, "NoAI", 1);
            NBT_TAG_COMPOUND_CLASS.getMethod("setBoolean", String.class, boolean.class).invoke(tag, "Silent", true);
            nmsEntity.getClass().getMethod("f", NBT_TAG_COMPOUND_CLASS).invoke(nmsEntity, tag);

            entity.setMaxHealth(1);
        } catch (Throwable throwable) {
            entity.setSilent(true);
            entity.setAI(false);
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1D);
        }

        Utils.trySilently(() -> entity.setInvulnerable(true));
        Utils.trySilently(() -> entity.setPersistent(false));
    }

    public static void trySilently(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }

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
