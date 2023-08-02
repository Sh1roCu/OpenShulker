package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
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

        e.setCancelled(this.openShulker.getShulkerActions().tryOpenShulkerBox(e.getPlayer()));
    }

    @EventHandler
    public void onShulkerOpenAlternative(InventoryClickEvent e) {
        if (!e.isRightClick())
            return;

        if (!e.isShiftClick())
            return;

        e.setCancelled(this.openShulker.getShulkerActions().tryOpenShulkerBox((Player) e.getWhoClicked(), e.getCurrentItem()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onShulkerItemDrop(PlayerDropItemEvent e) {
        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        ItemStack shulkerBox = e.getItemDrop().getItemStack();

        if (!this.openShulker.getShulkerActions().isOpenShulker(shulkerBox))
            return;

        this.openShulker.getShulkerActions().saveShulkerBox(shulkerBox, e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer());

        e.getPlayer().closeInventory();
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

        this.openShulker.getShulkerActions().saveShulkerBox(itemStack, e.getInventory(), (Player) e.getPlayer());
    }
}
