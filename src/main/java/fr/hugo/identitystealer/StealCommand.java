package fr.hugo.identitystealer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /steal <pseudo>");
            return true;
        }

        Player player = (Player) sender;
        String targetName = args[0];

        // 1. Application du skin via SkinsRestorer
        player.performCommand("skinsrestorer set " + targetName);
        
        // 2. Application du nom via NametagEditX (Ton système)
        player.performCommand("nte player " + player.getName() + " clear");
        player.performCommand("nte player " + player.getName() + " prefix " + targetName);

        // 3. Rafraîchissement visuel pour les autres (L'étape cruciale)
        // On cache le joueur puis on le montre à tout le monde
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player) {
                onlinePlayer.hidePlayer(JavaPlugin.getPlugin(IdentityStealer.class), player);
            }
        }

        // On attend 1 seconde (20 ticks) pour laisser le temps à SkinsRestorer de charger
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(IdentityStealer.class), () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer != player) {
                    onlinePlayer.showPlayer(JavaPlugin.getPlugin(IdentityStealer.class), player);
                }
            }
            player.updateInventory(); // Force le client à rafraîchir les données
            player.sendMessage("§aIdentité de " + targetName + " volée avec succès !");
        }, 20L);

        return true;
    }
}
