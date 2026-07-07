package fr.hugo.identitystealer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public class StealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Vérifie si celui qui envoie la commande est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        // Vérifie si un pseudo a bien été donné
        if (args.length < 1) {
            player.sendMessage("§cUsage: /steal <pseudo>");
            return true;
        }

        String targetName = args[0];
        
        // 1. Création de la tête avec le skin du joueur ciblé
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        // Création du profil pour appliquer le skin
        PlayerProfile profile = Bukkit.createPlayerProfile(targetName);
        meta.setOwnerProfile(profile);
        meta.setDisplayName("§dMasque de " + targetName);
        head.setItemMeta(meta);
        
        // Ajout de la tête à l'inventaire
        player.getInventory().addItem(head);

        // 2. Utilisation de NametagEditX pour changer le nom au-dessus de la tête
        // On demande à la console d'exécuter la commande pour le joueur
        String command = "ne player " + player.getName() + " prefix " + targetName;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        player.sendMessage("§aTu as pris l'apparence de " + targetName + " !");

        return true;
    }
}
