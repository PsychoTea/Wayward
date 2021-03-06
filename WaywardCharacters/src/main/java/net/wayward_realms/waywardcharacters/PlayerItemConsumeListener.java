package net.wayward_realms.waywardcharacters;

import net.wayward_realms.waywardlib.character.Character;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener implements Listener {

    private WaywardCharacters plugin;

    public PlayerItemConsumeListener(WaywardCharacters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
        	Player player = event.getPlayer();
        	Character character = plugin.getActiveCharacter(player);
        	character.setThirst(character.getThirst() + 2);
            if (event.getItem().hasItemMeta()) {	
                if (event.getItem().getItemMeta().hasDisplayName()) {
                    if (event.getItem().getItemMeta().getDisplayName().equals("Masheek")) {
                        if (event.getItem().getItemMeta().hasLore()) {
                            if (event.getItem().getItemMeta().getLore().contains("+5 mana")) {
                                character.setMana(Math.min(character.getMana() + 5, character.getMaxMana()));
                                event.getPlayer().sendMessage(plugin.getPrefix() + ChatColor.GREEN + "+5 mana" + ChatColor.GRAY + " (" + character.getMana() + "/" + character.getMaxMana() + ")");
                            }
                        }
                    }
                }
            }
        }
    }

}