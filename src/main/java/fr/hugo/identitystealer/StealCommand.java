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

        // 1. Enregistrer le vol pour le chat
        IdentityListener.stolenIdentities.put(player.getUniqueId(), targetName);

        // 2. Commande pour changer le skin (ex: via le plugin SkinsRestorer ou autre)
        // Remplace "skin set" par la commande exacte de ton plugin de skin
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + targetName);

        // 3. Commande pour le nom (NametagEditX)
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ne clear " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ne " + player.getName() + " " + targetName);

        // 4. Donner la tête
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(targetName);
        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);
        player.getInventory().addItem(head);

        player.sendMessage("§aTu as pris l'identité de " + targetName);
        return true;
    }
}
