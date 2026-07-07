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
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§cUsage: /steal <pseudo>");
            return true;
        }

        String targetName = args[0];

        // Création de la tête
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(targetName);
        meta.setOwnerProfile(profile);
        meta.setDisplayName("§dMasque de " + targetName);
        head.setItemMeta(meta);
        player.getInventory().addItem(head);

        // Application du nom via NametagEditX
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ne player " + player.getName() + " prefix " + targetName);

        player.sendMessage("§aTu as pris l'apparence de " + targetName + " !");
        return true;
    }
}
