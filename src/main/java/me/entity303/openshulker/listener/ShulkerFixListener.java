package me.entity303.openshulker.listener;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ShulkerFixListener implements Listener {
    private final NamespacedKey openShulkerKey;
    private final NamespacedKey openShulkerLocationKey;
    private final OpenShulker openShulker;

    public ShulkerFixListener(OpenShulker openShulker) {
        this.openShulker = openShulker;
        this.openShulkerKey = new NamespacedKey(this.openShulker, "openshulker");
        this.openShulkerLocationKey = new NamespacedKey(this.openShulker, "openshulkerlocation");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void resetOpenShulkerDataContainer(PlayerJoinEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack != null && itemStack.getType().name().contains(Material.SHULKER_BOX.name())) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
                PersistentDataContainer container = blockStateMeta.getPersistentDataContainer();
                container.remove(this.openShulkerKey);
                itemStack.setItemMeta(blockStateMeta);
            }
        }
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();
        container.remove(this.openShulkerKey);
        container.remove(this.openShulkerLocationKey);
    }
}
