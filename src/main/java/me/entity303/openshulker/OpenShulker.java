package me.entity303.openshulker;

import me.entity303.openshulker.util.ShulkerActions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpenShulker extends JavaPlugin implements Listener {
    private ShulkerActions shulkerActions;

    @Override
    public void onEnable() {
        this.shulkerActions = new ShulkerActions(this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!this.shulkerActions.hasOpenShulkerBox(all))
                continue;

            ItemStack shulkerBox = this.shulkerActions.searchShulkerBox(all);

            if (shulkerBox == null)
                continue;

            this.shulkerActions.saveShulkerBox(shulkerBox, all.getOpenInventory().getTopInventory(), all);

            all.closeInventory();
        }
    }

    public ShulkerActions getShulkerActions() {
        return this.shulkerActions;
    }
}
