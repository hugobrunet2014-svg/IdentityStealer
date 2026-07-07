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
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) return true;

        String targetName = args[0];

        // 1. Donner la tête
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(targetName);
        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);
        player.getInventory().addItem(head);

        // 2. CORRECTION DU NOM (NametagEditX)
        // Au lieu de mettre un préfixe, on va essayer de définir le nom affiché 
        // NametagEditX utilise souvent cette syntaxe pour changer le nom complet
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ne clear " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ne " + player.getName() + " " + targetName);

        player.sendMessage("§aTu as pris l'identité de " + targetName);
        return true;
    }
}
