package me.entity303.openshulker.util;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShulkerActions {
    private final NamespacedKey openShulkerKey;
    private OpenShulker openShulker;

    public ShulkerActions(OpenShulker openShulker) {
        this.openShulker = openShulker;
        this.openShulkerKey = new NamespacedKey(this.openShulker, "openshulker");
    }

    public ItemStack searchShulkerBox(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == Material.AIR)
                continue;

            if (!this.isOpenShulker(itemStack))
                continue;

            return itemStack;
        }

        return null;
    }

    public boolean isOpenShulker(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta))
            return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(this.openShulkerKey, PersistentDataType.BYTE))
            return false;

        return true;
    }

    public void saveShulkerBox(ItemStack shulkerBoxStack, Inventory inventory, Player player) {
        BlockStateMeta blockStateMeta = (BlockStateMeta) shulkerBoxStack.getItemMeta();

        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();

        shulkerBox.getInventory().setContents(inventory.getContents());

        blockStateMeta.setBlockState(shulkerBox);

        PersistentDataContainer container = blockStateMeta.getPersistentDataContainer();

        container.remove(this.openShulkerKey);

        shulkerBoxStack.setItemMeta(blockStateMeta);

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, null);

        container = player.getPersistentDataContainer();

        container.remove(this.openShulkerKey);
    }

    public boolean hasOpenShulkerBox(Player player) {
        ItemStack itemStack = this.searchShulkerBox(player);

        PersistentDataContainer container = player.getPersistentDataContainer();

        if (!container.has(this.openShulkerKey)) {
            if (itemStack != null) {
                Bukkit.getLogger().severe("Player " + player.getName() + " (" + player.getUniqueId() + ") may have duped!");
                Bukkit.getLogger().severe("Found opened Shulker while not having a shulker open!");
            }
            return false;
        }

        if (itemStack == null) {
            Bukkit.getLogger().severe("Player " + player.getName() + " (" + player.getUniqueId() + ") may have duped!");
            Bukkit.getLogger().severe("Currently viewing a shulker while not having an opened shulker!");
            return false;
        }

        return true;
    }

    public boolean tryOpenShulkerBox(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        return this.tryOpenShulkerBox(player, itemStack);
    }

    public boolean tryOpenShulkerBox(Player player, ItemStack itemStack) {
        if (itemStack.getAmount() <= 0)
            return false;

        if (itemStack.getAmount() > 1)
            return false;

        if (!itemStack.getType().name().contains(Material.SHULKER_BOX.name()))
            return false;

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta))
            return false;

        if (!(blockStateMeta.getBlockState() instanceof ShulkerBox shulker))
            return false;

        ItemMeta meta = itemStack.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(this.openShulkerKey, PersistentDataType.BYTE, (byte) 1);

        itemStack.setItemMeta(meta);

        player.openInventory(shulker.getInventory());

        return true;
    }
}
