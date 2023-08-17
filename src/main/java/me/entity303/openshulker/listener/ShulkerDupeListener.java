package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShulkerDupeListener implements Listener {
    private final OpenShulker openShulker;

    public ShulkerDupeListener(OpenShulker openShulker) {
        this.openShulker = openShulker;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        this.handleEvent(e, e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        this.handleEvent(e, e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        this.handleEvent(e, e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        this.handleEvent(e, e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (!this.handleEvent(e, (Player) e.getWhoClicked()))
            return;

        e.setResult(Event.Result.DENY);
    }

    @EventHandler(ignoreCancelled = true)
    public void onContainerBreak(BlockBreakEvent e) {
        if (!(e.getBlock().getState() instanceof Container))
            return;

        Container container = (Container) e.getBlock().getState();

        ItemStack shulkerBox = this.openShulker.getShulkerActions().searchShulkerBox(container.getInventory());

        if (shulkerBox == null)
            return;

        e.setCancelled(true);

        String prefix = ChatColor.translateAlternateColorCodes('&', this.openShulker.getConfig().getString("Messages.Prefix"));

        e.getPlayer().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', this.openShulker.getConfig().getString("Messages.CannotBreakContainer")));
    }

    @EventHandler(ignoreCancelled = true)
    public void onContainerBreak(BlockDropItemEvent e) {
        if (!(e.getBlock().getState() instanceof Container))
            return;

        Container container = (Container) e.getBlock().getState();

        ItemStack shulkerBox = this.openShulker.getShulkerActions().searchShulkerBox(container.getInventory());

        if (shulkerBox == null)
            return;

        e.setCancelled(true);

        String prefix = ChatColor.translateAlternateColorCodes('&', this.openShulker.getConfig().getString("Messages.Prefix"));

        e.getPlayer().sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', this.openShulker.getConfig().getString("Messages.CannotBreakContainer")));
    }

    @EventHandler(ignoreCancelled = true)
    public void onContainerBreak(BlockExplodeEvent e) {
        this.removeContainersWithOpenShulkers(e.blockList());
    }

    @EventHandler(ignoreCancelled = true)
    public void onContainerBreak(EntityExplodeEvent e) {
        this.removeContainersWithOpenShulkers(e.blockList());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        int slot = e.getSlot();

        if (e.getClickedInventory() == null)
            return;

        ItemStack clickedItemStack = e.getClickedInventory().getItem(slot);

        if (clickedItemStack == null)
            return;

        if (clickedItemStack.getType() == Material.AIR)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getWhoClicked()))
            return;

        if (!this.openShulker.getShulkerActions().isOpenShulker(clickedItemStack))
            return;

        if (e.isRightClick() && e.isShiftClick())
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick2(InventoryClickEvent e) {
        int slot = e.getSlot();

        if (e.getClickedInventory() == null)
            return;

        ItemStack clickedItemStack = e.getClickedInventory().getItem(slot);

        if (clickedItemStack == null)
            return;

        if (clickedItemStack.getType() == Material.AIR)
            return;

        if (this.openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getWhoClicked()))
            return;

        if (!this.openShulker.getShulkerActions().isOpenShulker(clickedItemStack))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        if (!this.openShulker.getShulkerActions().isOpenShulker(e.getItem()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent e) {
        if (!this.openShulker.getShulkerActions().isOpenShulker(e.getItem()))
            return;

        e.setCancelled(true);
    }

    private boolean handleEvent(Cancellable cancellableEvent, Player player) {
        if (player.getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return false;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(player))
            return false;

        cancellableEvent.setCancelled(true);

        player.openInventory(player.getOpenInventory());
        return true;
    }

    private void removeContainersWithOpenShulkers(List<Block> blockList) {
        for (Block block : new ArrayList<>(blockList)) {
            if (!(block.getState() instanceof Container))
                continue;

            Container container = (Container) block.getState();

            ItemStack shulkerBox = this.openShulker.getShulkerActions().searchShulkerBox(container.getInventory());

            if (shulkerBox == null)
                continue;

            blockList.remove(block);
        }
    }
}
