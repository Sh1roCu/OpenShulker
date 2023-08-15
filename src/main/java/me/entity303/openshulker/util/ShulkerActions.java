package me.entity303.openshulker.util;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
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
    private final NamespacedKey openShulkerLocationKey;
    private OpenShulker openShulker;

    public ShulkerActions(OpenShulker openShulker) {
        this.openShulker = openShulker;
        this.openShulkerKey = new NamespacedKey(this.openShulker, "openshulker");
        this.openShulkerLocationKey = new NamespacedKey(this.openShulker, "openshulkerlocation");
    }

    public ItemStack searchShulkerBox(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (this.hasOpenShulkerInEnderChest(player))
            return this.searchShulkerBox(player.getEnderChest(), player);

        if (dataContainer.has(this.openShulkerLocationKey)) {
            Container container = this.getShulkerHoldingContainer(player);

            return this.searchShulkerBox(container.getInventory(), player);
        }

        return this.searchShulkerBox(player.getInventory());
    }

    public boolean hasOpenShulkerInEnderChest(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        return dataContainer.has(this.openShulkerLocationKey, PersistentDataType.BYTE);
    }

    public Container getShulkerHoldingContainer(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        if (!dataContainer.has(this.openShulkerLocationKey))
            return null;

        if (dataContainer.has(this.openShulkerLocationKey, PersistentDataType.BYTE))
            return null;

        String locationString = dataContainer.get(this.openShulkerLocationKey, PersistentDataType.STRING);
        String[] locationStringArray = locationString.split(";");

        double x = Double.parseDouble(locationStringArray[0]);
        double y = Double.parseDouble(locationStringArray[1]);
        double z = Double.parseDouble(locationStringArray[2]);

        World world = Bukkit.getWorld(locationStringArray[3]);

        Location location = new Location(world, x, y, z);

        Block block = location.getBlock();

        if (!(block.getState() instanceof Container container))
            return null;

        return container;
    }

    public ItemStack searchShulkerBox(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
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

    public ItemStack searchShulkerBox(Inventory inventory, Player player) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == Material.AIR)
                continue;

            if (!this.isOpenShulker(itemStack, player))
                continue;

            return itemStack;
        }

        return null;
    }

    public boolean isOpenShulker(ItemStack itemStack, Player player) {
        ItemMeta meta = itemStack.getItemMeta();

        if (!(itemStack.getItemMeta() instanceof BlockStateMeta))
            return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(this.openShulkerKey, PersistentDataType.STRING))
            return false;

        if (player == null)
            return true;

        String uniqueId = container.get(this.openShulkerKey, PersistentDataType.STRING);

        if (uniqueId == null)
            return false;

        if (!uniqueId.equalsIgnoreCase(player.getUniqueId().toString()))
            return false;

        return true;
    }

    public boolean isOpenShulker(ItemStack itemStack) {
        return this.isOpenShulker(itemStack, null);
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

        container.remove(this.openShulkerLocationKey);

        try {
            player.playSound(player, Sound.valueOf(this.openShulker.getConfig().getString("CloseSound")), 1F, 1F);
        } catch (Throwable ignored) {
            //Ignore the exception, it's probably just a message about not being able to find the correct sound
            //We have an info message in OpenShulker#onEnable for this
        }
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

    public boolean attemptToOpenShulkerBox(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        return this.attemptToOpenShulkerBox(player, itemStack);
    }

    public boolean attemptToOpenShulkerBox(Player player, ItemStack itemStack, Location chest) {
        Block block = chest.getBlock();

        if (!(block.getState() instanceof Container))
            return false;

        boolean open = this.attemptToOpenShulkerBox(player, itemStack);

        if (!open)
            return false;

        PersistentDataContainer container = player.getPersistentDataContainer();

        container.set(this.openShulkerLocationKey, PersistentDataType.STRING, chest.getX() + ";" + chest.getY() + ";" + chest.getZ() + ";" + chest.getWorld().getName());
        return true;
    }

    public boolean attemptToOpenShulkerBox(Player player, ItemStack itemStack, boolean enderChest) {
        boolean open = this.attemptToOpenShulkerBox(player, itemStack);

        if (!open)
            return false;

        if (!enderChest)
            return true;

        PersistentDataContainer container = player.getPersistentDataContainer();

        container.set(this.openShulkerLocationKey, PersistentDataType.BYTE, (byte) 1);
        return true;
    }

    public boolean attemptToOpenShulkerBox(Player player, ItemStack itemStack) {
        if (!player.hasPermission("openshulker.use"))
            return false;

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

        if (container.has(this.openShulkerKey, PersistentDataType.STRING))
            return false;

        container.set(this.openShulkerKey, PersistentDataType.STRING, player.getUniqueId().toString());

        itemStack.setItemMeta(meta);

        container = player.getPersistentDataContainer();

        container.set(this.openShulkerKey, PersistentDataType.STRING, player.getUniqueId().toString());

        player.openInventory(shulker.getInventory());

        try {
            player.playSound(player, Sound.valueOf(this.openShulker.getConfig().getString("OpenSound")), 1F, 1F);
        } catch (Throwable ignored) {
            //Ignore the exception, it's probably just a message about not being able to find the correct sound
            //We have an info message in OpenShulker#onEnable for this
        }
        return true;
    }
}
