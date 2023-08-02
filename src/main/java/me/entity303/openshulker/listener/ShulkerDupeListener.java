package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ShulkerDupeListener implements Listener {
    private final OpenShulker openShulker;

    public ShulkerDupeListener(OpenShulker openShulker) {
        this.openShulker = openShulker;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        e.setCancelled(true);

        e.getPlayer().openInventory(e.getPlayer().getOpenInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        e.setCancelled(true);

        e.getPlayer().openInventory(e.getPlayer().getOpenInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getPlayer().getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        e.setCancelled(true);

        e.getPlayer().openInventory(e.getPlayer().getOpenInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox(e.getPlayer()))
            return;

        e.setCancelled(true);

        e.getPlayer().openInventory(e.getPlayer().getOpenInventory());
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (e.getWhoClicked().getOpenInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!this.openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getWhoClicked()))
            return;

        e.setCancelled(true);

        e.setResult(Event.Result.DENY);

        e.getWhoClicked().openInventory(e.getWhoClicked().getOpenInventory());
    }
}
