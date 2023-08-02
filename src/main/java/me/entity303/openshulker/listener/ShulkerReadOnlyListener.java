package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class ShulkerReadOnlyListener implements Listener {
    private final OpenShulker openShulker;

    public ShulkerReadOnlyListener(OpenShulker openShulker) {
        this.openShulker = openShulker;
    }

    @EventHandler
    public void onShulkerInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == e.getWhoClicked().getInventory())
            return;

        if (e.getClickedInventory().getType() != InventoryType.SHULKER_BOX)
            return;

        if (!openShulker.getShulkerActions().hasOpenShulkerBox((Player) e.getWhoClicked()))
            return;

        if (e.getWhoClicked().hasPermission("openshulker.write"))
            return;

        e.setCancelled(true);
    }
}
