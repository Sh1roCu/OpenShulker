package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShulkerOpenCloseListener implements Listener {
    private final OpenShulker openShulker;

    public ShulkerOpenCloseListener(OpenShulker openShulker) {
        this.openShulker = openShulker;
    }

    @EventHandler
    public void onShulkerOpen(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!e.getPlayer().isSneaking())
            return;

        e.setCancelled(this.openShulker.getShulkerActions().attemptToOpenShulkerBox(e.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onShulkerOpenAlternative(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;

        int clickedSlot = e.getSlot();

        ItemStack clickedItemStack = e.getClickedInventory().getItem(clickedSlot);

        if (clickedItemStack == null)
            return;

        if (clickedItemStack.getType() == Material.AIR)
            return;

        if (!e.isRightClick())
            return;

        if (!e.isShiftClick())
            return;

        if (e.getClickedInventory() == e.getWhoClicked().getInventory())
            if (clickedItemStack.getType().name().contains(Material.SHULKER_BOX.name()))
                if (e.getView().getTopInventory().getType() == InventoryType.SHULKER_BOX)
                    if (this.openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getWhoClicked())) {
                        ItemStack shulkerBox = this.openShulker.getShulkerActions().searchShulkerBox((Player) e.getWhoClicked());

                        this.openShulker.getShulkerActions().saveShulkerBox(shulkerBox, e.getView().getTopInventory(), (Player) e.getWhoClicked());
                        e.getWhoClicked().closeInventory();
                    }

        Location location = null;
        if (e.getClickedInventory() != e.getWhoClicked().getInventory())
            location = e.getClickedInventory().getLocation();

        if (location == null)
            e.setCancelled(this.openShulker.getShulkerActions().attemptToOpenShulkerBox((Player) e.getWhoClicked(), clickedItemStack));
        else
            e.setCancelled(this.openShulker.getShulkerActions().attemptToOpenShulkerBox((Player) e.getWhoClicked(), clickedItemStack, location));
    }

    @EventHandler(ignoreCancelled = true)
    public void onShulkerItemDrop(PlayerDropItemEvent e) {
        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        ItemStack shulkerBox = e.getItemDrop().getItemStack();

        if (!this.openShulker.getShulkerActions().isOpenShulker(shulkerBox, e.getPlayer()))
            return;

        Container container = this.openShulker.getShulkerActions().getShulkerHoldingContainer(e.getPlayer());

        this.openShulker.getShulkerActions().saveShulkerBox(shulkerBox, e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer());

        e.getPlayer().closeInventory();

        Bukkit.getScheduler().runTaskLater(this.openShulker, () -> {
            if (container != null) {
                if (container.getWorld() != e.getPlayer().getWorld())
                    return;

                if (container.getLocation().distance(e.getPlayer().getLocation()) > 4)
                    return;

                e.getPlayer().openInventory(container.getInventory());
            }
        }, 1L);
    }

    @EventHandler
    public void onShulkerInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getPlayer()))
            return;

        ItemStack itemStack = this.openShulker.getShulkerActions().searchShulkerBox((Player) e.getPlayer());

        if (itemStack == null)
            return;

        Container container = this.openShulker.getShulkerActions().getShulkerHoldingContainer((Player) e.getPlayer());

        this.openShulker.getShulkerActions().saveShulkerBox(itemStack, e.getInventory(), (Player) e.getPlayer());

        e.getPlayer().closeInventory();

        Bukkit.getScheduler().runTaskLater(this.openShulker, () -> {
            if (container != null) {
                if (container.getWorld() != e.getPlayer().getWorld())
                    return;

                if (container.getLocation().distance(e.getPlayer().getLocation()) > 4)
                    return;

                e.getPlayer().openInventory(container.getInventory());
            }
        }, 1L);
    }
}
