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

        // On exécute les commandes comme si le joueur les tapait lui-même
        // Cela permet aux plugins de skin et de nom de fonctionner correctement sur lui
        player.performCommand("skin set " + targetName);
        player.performCommand("ne " + player.getName() + " " + targetName);

        player.sendMessage("§aTu as pris l'identité de " + targetName);
        return true;
    }
}
