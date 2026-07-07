package fr.hugo.identitystealer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /steal <pseudo>");
            return true;
        }

        Player player = (Player) sender;
        String targetName = args[0];

        // 1. Application du skin
        player.performCommand("skinsrestorer set " + targetName);

        // 2. Rafraîchissement visuel pour les autres (sans boucle complexe)
        player.sendMessage("§aVol d'identité en cours pour : " + targetName);
        
        // On rafraîchit le joueur pour tout le monde proprement
        player.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            onlinePlayer.hidePlayer(org.bukkit.plugin.java.JavaPlugin.getPlugin(IdentityStealer.class), player);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    onlinePlayer.showPlayer(org.bukkit.plugin.java.JavaPlugin.getPlugin(IdentityStealer.class), player);
                }
            }.runTaskLater(org.bukkit.plugin.java.JavaPlugin.getPlugin(IdentityStealer.class), 20L);
        });

        return true;
    }
}
