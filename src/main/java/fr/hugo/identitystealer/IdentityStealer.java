package fr.hugo.identitystealer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§cUsage: /steal <pseudo>");
            return true;
        }

        String targetName = args[0];

        // 1. Changement du skin via SkinsRestorer
        player.performCommand("skinsrestorer set " + targetName);

        // 2. Rafraîchissement visuel pour que les autres voient le changement
        for (Player online : player.getServer().getOnlinePlayers()) {
            if (online != player) {
                online.hidePlayer(player);
                // Petit délai pour laisser le temps au serveur de traiter le changement
                player.getServer().getScheduler().runTaskLater(
                    org.bukkit.plugin.java.JavaPlugin.getPlugin(IdentityStealer.class), 
                    () -> online.showPlayer(player), 20L
                );
            }
        }

        player.sendMessage("§aIdentité de " + targetName + " volée avec succès !");
        return true;
    }
}
