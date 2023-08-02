package me.entity303.openshulker.commands;

import me.entity303.openshulker.OpenShulker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenShulkerCommand implements TabExecutor {
    private final OpenShulker openShulker;

    public OpenShulkerCommand(OpenShulker openShulker) {
        this.openShulker = openShulker;
    }

    //TODO: Add messages.yml for translations
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!commandSender.hasPermission("openshulker.admin"))
            return true;

        if (args.length <= 0) {
            commandSender.sendMessage("§cSyntax: §4/" + label + " reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            this.openShulker.reloadConfig();
            commandSender.sendMessage("§2Reloaded!");
            return true;
        }

        commandSender.sendMessage("§cSyntax: §4/" + label + " reload");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) return Collections.singletonList("reload");

        return new ArrayList<>();
    }
}
