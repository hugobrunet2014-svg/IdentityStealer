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

        String target = args[0];

        // 1. Skin : on utilise ta commande standard
        player.performCommand("skin set " + target);

        // 2. Nom : on nettoie d'abord, puis on applique le préfixe
        // Note : Nte ajoute un préfixe. Pour "voler" l'identité, on met le nom en préfixe.
        player.performCommand("nte player " + player.getName() + " clear");
        
        // On définit le nom de la victime comme préfixe (en ajoutant un espace pour la lisibilité)
        player.performCommand("nte player " + player.getName() + " prefix " + target);

        player.sendMessage("§aIdentité de " + target + " volée !");
        
        return true;
    }
}
